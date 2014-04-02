package osmo.tester.explorer;

import osmo.common.log.Logger;
import osmo.tester.explorer.trace.TraceNode;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Explores one path up to the given depth, starting other instances of this class for sub-paths.
 *
 * @author Teemu Kanstren
 */
public class PathExplorer extends RecursiveTask<List<TestCase>> {
  private static final Logger log = new Logger(PathExplorer.class);
  /** Common parameters for exploration. */
  private final ExplorationState state;
  /** The depth of exploration for this explorer. */
  private final int depth;
  /** The representation of the exploration trace. */
  private final TraceNode trace;
  /** The exploration targets from the current exploration start state. */
  private final Collection<FSMTransition> targets;
  /** The path to the current start of exploration for the current test case. */
  private final List<String> script;
  /** The thread pool used to fork new threads for sub-paths. */
  private final ForkJoinPool pool;
  /** Used to check if a path exploration should end before depth is reached. */
  private final ExplorationEndCondition endCondition;

  public PathExplorer(ExplorationState state, int depth, TraceNode trace, Collection<FSMTransition> targets,
                      List<String> script, ForkJoinPool pool) {
    this.state = state;
    this.depth = depth;
    this.trace = trace;
    this.targets = targets;
    this.script = script;
    this.pool = pool;
    //we need a separate copy of the end condition as it needs to be set into exploration mode
    this.endCondition = new ExplorationEndCondition(state.getConfig(), state.getSuiteCoverage(), true);
    endCondition.init(state.getConfig().getSeed(), null, null);
  }

  /**
   * Explores the configured model, suite, and test to the given depth.
   * This is where the recursion happens.
   */
  @Override
  protected List<TestCase> compute() {
//    log.debug("Starting path exploration");
    Collection<String> toExplore = new ArrayList<>();
    for (FSMTransition transition : targets) {
      toExplore.add(transition.getStringName());
    }
    Collection<PathExplorer> children = new ArrayList<>();
    List<TestCase> result = new ArrayList<>();
    for (String explore : toExplore) {
      TraceNode child = trace.add(explore, true);
      MainGenerator generator = ExplorationHelper.initPath(state, script);
      initPath(explore, generator);
      int newDepth = checkDepth(state, endCondition, generator, depth);
      if (newDepth > 0) {
        PathExplorer explorer = forkExplorer(explore, child, generator, newDepth);
        children.add(explorer);
      } else {
        //we only come here once there is no depth left to go deeper. thus the end result is that the set of 
        //concrete test cases returned is a set of the deepest test cases according to the original configuration
        TestCase test = generator.getCurrentTest();
        result.add(test);
      }
    }
    for (PathExplorer child : children) {
      Collection<TestCase> tests = child.join();
      result.addAll(tests);
    }
    return result;
  }

  private PathExplorer forkExplorer(String explore, TraceNode child, MainGenerator generator, int newDepth) {
    List<String> newScript = new ArrayList<>();
    newScript.addAll(script);
    newScript.add(explore);
    //here we explore further into the depths of the "tree", but do not save the "testcase" so far as it is just
    //a temporary test on the way further in the depths of the "tree"
    List<FSMTransition> nowEnabled = generator.getEnabled();
    PathExplorer explorer = new PathExplorer(state, newDepth - 1, child, nowEnabled, newScript, pool);
    explorer.fork();
    return explorer;
  }

  private void initPath(String explore, MainGenerator generator) {
    List<FSMTransition> enabled = generator.getEnabled();
    for (FSMTransition transition : enabled) {
      if (transition.getStringName().equals(explore)) {
        generator.execute(transition);
        break;
      }
    }
  }

  /**
   * Check how deep we can actually go on the given path.
   * Checking happens through the defined configuration in the {@link ExplorationEndCondition}.
   * Static to allow cloud classes to access this without an instance.
   *
   * @param toCheck The depth to check.
   * @return 0 if the end condition wants to stop, otherwise the given parameter value.
   */
  public static int checkDepth(ExplorationState state, ExplorationEndCondition ec, MainGenerator generator, int toCheck) {
    ExplorationConfiguration config = state.getConfig();
    int maxLength = config.getMaxTestLength();
    TestSuite suite = generator.getSuite();
    int length = suite.getCurrentTest().getAllStepNames().size();
    if (maxLength > 0) {
      int maxDepth = maxLength - length;
      if (maxDepth < toCheck) {
        toCheck = maxDepth;
      }
    }
    if (ec.endTest(suite, null)) {
      //this cannot be exactly defined since we do not know the score of the following steps before exploring them
      //for this reason, we just check if the test should end or not
      log.debug("Depth trimmed due to end condition wanting to stop");
      return 0;
    }
    Collection<InvocationTarget> fsmECs = generator.getFsm().getEndConditions();
    for (InvocationTarget fsmEC : fsmECs) {
      Boolean result = (Boolean) fsmEC.invoke();
      if (result) {
        log.debug("model @EndCondition signalled to stop");
        return 0;
      }
    }
    return toCheck;
  }

}

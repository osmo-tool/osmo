package osmo.tester.explorer;

import osmo.common.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.explorer.trace.DOTWriter;
import osmo.tester.explorer.trace.TimeTrace;
import osmo.tester.explorer.trace.TraceNode;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * The OSMO Tester test generation algorithm that explores the set of available test step options up to given depth
 * and chooses the next step based on which path gives the best score.
 *
 * @author Teemu Kanstren
 */
public class ExplorerAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(ExplorerAlgorithm.class);
  /** Defines the configuration to use in exploration. */
  private final ExplorationConfiguration config;
  /** This is used to tell OSMO Tester when to stop test generation, and the explorer when exploration is finished. */
  private final ExplorationEndCondition explorationEndCondition;
  /** Used to write a graphical trace for all explored tests and their steps. */
  private DOTWriter dot = new DOTWriter(1);
  /** Currently running explorer instance, possibly started at end of last iteration. */
  private MainExplorer currentExplorer = null;
  /** For collecting possible coverage metrics. */
  private static final Collection<String> possibleStepPairs = new LinkedHashSet<>();
  /** For collecting possible coverage metrics. Variable values. */
  private static final Map<String, Collection<String>> possibleValues = new LinkedHashMap<>();
  /** For collecting possible coverage metrics. User defined state coverage values. */
  private static final Map<String, Collection<String>> possibleStates = new LinkedHashMap<>();
  /** For collecting possible coverage metrics. User defined state coverage value pairs. */
  private static final Map<String, Collection<String>> possibleStatePairs = new LinkedHashMap<>();
  /** The model structure. */
  private FSM fsm = null;
  /** Tracks how much time each step took to explore, which step it was and what was chosen as next candidate. */
  private Collection<TimeTrace> traces = new ArrayList<>();
  /** If true, information on possible to reach coverage is maintained. */
  public static boolean trackCoverage = false;

  public ExplorerAlgorithm(ExplorationConfiguration config) {
    this.config = config;
    this.explorationEndCondition = config.getExplorationEndCondition();
  }

  @Override
  public void init(long seed, FSM fsm) {
    //wipe out previous run's trace
    DOTWriter.deleteFiles();
    this.fsm = fsm;
    //we initialize the fallback algorithm
    config.getFallback(seed, fsm);
  }

  public Collection<TimeTrace> getTraces() {
    return traces;
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    int testIndex = suite.getAllTestCases().size();
    List<String> path = suite.getCurrentTest().getAllStepNames();
    log.d("path for the current (explored) test:" + path);
    log.i("Exploring step " + testIndex + "." + path.size());
    TestCoverage suiteCoverage = suite.getCoverage();
    //create trace if DOT graph is wanted
    TraceNode[] trace = initTrace(path, testIndex);
    ExplorationState state = new ExplorationState(config, suiteCoverage);
    String choice = null;
    if (choices.size() == 1) {
      //this handles the scenario startup, where there is always just one choice (and other similar scenarios)
      choice = choices.get(0).getStringName();
      path.add(choice);
    } else {
      //initiate exploration of the possible paths
      choice = exploreLocal(suite, state, path, trace[1]);
    }
    writeDot(path, trace[0]);
    //we have to find the transition here based on string matching because returning the actual FSMTransition
    //object would give a reference to the wrong instance of the model object
    for (FSMTransition transition : choices) {
      if (transition.getStringName().equals(choice)) {
        return transition;
      }
    }
    throw new IllegalStateException("Exploration choice (" + choice + ") not available. OSMO is bugging?");
  }

  /**
   * Writes the DOT trace for this step, if so desired.
   * 
   * @param path       The path before exploration was started.
   * @param traceRoot  Root of trace description, the first step in test.
   */
  private void writeDot(List<String> path, TraceNode traceRoot) {
    String top = "root";
    if (path.size() > 0) {
      top = path.get(path.size() - 1);
    }
    dot.write(traceRoot, config.getDepth()-1, top);
  }

  @Override
  public void initTest(long seed) {
    if (currentExplorer != null) {
      currentExplorer.stop();
    }
    currentExplorer = null;
  }

  /**
   * Explores the next step locally. Local refers to the same machine, at one point we had distributed version as well.
   * If no explorer is currently running, starts a new one and waits for the results. 
   * If one is already running, waits for the results from that explorer.
   * A new explorer is started once the selection is made to already search for the next step concurrently while
   * the generator is executing the previous one.
   * 
   * @param suite  Current test suite.
   * @param state  State to use for exploration, includes exploration configuration and initial suite coverage.
   * @param path   Path executed so far for this test before exploring for this step.
   * @param root   We use this to write the trace.
   * @return The next step to take according to exploration results.
   */
  private String exploreLocal(TestSuite suite, ExplorationState state, List<String> path, TraceNode root) {
    if (currentExplorer == null) {
      currentExplorer = new MainExplorer(root);
      currentExplorer.init(fsm, suite, state, path, config.getParallelism());
      currentExplorer.explore();
    }
    //get the choice of next step based on best exploration score
    //the exploration was already started at the end of previous session..
    String result = currentExplorer.getResult();
    int test = suite.getAllTestCases().size();
    int step = suite.getCurrentTest().getSteps().size()+1;
    //update time trace to track how much time each step takes to explore. for providing statistics to user.
    TimeTrace trace = new TimeTrace(test, step, result, currentExplorer.getDuration());
    traces.add(trace);

    //tracking coverage means we want to trace all possible options that could have been taken
    if (trackCoverage) {
      //grab all possible values from the explorer and add to list of unique
      Map<String, Collection<String>> observed = currentExplorer.getPossibleVariableValues();
      for (String key : observed.keySet()) {
        Collection<String> possibles = possibleValues.get(key);
        if (possibles == null) {
          possibles = new LinkedHashSet<>();
          possibleValues.put(key, possibles);
        }
        possibles.addAll(observed.get(key));
      }

      //grab all possible states from the explorer and add to list of unique
      observed = currentExplorer.getPossibleStates();
      for (String key : observed.keySet()) {
        Collection<String> possibles = possibleStates.get(key);
        if (possibles == null) {
          possibles = new LinkedHashSet<>();
          possibleStates.put(key, possibles);
        }
        possibles.addAll(observed.get(key));
      }

      //grab all possible state pairs from the explorer and add to list of unique
      observed = currentExplorer.getPossibleStatePairs();
      for (String key : observed.keySet()) {
        Collection<String> possibles = possibleStatePairs.get(key);
        if (possibles == null) {
          possibles = new LinkedHashSet<>();
          possibleStatePairs.put(key, possibles);
        }
        possibles.addAll(observed.get(key));
      }

      possibleStepPairs.addAll(currentExplorer.getPossibleStepPairs());
    }

    if (explorationEndCondition.endTest(suite, null)) {
      currentExplorer.stop();
      currentExplorer = null;
    } else {
      currentExplorer.stop();
      List<String> newScript = new ArrayList<>();
      newScript.addAll(path);
      newScript.add(result);
      currentExplorer = new MainExplorer(root);
      currentExplorer.init(fsm, suite, state, newScript, config.getParallelism());
      currentExplorer.explore();
    }

    return result;
  }
  
  /**
   * Creates the representation of the generation/exploration trace for using with DOT trace writing.
   *
   * @param script The script for the test case.
   * @return index[0] = root, index[1] = current step from which exploration continues.
   */
  private TraceNode[] initTrace(Collection<String> script, int testIndex) {
    if (dot.getTestIndex() != testIndex) {
      log.d("New test started, creating new DOT tracer.");
      dot = new DOTWriter(testIndex);
    }
    TraceNode.reset();
    TraceNode[] nodes = new TraceNode[2];
    TraceNode root = new TraceNode("init", null, false);
    nodes[0] = root;
    TraceNode node = root;
    for (String step : script) {
      node = node.add(step, false);
    }
    nodes[1] = node;
    return nodes;
  }

  public ExplorationEndCondition getExplorationEndCondition() {
    return explorationEndCondition;
  }

  public Collection<String> getPossibleStepPairs() {
    return possibleStepPairs;
  }

  public Map<String, Collection<String>> getPossibleValues() {
    return possibleValues;
  }

  public Map<String, Collection<String>> getPossibleStates() {
    return possibleStates;
  }

  public Map<String, Collection<String>> getPossibleStatePairs() {
    return possibleStatePairs;
  }

  @Override
  public FSMTraversalAlgorithm cloneMe() {
    ExplorerAlgorithm clone = new ExplorerAlgorithm(config);
    return clone;
  }
}


package osmo.tester.explorer;

import osmo.common.log.Logger;
import osmo.tester.explorer.trace.DOTWriter;
import osmo.tester.explorer.trace.TimeTrace;
import osmo.tester.explorer.trace.TraceNode;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.testsuite.TestCase;
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
  /** Defines the properties to use in exploration. */
  private final ExplorationConfiguration config;
  /** This is used to tell OSMO Tester when to stop test generation, when exploration is finished. */
  private final ExplorationEndCondition explorationEndCondition;
  /** Used to write a graphical trace for all explored tests and their steps. */
  private DOTWriter dot = new DOTWriter(1);
  /** Currently running explorer instance, possibly started at end of last iteration. */
  private MainExplorer currentExplorer = null;
  /** For collecting possible coverage metrics. */
  private static final Collection<String> possibleStepPairs = new LinkedHashSet<>();
  /** For collecting possible coverage metrics. */
  private static final Map<String, Collection<String>> possibleValues = new LinkedHashMap<>();
  /** For collecting possible coverage metrics. */
  private static final Map<String, Collection<String>> possibleStates = new LinkedHashMap<>();
  /** For collecting possible coverage metrics. */
  private static final Map<String, Collection<String>> possibleStatePairs = new LinkedHashMap<>();
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
    DOTWriter.deleteFiles();
    this.fsm = fsm;
    config.getFallback().init(seed, fsm);
  }

  public Collection<TimeTrace> getTraces() {
    return traces;
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    int testIndex = suite.getAllTestCases().size();
    log.debug("Exploring for test number " + testIndex);
    if (dot.getTestIndex() != testIndex) {
      log.debug("New test started, creating new DOT tracer.");
      dot = new DOTWriter(testIndex);
    }
    List<String> script = suite.getCurrentTest().getAllStepNames();
    log.debug("script for the current (explored) test:" + script);
    TestCoverage suiteCoverage = suite.getCoverage();
    //create trace if DOT graph is wanted
    TraceNode[] trace = initTrace(script);
    ExplorationState state = new ExplorationState(config, suiteCoverage);
    String choice = null;
    if (choices.size() == 1) {
      //this handles the scenario startup, where there is always just one choice (and other similar scenarios)
      choice = choices.get(0).getStringName();
      script.add(choice);
      MainGenerator generator = ExplorationHelper.initPath(state, script);
      explorationEndCondition.setExploredTest(generator.getCurrentTest());
    } else {
      //initiate exploration of the possible paths
      choice = exploreLocal(suite, state, script, trace[1]);
    }
    String top = "root";
    if (script.size() > 0) {
      top = script.get(script.size() - 1);
    }
    //write the DOT trace for this step
    dot.write(trace[0], config.getDepth()-1, top);
    //we have to find the transition here based on string matching because returning the actual FSMTransition
    //object would give a reference to the wrong instance of the model object
    for (FSMTransition transition : choices) {
      if (transition.getStringName().equals(choice)) {
        return transition;
      }
    }
    throw new IllegalStateException("Exploration choice (" + choice + ") not available. OSMO is bugging?");
  }

  @Override
  public void initTest() {
    explorationEndCondition.setExploredTest(null);
    if (currentExplorer != null) {
      currentExplorer.stop();
    }
    currentExplorer = null;
  }

  private String exploreLocal(TestSuite suite, ExplorationState state, List<String> script, TraceNode root) {
    if (currentExplorer == null) {
      currentExplorer = new MainExplorer(root);
      currentExplorer.init(fsm, suite, state, script, config.getParallelism());
      currentExplorer.explore();
    }
    //and get the choice of next step based on best exploration score
    //the exploration was already started at the end of previous session..
    String result = currentExplorer.getResult();
    int test = suite.getAllTestCases().size();
    int step = suite.getCurrentTest().getSteps().size()+1;
    TimeTrace trace = new TimeTrace(test, step, result, currentExplorer.getDuration());
    traces.add(trace);

    if (trackCoverage) {
      //grab all possible values from the explorer and add to list of unique
      Map<String, Collection<String>> observed = currentExplorer.getPossibleValues();
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

    //we have to keep track of exploration in the endcondition to be able to tell when to stop test generation
    //that is, to be able to identify a plateau
    TestCase winner = currentExplorer.getWinner();
    explorationEndCondition.setExploredTest(winner);

    if (explorationEndCondition.endTest(suite, null)) {
      currentExplorer.stop();
      currentExplorer = null;
    } else {
      currentExplorer.stop();
      List<String> newScript = new ArrayList<>();
      newScript.addAll(script);
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
  private TraceNode[] initTrace(Collection<String> script) {
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
}


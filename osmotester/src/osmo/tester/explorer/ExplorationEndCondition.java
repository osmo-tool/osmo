package osmo.tester.explorer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.scenario.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * An end condition for guiding OSMO Tester during test exploration.
 * Tells OSMO Tester to stop test generation when an explored test has reached suitable length or
 * the score has reached a plateau (not increasing over a threshold any more).
 * The same applies for a suite.
 *
 * @author Teemu Kanstren
 */
public class ExplorationEndCondition implements EndCondition {
  private static final Logger log = new Logger(ExplorationEndCondition.class);
  /** The configuration used to calculate coverage scores. */
  private final ExplorationConfiguration config;
  /** When all other conditions for a test/suite end is reached, this is used to provide some final randomization. */
  private double fallbackProbability;
  /** For randomization when checking suite end fallback. */
  private Randomizer rand;
  /** Base randomization seed for checking test case end fallback. */
  private long seed;
  /** When exploration was started.. */
  private long startTime = 0;
  /** For calculating coverage scores. */
  private final ScoreCalculator scoreCalculator;
  /** Coverage for the generated test suite. */
  private TestCoverage suiteCoverage;
  /** Useful for debugging, if you need to print something only when concrete execution is going on. */
  private final boolean exploring;
  /** Used to check scenario is done before starting to check if exploration is done. */
  private final EndCondition scenarioEndCondition;
  

  public ExplorationEndCondition(ExplorationConfiguration config) {
    this(config, null, false);
  }

  /**
   * This is used when not calling from main explorer (local) algorithm.
   * 
   * @param config Exploration properties.
   * @param suiteCoverage Used for calculating if end is near or not.
   * @param exploring Set to true if this instance is to be used for exploration mode.
   */
  public ExplorationEndCondition(ExplorationConfiguration config, TestCoverage suiteCoverage, boolean exploring) {
    this.config = config;
    this.scoreCalculator = new ScoreCalculator(config);
    this.fallbackProbability = config.getFallbackProbability();
    this.startTime = System.currentTimeMillis();
    this.suiteCoverage = suiteCoverage;
    this.exploring = exploring;
    Scenario scenario = config.getScenario();
    if (scenario != null) {
      this.scenarioEndCondition = scenario.createEndCondition(null);
    } else {
      this.scenarioEndCondition = null;
    }
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCoverage coverage = suite.getCoverage();
    return isSuiteFinished(tests, coverage);
  }

  public boolean isSuiteFinished(List<TestCase> suite, TestCoverage tc) {
    if (isTimedOut()) {
      return true;
    }
    int max = config.getMaxSuiteLength();
    int suiteSize = suite.size();
    //have we reached the maximum suite length
    if (max > 0) {
      if (suiteSize >= max) {
        return true;
      }
    }
    //are we under minimum suite length
    int min = config.getMinSuiteLength();
    if (min > 0) {
      if (suiteSize < min) {
        return false;
      }
    }
    //plateau should take precedence over score limit otherwise it may never happen. thus the ordering with this here
    int plateauThreshold = config.getSuitePlateauThreshold();
    if (plateauThreshold > 0) {
      if (isSuitePlateau(suite, tc, 2)) {
        return true;
      }
    }
    //do we have minimum score yet?
    int minScore = config.getMinSuiteScore();
    if (minScore > 0) {
      if (scoreCalculator.calculateScore(tc) < minScore) {
        return false;
      }
    }
    //exploration only works nice and shiny until the end condition hits, then we have to go with the pössis
    if (exploring) return true;
    //finally we go with random values to allow progress beyond end if so desired
    double v = rand.nextDouble();
    return v < fallbackProbability;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    if (scenarioEndCondition != null) {
      //if scenario is not yet done, we do not end no matter what
      if (!scenarioEndCondition.endTest(suite, fsm)) return false;
    }
    if (isTimedOut()) {
      log.debug("Exploration timeout");
      return true;
    }

    int max = config.getMaxTestLength();
    int testSteps = suite.currentSteps();
    if (max > 0) {
      if (testSteps >= max) {
        log.debug("test over maximum length");
        return true;
      }
    }
    //have we achieved defined minimum length yet?
    int min = config.getMinTestLength();
    if (min > 0) {
      if (testSteps < min) {
        return false;
      }
    }
    long mySeed = seed + suite.totalSteps();
    //plateau should take precedence over score limit otherwise it never happens. thus the ordering with this here
    int plateauThreshold = config.getTestPlateauThreshold();

    if (plateauThreshold > 0) {
      if (isTestPlateau(suite, config.getTestPlateauLength())) {
        log.debug("test has plateaued");
        return checkProbability(mySeed);
      }
    }

    //have we achieved minimum defined added test coverage yet?
    int minScore = config.getMinTestScore();
    if (minScore > 0) {
      if (suiteCoverage == null) suiteCoverage = suite.getCoverage();
      TestCase test = suite.getCurrentTest();
      TestCaseStep step = test.getCurrentStep();
      //this is the case when starting generation
      if (step == null) return false;
      int added = step.getAddedCoverage();

      if (added < minScore) {
        return false;
      }
    }
    
    //exploration only works nice and shiny until the end condition hits, then we have to go with the pössis
    //this is because if we evaluate paths for overall score and one of them is longer but only due to having
    //a different random "v" (below) value generated, it "seems" better although in fact should not (or should it ?:)
    //for example: ++-+-+++++++++  //<-evaluates one step later for the random "v" and gets a different value
    //             ++-+++++++   //<-evaluates one step before for the random "v" and gets a different value
    if (exploring) return true;
    return checkProbability(mySeed);
  }
  
  private boolean checkProbability(long mySeed) {
    //need a separate randomizer, with predictable but changing seed to allow for parallel runs using same endcondition
    Randomizer rand = new Randomizer(mySeed);
    //finally we go with random values to allow progress beyond end if so desired
    double v = rand.nextDouble();
    log.debug("randomizing..:"+v);
    return v < fallbackProbability;
  }  

  private boolean isTimedOut() {
    if (config.getTimeout() <= 0) {
      return false;
    }
    long now = System.currentTimeMillis();
    long diff = now - startTime;
    if (diff > config.getTimeout() * 1000) {
      return true;
    }
    return false;
  }

  @Override
  public void init(long seed, FSM fsm) {
    this.seed = seed;
    this.rand = new Randomizer(seed);
    //pathexplorer uses this to check path ending without the fsm
    if (fsm != null) config.validate(fsm);
  }

  /**
   * Evaluates the test suite to define if test generation has reached a plateau.
   * The suite generation is considered to have reached a plateau if the last two test cases have not increased
   * the coverage score above the plateau threshold defined in the exploration configuration.
   *
   * @param suite The suite to check for plateau.
   * @param size How many tests to use for testing the plateau.
   * @return True if plateau is reached. False otherwise.
   */
  private boolean isSuitePlateau(List<TestCase> suite, TestCoverage suiteCoverage, int size) {
    if (suite.size() < size) {
      //if we have less than 2 tests finished, we cannot check for plateau
      return false;
    }
    //create a clone of suite with two less tests to compare against
    List<TestCase> suiteClone = new ArrayList<>();
    suiteClone.addAll(suite);
    for (int i = 0 ; i < size ; i++) {
      suiteClone.remove(suiteClone.size() - 1);
    }
    TestCoverage cloneTC = new TestCoverage(suiteClone);
    
    //calculate how much the last N tests have added coverage
    int before = scoreCalculator.calculateScore(cloneTC);
    int after = scoreCalculator.calculateScore(suiteCoverage);
    int diff = after - before;
//    int diff = scoreCalculator.addedFitnessFor(suiteCoverage, newTests);
    //is the added value more than the given threshold?
    return diff < config.getSuitePlateauThreshold();
  }

  /**
   * Evaluates the test case to define if test generation has reached a plateau.
   * The test case generation is considered to have reached a plateau if the current test case is observed to gain
   * less than the plateau threshold of added coverage in the current best exploration option (up to 'depth' new steps).
   * The plateau threshold is defined in the exploration configuration.
   *
   * @param suite The suite where we grab the current test from to check for plateau.
   * @param steps How many steps to check for the plateau.
   * @return True if plateau is reached. False otherwise.
   */
  private boolean isTestPlateau(TestSuite suite, int steps) {
    TestCase currentTest = suite.getCurrentTest();
    int length = currentTest.getSteps().size();
    //steps + 1 is to measure gain for last "steps" number of steps correctly
    //for example, suite has four steps: A,B,C,D and we request to look at last 3
    //if we count different between D and B, we get gain of last two which is C and D
    //so we must look at different between gain in end of A and in end of D. this requires the + 1
    int index = length - (steps+1);
    if (index < steps) {
      //not possible to check the plateau if test is not long enough
      return false;
    } 
    int now = currentTest.getCurrentStep().getAddedCoverage();
    int before = currentTest.getSteps().get(index).getAddedCoverage();
//    TestCoverage coverage = suite.getCoverage();
//    int now = scoreCalculator.addedScoreFor(coverage, currentTest);
////    int before = scoreCalculator.addedScoreFor(coverage, currentTest, length-steps);
//    int before = scoreCalculator.addedScoreFor(coverage, currentTest);
    int diff = now - before;
    //is the added value more than the given threshold?
    return diff < config.getTestPlateauThreshold();
  }
}
 
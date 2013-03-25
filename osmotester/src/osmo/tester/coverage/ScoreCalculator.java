package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.Collection;
import java.util.Map;

/** @author Teemu Kanstren */
public class ScoreCalculator {
  private static Logger log = new Logger(ScoreCalculator.class);
  /** Defines how the coverage score is calculated. */
  private final ScoreConfiguration config;

  public ScoreCalculator(ScoreConfiguration config) {
    this.config = config;
  }

  /**
   * Calculates the coverage score for the data represented in this coverage object with the given score configuration.
   *
   * @return The score for the set described in this object.
   */
  public int calculateScore(TestCoverage tc) {
    int score = tc.getTransitions().size() * config.getLengthWeight();
    score += tc.getSingles().size() * config.getStepWeight();
    score += tc.getStepPairs().size() * config.getStepPairWeight();
    score += tc.getStates().size() * config.getStateWeight();
    score += tc.getStatePairs().size() * config.getStatePairWeight();
    Map<String,Collection<String>> variables = tc.getVariables();
    score += variables.size() * config.getVariableCountWeight();
    for (String name : variables.keySet()) {
      Collection<String> values = variables.get(name);
      score += values.size() * config.getVariableWeight(name);
    }
    score += tc.getRequirements().size() * config.getRequirementWeight();
    log.debug("calculated score:" + score);
    return score;
  }

  public int addedScoreFor(TestCoverage tc, TestCase test) {
    return addedScoreFor(tc, test, test.getAllTransitionNames().size());
  }

  /**
   * Calculates how much the coverage score would raise if the given test case was added to this set.
   * Does not add anything to this set, so after this the set is the same as before.
   *
   * @param test The test to check added coverage for.
   * @return The new coverage score.
   */
  public int addedScoreFor(TestCoverage tc1, TestCase test, int steps) {
    TestCoverage tc2 = tc1.cloneMe();
    tc2.addTestCoverage(test, steps);
    int oldScore = calculateScore(tc1);
    int newScore = calculateScore(tc2);
    int added = newScore - oldScore;
    log.debug("added fitness:" + added);
    return added;
  }

  /**
   * Calculates how much the coverage score would raise if the given test cases were added to this set.
   * Does not add anything to this set, so after this the set is the same as before.
   *
   * @param tests The tests to check added coverage for.
   * @return The new coverage score.
   */
  public int addedScoreFor(TestCoverage tc1, Collection<TestCase> tests) {
    TestCoverage tc2 = tc1.cloneMe();
    for (TestCase test : tests) {
      tc2.addTestCoverage(test);
    }
    int oldScore = calculateScore(tc1);
    int newScore = calculateScore(tc2);
    int added = newScore - oldScore;
    log.debug("added fitness:" + added);
    return added;
  }
}

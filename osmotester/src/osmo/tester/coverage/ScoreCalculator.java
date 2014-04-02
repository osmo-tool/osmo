package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.Collection;
import java.util.Map;

/** 
 * Calculates coverage score for given configuration and test coverage values.
 * 
 * @author Teemu Kanstren 
 */
public class ScoreCalculator {
  private static final Logger log = new Logger(ScoreCalculator.class);
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
    int score = tc.getTotalSteps() * config.getLengthWeight();
    score += tc.getSingles().size() * config.getStepWeight();
    score += tc.getStepPairs().size() * config.getStepPairWeight();
    score += tc.getVariables().size() * config.getVariableCountWeight();
    score += tc.getStateCount() * config.getStateWeight();
    score += tc.getStatePairCount() * config.getStatePairWeight();
    Map<String,Collection<String>> values = tc.getVariableValues();
    for (String name : values.keySet()) {
      Collection<String> varValues = values.get(name);
      score += varValues.size() * config.getVariableWeight(name);
    }
    score += tc.getRequirements().size() * config.getRequirementWeight();
    log.debug("calculated score:" + score);
    return score;
  }

  /**
   * How much score would adding the given test case add to the given test coverage set?
   * Does not add anything to this set, so after this the set is the same as before.
   * Calculates for all steps in given test case.
   * 
   * @param tc1 The coverage so far.
   * @param test The test to add to the coverage so far (the tc parameter).
   * @return How much score would be added.
   */
  public int addedScoreFor(TestCoverage tc1, TestCase test) {
//    return addedScoreFor(tc, test, test.getAllStepNames().size());
//  }
//
//  /**
//   * Calculates how much the coverage score would raise if the given test case was added to this set.
//   * Does not add anything to this set, so after this the set is the same as before.
//   * Calculates to the number of steps, ignoring any remaining steps beyond given number.
//   *
//   * @param tc1 The coverage so far.
//   * @param test The test to check added coverage for.
//   * @param steps The number of steps to take from the (beginning of) given test.
//   * @return The new coverage score.
//   */
//  public int addedScoreFor(TestCoverage tc1, TestCase test, int steps) {
    TestCoverage tc2 = test.getCoverage();
    TestCoverage tc3 = tc2.cloneMe();
    tc3.addCoverage(tc1);
//    tc2.addTestCoverage(test, steps);
    int oldScore = calculateScore(tc1);
    int newScore = calculateScore(tc3);
    int added = newScore - oldScore;
    log.debug("added score:" + added);
    return added;
  }
}

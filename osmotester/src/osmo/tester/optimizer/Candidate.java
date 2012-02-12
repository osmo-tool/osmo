package osmo.tester.optimizer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a candidate solution for optimization of test suite.
 * A candidate solution is one possible ordering of tests.
 *
 * @author Teemu Kanstren
 */
public class Candidate {
  private static final Logger log = new Logger(Candidate.class);
  /** The set of tests for this candidate. */
  private List<TestCase> tests = new ArrayList<>();
  /** Candidate fitness, if > -1 it is not recalculated. Higher fitness = better candidate. */
  private int fitness = -1;
  /** The configuration to use in this search, used for fitness calculation. */
  private final SearchConfiguration config;

  public Candidate(SearchConfiguration config, List<TestCase> tests) {
    this.tests = tests;
    this.config = config;
  }

  /**
   * Gives the candidate fitness. Calculates if not yet calculated. Once calculated, it is never recalculated
   * (even if search configuration is changed). Higher fitness describes a better candidate.
   *
   * @return The fitness.
   */
  public synchronized int getFitness() {
    if (fitness < 0) {
      fitness = calculateFitness();
    }
    return fitness;
  }

  /**
   * Calculates fitness value. This always recalculates but does not store anywhere.
   *
   * @return The calculated fitness.
   */
  public int calculateFitness() {
    TestCoverage suiteCoverage = new TestCoverage();
    for (TestCase tc : tests) {
      suiteCoverage.addTestCoverage(tc);
    }
    return suiteCoverage.fitnessFor(config);
  }

  /** @return The number of tests in the candidate. */
  public int size() {
    return tests.size();
  }

  public List<TestCase> getTests() {
    return tests;
  }

  @Override
  public String toString() {
    return "candidate:fitness=" + getFitness();
  }

  /**
   * @param i Index of test case to get.
   * @return The test case at given index for this candidate.
   */
  public TestCase get(int i) {
    return tests.get(i);
  }

  /**
   * Creates a matrix of information for all tests in this candidate.
   *
   * @return The information matrix.
   */
  public String matrix() {
    String matrix = "";
    for (TestCase test : tests) {
      matrix += matrixFor(test);
    }
    return matrix;
  }

  /**
   * Creates a matrix of information for the given test case.
   *
   * @param test The test to get the matrix for.
   * @return The information matrix.
   */
  private String matrixFor(TestCase test) {
    String matrix = "";
    TestCoverage coverage = new TestCoverage(test);
    int pairs = coverage.getPairs().size();
    int transitions = coverage.getTransitions().size();
    int singles = coverage.getSingles().size();
    int reqs = coverage.getRequirements().size();
    Map<String, ModelVariable> variables = coverage.getVariables();
    int variableCount = variables.keySet().size();
    int valueCount = 0;
    for (ModelVariable variable : variables.values()) {
      valueCount += variable.getValues().size();
    }
    matrix += "test:\n";
    matrix += "pairs = " + pairs + "\n";
    matrix += "transitions = " + transitions + "\n";
    matrix += "singles = " + singles + "\n";
    matrix += "requirements = " + reqs + "\n";
    matrix += "variables = " + variableCount + "\n";
    matrix += "values = " + valueCount + "\n";
    return matrix;
  }
}

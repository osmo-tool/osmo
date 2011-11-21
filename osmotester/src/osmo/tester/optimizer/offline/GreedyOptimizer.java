package osmo.tester.optimizer.offline;

import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.Candidate;
import osmo.tester.optimizer.SearchConfiguration;
import osmo.tester.optimizer.TestCoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Greedily optimizes the test suite with regards to coverage criteria as given in a {@link SearchConfiguration}.
 * Meaning the first test in the given set that has the highest overall fitness values, the second one adds
 * most fitness to that one as evaluated in terms of single test cases, the third adds the most to these two,
 * and so on.
 *
 * @author Teemu Kanstren
 */
public class GreedyOptimizer {
  /** Defines weights for different coverage requirements to optimize for. */
  private final SearchConfiguration config;
  /** The overall coverage in the suite we are building, updated as new tests are chosen and added to suite. */
  private TestCoverage suiteCoverage = new TestCoverage();
  /** The coverage of the best test in each iteration, used to find the best of each iteration. */
  private TestCoverage bestTestCoverage = new TestCoverage();
  /** Here we will store the result of optimization. */
  private List<TestCase> tests = new ArrayList<TestCase>();
  /** The best test case of each iteration, added to test suite to increase fitness the most. */
  private TestCase best = null;

  public GreedyOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
  }

  /**
   * Performs a search following the defined search configuration.
   *
   * @return The best candidate found (with highest fitness).
   */
  public Candidate search() {
    List<TestCase> tests = createSortedTestSet(config.getPopulationSize());
    return new Candidate(config, tests);
  }

  /**
   * Provides a sorted list of test cases, where the one with highest fitness is first, one that
   * adds most to this is second, one that adds most to those two is third and so on.
   * The set of tests is chosen by first generating a number of tests equals to the values in
   * searchconfiguration (populationSize*numberOfCandidates). From this overall set, the number
   * howMany is returned as sorted in this way.
   *
   * @param howMany The number of tests to be returned, sorted as specified.
   * @return The sorted set of test cases, with requested number of tests.
   */
  public List<TestCase> createSortedTestSet(int howMany) {
    int count = config.getNumberOfCandidates();
    int populationSize = config.getPopulationSize();
    count *= populationSize;
    MainGenerator generator = config.getGenerator();
    generator.initSuite();
    List<TestCase> all = new ArrayList<TestCase>();
    for (int i = 0; i < count; i++) {
      all.add(generator.next());
    }
    TestCase high = null;
    for (TestCase test : all) {
      if (high == null || test.getSteps().size() > high.getSteps().size()) {
        high = test;
      }
    }
    generator.endSuite();
    return createSortedTestSet(howMany, all);
  }

  /**
   * Same as createSortedSet(int howMany) but does not generate the tests, uses the given set as source instead.
   *
   * @param howMany How many to pick from the source set.
   * @param from    The source set to pick from.
   * @return Greedily sorted set of requested size.
   */
  public List<TestCase> createSortedTestSet(int howMany, List<TestCase> from) {
    if (howMany > from.size()) {
      throw new IllegalArgumentException("Requested bigger greedily sorted set than source has elements.");
    }
    for (int i = 0; i < howMany; i++) {
      int added = 0;
      for (TestCase test : from) {
        TestCoverage tc = new TestCoverage(test);
        int fitness = tc.fitnessFor(config);
        if (fitness >= added) {
          added = fitness;
          best = test;
        }
      }
      from.remove(best);
      addBest();
    }

    return tests;
  }

  /** Adds the test chosen as the best (with highest added fitness) to the overall sorted set (suite). */
  private void addBest() {
    tests.add(best);
    suiteCoverage.getTransitions().addAll(bestTestCoverage.getTransitions());
    suiteCoverage.getPairs().addAll(bestTestCoverage.getPairs());
    suiteCoverage.getSingles().addAll(bestTestCoverage.getSingles());
    suiteCoverage.getRequirements().addAll(bestTestCoverage.getRequirements());
    Map<String, ModelVariable> suiteVariables = suiteCoverage.getVariables();
    Map<String, ModelVariable> btVariables = bestTestCoverage.getVariables();
    for (String name : btVariables.keySet()) {
      ModelVariable mv = suiteVariables.get(name);
      if (mv == null) {
        mv = new ModelVariable(name);
        suiteVariables.put(name, mv);
      }
      mv.addAll(btVariables.get(name));
    }
  }
}

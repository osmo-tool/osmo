package osmo.tester.optimizer.online;

import osmo.common.log.Logger;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  private static final Logger log = new Logger(SearchingOptimizer.class);
  private static final FitnessComparator comparator = new FitnessComparator();
  private Collection<TestCase> population = new ArrayList<TestCase>();
  private final SearchConfiguration config;

  public SearchingOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
  }

  public void search() {
    OSMOTester tester = new OSMOTester();
    int noc = config.getNumberOfCandidates();
    Length maxLength = new Length(noc);
    maxLength.setStrict(true);
    tester.addTestEndCondition(maxLength);
    tester.addSuiteEndCondition(maxLength);
    tester.generate();
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    List<TestCase> workList = new ArrayList<TestCase>();
    workList.addAll(population);
    workList.addAll(tests);
    List<Candidate> candidates = new ArrayList<Candidate>();
    for (int i = 0 ; i < noc ; i++) {
      candidates.add(createCandidate(workList));
    }
    int iterations = config.getIterations();
    for (int i = 0 ; i < iterations ; i++) {
      candidates = nextGenerationFrom(candidates);
    }
  }

  public List<Candidate> nextGenerationFrom(List<Candidate> candidates) {
    Collections.sort(candidates, comparator);
    return candidates;
    //kopioi testejä toisesta setistä
    //generoi uusia settejä
    //laskee fitness arvot
    //parhaat talteen

  }

  public Candidate createCandidate(Collection<TestCase> from) {
    int populationSize = config.getPopulationSize();
    if (from.size() < populationSize) {
      throw new IllegalArgumentException("Requested population of " + populationSize + " from set of " + from.size() + " tests. Population size cannot be bigger than source set size.");
    }
    List<TestCase> tests = new ArrayList<TestCase>();
    tests.addAll(from);
    while (tests.size() > populationSize) {
      tests.remove(cInt(0, tests.size() - 1));
    }
    return new Candidate(config, tests);
  }
}

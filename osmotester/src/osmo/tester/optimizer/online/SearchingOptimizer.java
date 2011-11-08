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
import java.util.Random;

import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  private static final Logger log = new Logger(SearchingOptimizer.class);
  private Candidate best = null;
  private static final FitnessComparator comparator = new FitnessComparator();
  private Collection<TestCase> population = new ArrayList<TestCase>();
  private final SearchConfiguration config;
  private final Random random;

  public SearchingOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
    random = new Random(config.getSeed());
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
      updateBestFrom(candidates);
    }
  }

  private void updateBestFrom(List<Candidate> candidates) {
    Collections.sort(candidates, comparator);
    Candidate current = candidates.get(0);
    if (best == null || best.getFitness() < current.getFitness()) {
      best = current;
    }
  }

  public List<Candidate> nextGenerationFrom(List<Candidate> candidates) {
    return candidates;
    //kopioi testejä toisesta setistä
    //generoi uusia settejä
    //laskee fitness arvot
    //parhaat talteen
  }

  //this implements uniform crossover
  public Candidate[] recombine(Candidate c1, Candidate c2) {
    Candidate[] result = new Candidate[2];
    List<TestCase> c3Tests = new ArrayList<TestCase>();
    List<TestCase> c4Tests = new ArrayList<TestCase>();
    List<TestCase> joint = new ArrayList<TestCase>();
    joint.addAll(c1.getTests());
    joint.retainAll(c2.getTests());
    //this is actually Hamming distance
    int size = c1.size()-joint.size();
    int i1 = 0;
    int i2 = 0;
    for (int i = 0 ; i < size ; i++) {
      while (joint.contains(c1.get(i+i1))) {
        c3Tests.add(c1.get(i + i1));
        i1++;
      }
      while (joint.contains(c2.get(i + i2))) {
        c4Tests.add(c2.get(i + i2));
        i2++;
      }
      TestCase t3 = c1.get(i+i1);
      TestCase t4 = c2.get(i+i2);
      double d = random.nextDouble();
      if (d >= 0.5d) {
        TestCase t5 = t3;
        t3 = t4;
        t4 = t5;
      }
      c3Tests.add(t3);
      c4Tests.add(t4);
    }
    result[0] = new Candidate(config, c3Tests);
    result[1] = new Candidate(config, c4Tests);
    return result;
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

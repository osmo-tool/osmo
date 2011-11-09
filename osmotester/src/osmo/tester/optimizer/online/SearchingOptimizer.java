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

import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  private static final Logger log = new Logger(SearchingOptimizer.class);
  private static final FitnessComparator comparator = new FitnessComparator();
  private final SearchConfiguration config;
  private final SearchEndCondition endCondition;
  private SearchState state = new SearchState();

  public SearchingOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
    this.endCondition = config.getEndCondition();
  }

  public SearchState getState() {
    return state;
  }

  public Candidate search() {
    OSMOTester tester = config.getTester();
    int noc = config.getNumberOfCandidates();
    Length maxLength = new Length(noc);
    maxLength.setStrict(true);
    tester.addSuiteEndCondition(maxLength);
    tester.generate();
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    List<Candidate> candidates = new ArrayList<Candidate>();
    for (int i = 0; i < noc; i++) {
      Candidate candidate = createCandidate(tests);
      candidates.add(candidate);
    }
    while (!endCondition.shouldEnd(state)) {
      state.incrementIterationCount();
      candidates = nextGenerationFrom(candidates);
    }
    return state.getBest();
  }

  public void updateBestFrom(List<Candidate> candidates) {
    Candidate current = candidates.get(candidates.size() - 1);
    state.checkCandidate(current);
  }

  public List<Candidate> nextGenerationFrom(List<Candidate> candidates) {
    log.debug("next generation of search space being created");
    Collections.sort(candidates, comparator);
    int size = config.getPopulationSize();
    List<Candidate> newPopulation = new ArrayList<Candidate>();
    List<Integer> weights = new ArrayList<Integer>();
    int total = 0;
    for (Candidate candidate : candidates) {
      int fitness = candidate.getFitness();
      total += fitness;
      weights.add(total);
    }
    while (newPopulation.size() < size) {
      int index1 = sumWeightedRandomFrom(weights);
      int index2 = index1;
      while (index2 == index1) {
        index2 = sumWeightedRandomFrom(weights);
      }
      Candidate parent1 = candidates.get(index1);
      Candidate parent2 = candidates.get(index2);
      Candidate[] offspring = recombine(parent1, parent2);
      newPopulation.add(offspring[0]);
      newPopulation.add(offspring[1]);
    }
    updateBestFrom(newPopulation);
    return newPopulation;
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
    int size = c1.size() - joint.size();
    int i1 = 0;
    int i2 = 0;
    for (int i = 0; i < size; i++) {
      while (joint.contains(c1.get(i + i1))) {
        c3Tests.add(c1.get(i + i1));
        i1++;
      }
      while (joint.contains(c2.get(i + i2))) {
        c4Tests.add(c2.get(i + i2));
        i2++;
      }
      TestCase t3 = c1.get(i + i1);
      TestCase t4 = c2.get(i + i2);
      double d = cDouble();
      if (d >= 0.5d) {
        TestCase t5 = t3;
        t3 = t4;
        t4 = t5;
      }
      c3Tests.add(t3);
      c4Tests.add(t4);
    }
    //this is needed if joint tests are in last position, otherwise they will be left out
    for (TestCase test : joint) {
      if (!c3Tests.contains(test)) {
        c3Tests.add(test);
      }
      if (!c4Tests.contains(test)) {
        c4Tests.add(test);
      }
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

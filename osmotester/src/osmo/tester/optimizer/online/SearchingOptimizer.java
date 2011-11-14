package osmo.tester.optimizer.online;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  private static final Logger log = new Logger(SearchingOptimizer.class);
  private static final FitnessComparator comparator = new FitnessComparator();
  private final SearchConfiguration config;
  private final SearchEndCondition endCondition;
  private SearchState state = new SearchState();
  private MainGenerator generator = null;
  private final Randomizer random;

  public SearchingOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
    this.endCondition = config.getEndCondition();
    this.random = new Randomizer(configuration.getSeed());
  }

  public SearchState getState() {
    return state;
  }

  public void setGenerator(MainGenerator generator) {
    this.generator = generator;
  }

  public Candidate search() {
    this.generator = config.getGenerator();
    int noc = config.getNumberOfCandidates();
    List<Candidate> candidates = new ArrayList<Candidate>();
    generator.initSuite();
    for (int i = 0; i < noc; i++) {
      Candidate candidate = createCandidate();
      candidates.add(candidate);
    }
    Collections.sort(candidates, comparator);
    updateBestFrom(candidates);
    runPhase(candidates, 0);
    runPhase(candidates, 0.01);
    runPhase(candidates, 0.05);
    runPhase(candidates, 0.20);
    generator.endSuite();
    return state.getBest();
  }

  private List<Candidate> runPhase(List<Candidate> candidates, double mutationProbability) {
    state.startPhase();
    while (!endCondition.shouldEnd(state)) {
      generator.resetSuite();
      state.incrementIterationCount();
      candidates = nextGenerationFrom(candidates, mutationProbability);
    }
    return candidates;
  }

  public void updateBestFrom(List<Candidate> candidates) {
    Candidate current = candidates.get(candidates.size() - 1);
    state.checkCandidate(current);
  }

  public List<Candidate> nextGenerationFrom(List<Candidate> candidates, double mp) {
    log.debug("next generation of search space being created");
    List<Candidate> newPopulation = new ArrayList<Candidate>();
    int size = config.getPopulationSize();
    Collections.sort(candidates, comparator);
    List<Integer> weights = getWeightLine(candidates);
    while (newPopulation.size() < size) {
      int index1 = random.sumWeightedRandomFrom(weights);
      int index2 = index1;
      while (index2 == index1) {
        index2 = random.sumWeightedRandomFrom(weights);
      }
      Candidate parent1 = candidates.get(index1);
      Candidate parent2 = candidates.get(index2);
      Candidate[] offspring = recombine(parent1, parent2);
//      Candidate[] offspring = new Candidate[] {parent1, parent2};
      mutate(offspring[0], mp);
      mutate(offspring[1], mp);
      newPopulation.add(offspring[0]);
      newPopulation.add(offspring[1]);
    }
    updateBestFrom(newPopulation);
    return newPopulation;
  }

  private List<Integer> getWeightLine(List<Candidate> candidates) {
    int low = candidates.get(0).getFitness();
    if (low < 0) {
      //tricks to allow negative weights
      low *= -1;
      low++;
    }
    List<Integer> weights = new ArrayList<Integer>();
    int total = 0;
    for (Candidate candidate : candidates) {
      int fitness = candidate.getFitness();
      fitness += low;
      total += fitness;
      weights.add(total);
    }
    return weights;
  }

  public void mutate(Candidate candidate, double probability) {
    List<TestCase> tests = candidate.getTests();
    int size = tests.size();
    for (int i = 0; i < size; i++) {
      double tp = random.cDouble();
      if (tp < probability) {
        tests.set(i, generator.next());
      }
    }
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
      double d = random.cDouble();
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

  public Candidate createCandidate() {
    //TODO: validate configuration first
    int populationSize = config.getPopulationSize();
    List<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0; i < populationSize; i++) {
      tests.add(generator.next());
    }
    return new Candidate(config, tests);
  }

  /*
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
  }*/
}

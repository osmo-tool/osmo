package osmo.tester.optimizer.online;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.Candidate;
import osmo.tester.optimizer.FitnessComparator;
import osmo.tester.optimizer.SearchConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements search-based optimization to look for an optimal set of test cases.
 * Uses an implementation of genetic algorithms, with uniform crossover and a mutation operator of
 * generating new tests from the model.
 * <p/>
 * NOTE: Currently this has been observed to basically suck, so give it a try if interested but also try
 * the {@link osmo.tester.optimizer.offline.GreedyOptimizer} as well.
 *
 * @author Teemu Kanstren
 */
public class SearchingOptimizer {
  private static final Logger log = new Logger(SearchingOptimizer.class);
  /** Used to sort candidates according to fitness. Required for weighted random choice. */
  private static final FitnessComparator comparator = new FitnessComparator();
  /** Configuration for the search to be performed. */
  private final SearchConfiguration config;
  /** The end condition when this optimizer should stop searching. */
  private final SearchEndCondition endCondition;
  /** The current state of the search. */
  private SearchState state = new SearchState();
  /** Generates the test cases for us. */
  private MainGenerator generator = null;
  /** Produces randomized values for this optimizer. */
  private final Randomizer random;

  public SearchingOptimizer(SearchConfiguration configuration) {
    this.config = configuration;
    this.endCondition = config.getEndCondition();
    this.random = new Randomizer(configuration.getSeed());
    this.generator = config.getGenerator();
  }

  public SearchState getState() {
    return state;
  }

  /**
   * Searches the given set of tests according to the defined search configuration.
   *
   * @param tests The set of tests to be searched.
   * @return The optimized candidate.
   */
  public Candidate searchFromTests(List<TestCase> tests) {
    int noc = config.getNumberOfCandidates();
    List<Candidate> candidates = new ArrayList<Candidate>();
    for (int i = 0; i < noc; i++) {
      int populationSize = config.getPopulationSize();
      List<TestCase> candidateTests = new ArrayList<TestCase>();
      for (int p = 0; p < populationSize; p++) {
        candidateTests.add(tests.remove(0));
      }
      Candidate candidate = new Candidate(config, candidateTests);
      candidates.add(candidate);
    }
    return search(candidates);
  }

  /**
   * Peforms a search according to the given search configuration.
   *
   * @return The optimized candidate.
   */
  public Candidate search() {
    int noc = config.getNumberOfCandidates();
    List<Candidate> candidates = new ArrayList<Candidate>();
    generator.initSuite();
    for (int i = 0; i < noc; i++) {
      Candidate candidate = createCandidate();
      candidates.add(candidate);
    }
    Candidate best = search(candidates);
    generator.endSuite();
    return best;
  }

  /**
   * Search the given list of candidates for the optimal candidate. Does recombination, mutation, etc. on them.
   *
   * @param candidates The set of candidates to search.
   * @return The optimal candidate found.
   */
  public Candidate search(List<Candidate> candidates) {
    Collections.sort(candidates, comparator);
    updateBestFrom(candidates);
    runPhase(candidates, 0);
    runPhase(candidates, 0.01);
    runPhase(candidates, 0.05);
    return state.getBest();
  }

  /**
   * Runs a search on the given set of candidates with the given mutation probability.
   *
   * @param candidates          The set of candidates to search.
   * @param mutationProbability The probability to mutatate (replace with another) a test.
   * @return The set of candidates order as best first, worst last.
   */
  private List<Candidate> runPhase(List<Candidate> candidates, double mutationProbability) {
    state.startPhase();
    while (!endCondition.shouldEnd(state)) {
      generator.resetSuite();
      state.incrementIterationCount();
      candidates = nextGenerationFrom(candidates, mutationProbability);
    }
    return candidates;
  }

  /**
   * Updates the best solution found if better than what is before found.
   *
   * @param candidates List of candidates. Must be sorted so best is first.
   */
  public void updateBestFrom(List<Candidate> candidates) {
    Candidate current = candidates.get(candidates.size() - 1);
    state.checkCandidate(current);
  }

  /**
   * Creates a new search generation for the genetic algorithm.
   *
   * @param candidates The source set of candidates for generation.
   * @param mp         The probability to mutation a test in candidate suite.
   * @return The next generation.
   */
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

  /**
   * Provides a "weight-line" meaning a list of weights representing fitness of candidates in the given list.
   * The list can then be used for weighted random choice.
   *
   * @param candidates The set of candidates for which weights should be provided.
   * @return The list of weights, in the same order as matching candidates.
   */
  private List<Integer> getWeightLine(List<Candidate> candidates) {
    int low = candidates.get(0).getFitness();
    if (low <= 0) {
      //tricks to allow negative weights
      //0 will also fail weight calculations so we must add +1 to it as well
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

  /**
   * @param candidate
   * @param probability
   */
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

  /**
   * Implements uniform crossover recombination for the given two candidates.
   * Switches the tests at matching indices of the candidates with a 50% probability.
   *
   * @param c1 The first parent.
   * @param c2 The second parent.
   * @return Two offsping.
   */
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

  /**
   * Creates a new candidate suite based on the defined search configuration (generates new tests to fill a new candidate).
   *
   * @return The generated candidate.
   */
  public Candidate createCandidate() {
    int populationSize = config.getPopulationSize();
    List<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0; i < populationSize; i++) {
      tests.add(generator.next());
    }
    return new Candidate(config, tests);
  }
}

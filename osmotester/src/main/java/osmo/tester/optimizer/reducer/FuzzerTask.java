package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.reducer.debug.invariants.NumberOfSteps;
import osmo.tester.scenario.Scenario;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Generates randomized test cases using given configuration.
 * If used in debugging mode, tries to find a path that throws an Exception.
 * If used in requirements mode, tries to find tests that cover different requirements.
 *
 * @author Teemu Kanstren
 */
public class FuzzerTask implements Runnable {
  private static final Logger log = new Logger(FuzzerTask.class);
  /** The generator configuration. */
  private final OSMOConfiguration config;
  /** Base seed randomizer for the generators running in this task. Used to generate generator seeds. */
  private final Randomizer seeder;
  /** Current reduction state; found tests, iteration information, etc. */
  private final ReducerState state;
  /** Number of tests to generate in one fuzzing iteration. */
  private final int populationSize;
  /** Task iteration counter. */
  private static int nextId = 1;
  private final TestCase test;

  public FuzzerTask(OSMOConfiguration osmoConfig, TestCase test, long seed, ReducerState state) {
    this.config = new OSMOConfiguration(osmoConfig);
    this.seeder = new Randomizer(seed);
    this.state = state;
    this.test = test;
    this.populationSize = state.getConfig().getPopulationSize();
  }

  /**
   * Task execution starts here.
   */
  @Override
  public void run() {
    try {
      runrun();
    } catch (Exception e) {
      log.e("Failed to run reducer task", e);
    }
  }

  /**
   * Task main execution delegates here. To allow cleaner implementation if exceptions need to be thrown.
   */
  public void runrun() {
    log.i("Starting fuzz task");
    while (!state.isDone()) {
      if (state.isFoundFailing()) {
        config.setScripts(null);
      }
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.setPrintCoverage(false);
      if (test != null) {
        //here we update the generator configuration to look for shorter tests allowing only found steps
        Scenario scenario = createScenario(test);
        config.setScenario(scenario);
      }
      int newMinimum = state.getMinimum();
      int length = state.getConfig().getLength();
      if (newMinimum > length) {
        newMinimum = length;
      }
      //if a parallel task found a shorter path, we aim for even shorter
      tester.setTestEndCondition(new Length(newMinimum));
      tester.setSuiteEndCondition(new Length(populationSize));
      //create new seed for our new test generator
      long seed = seeder.nextLong();
      int id = nextId++;
      log.d("Starting fuzzer " + id + " with seed " + seed + " and population " + populationSize);
      tester.generate(seed);
      //update counter for how many tests overall have been generated
      state.testsDone(populationSize);
      TestSuite suite = tester.getSuite();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        //if we debug, we ignore passing tests. if we look for requirements we look at them all
        if (!state.getConfig().isRequirementsSearch() && !test.isFailed()) continue;
        //here we check if this matches what we are looking for.
        //that is it is shorter than before and in case of requirements it covers the requirement we look for
        //in case of requirements, it also captures any better tests for other requirements
        if (!state.check(test)) continue;
        state.addTest(test);
      }
    }
  }

  /**
   * Creates a new generator scenario.
   *
   * @param test To use as a basis.
   * @return Configuration for the generator targeting variants of given test.
   */
  public Scenario createScenario(TestCase test) {
    Scenario scenario = new Scenario(true);
    List<String> allSteps = test.getAllStepNames();
    Collection<String> steps = new HashSet<>();
    steps.addAll(allSteps);
    NumberOfSteps metric = new NumberOfSteps(test);
    Map<String,Integer> counts = metric.getStepCounts();
    for (String step : steps) {
      if (state.getConfig().isStrictReduction()) scenario.addSlice(step, 0, counts.get(step));
      else scenario.addSlice(step, 0, test.getLength());
    }
    return scenario;
  }
}

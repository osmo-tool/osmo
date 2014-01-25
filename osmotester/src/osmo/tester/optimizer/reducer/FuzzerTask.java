package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
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
 * Generates test cases using given configuration, trying to find a path that throws an Exception.
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

  public FuzzerTask(OSMOConfiguration osmoConfig, long seed, ReducerState state) {
    this.config = osmoConfig;
    this.seeder = new Randomizer(seed);
    this.state = state;
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
      log.error("Failed to run reducer task", e);
    }
  }

  /**
   * Task main execution delegates here. To allow cleaner implementation if exceptions need to be thrown.
   */
  public void runrun() {
    while (!state.isDone()) {
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.setPrintCoverage(false);
      int newMinimum = state.getMinimum();
      if (newMinimum <= 0) {
        //does this really happen?
        log.info("Stopping due to new minimun "+newMinimum);
        state.finish();
        return;
      }
      tester.setTestEndCondition(new Length(newMinimum));
      tester.setSuiteEndCondition(new Length(populationSize));
      long seed = seeder.nextLong();
      int id = nextId++;
      log.debug("Starting reducer task "+id+" with seed "+seed + " and population "+populationSize);
      tester.generate(seed);
      state.testsDone(populationSize);
      TestSuite suite = tester.getSuite();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        if (!test.isFailed()) continue;
        if (!state.check(test)) continue;
        state.addTest(test);
        Scenario scenario = createScenario(test);
        config.setScenario(scenario);
      }
    }
  }

  /**
   * Creates a new generator scenario.
   * 
   * @param test
   * @return
   */
  public Scenario createScenario(TestCase test) {
    Scenario scenario = new Scenario(true);
    List<String> allSteps = test.getAllStepNames();
    Collection<String> steps = new HashSet<>();
    steps.addAll(allSteps);
    NumberOfSteps metric = new NumberOfSteps(test);
    Map<String,Integer> counts = metric.getStepCounts();
    for (String step : steps) {
      scenario.addSlice(step, 0, counts.get(step));
    }
    return scenario;
  }
}

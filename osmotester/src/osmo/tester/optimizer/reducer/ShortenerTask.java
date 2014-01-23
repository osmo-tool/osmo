package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.reducer.debug.TestMetrics;
import osmo.tester.scenario.Scenario;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ShortenerTask implements Runnable {
  private static final Logger log = new Logger(ShortenerTask.class);
  private final OSMOConfiguration osmoConfig;
  private final ReducerState state;
  private final Collection<String> untried = new HashSet<>();
  /** Base seed randomizer for the generators running in this task. */
  private final Randomizer seeder;
  private final int populationSize;
  private static int nextId = 1;

  public ShortenerTask(OSMOConfiguration osmoConfig, long seed, ReducerConfig config, ReducerState state) {
    this.osmoConfig = new OSMOConfiguration(osmoConfig);
    this.state = state;
    this.seeder = new Randomizer(seed);
    this.populationSize = config.getPopulationSize();
  }

  @Override
  public void run() {
    state.resetDone();
    while (!state.isDone()) {
      TestCase previousTest = state.getTest();
      untried.clear();
      untried.addAll(previousTest.getAllStepNames());
      while (untried.size() > 0) {
        String removeMe = untried.iterator().next();
        untried.remove(removeMe);
        Scenario scenario = createScenario(previousTest, removeMe);

        OSMOTester tester = new OSMOTester();
        osmoConfig.setScenario(scenario);
        tester.setConfig(osmoConfig);
        tester.setPrintCoverage(false);
        int newMinimum = state.getMinimum();
        tester.setTestEndCondition(new Length(newMinimum));
        tester.setSuiteEndCondition(new Length(1));
        long seed = seeder.nextLong();
        int id = nextId++;
        log.debug("Starting shortener task "+id+" with seed "+seed + " and population "+populationSize);
        tester.generate(seed);
        TestSuite suite = tester.getSuite();
        List<TestCase> tests = suite.getAllTestCases();

        for (TestCase test : tests) {
          if (!test.isFailed()) continue;
          if (!state.check(test)) {
            log.error("Shortener producer a longer test. This should never happen.");
            continue;
          }
          state.addTest(test);
          untried.clear();
        }
      }
      //if we did not find anything shorter, we finish
      if (state.getTest() == previousTest) {
        state.finish();
      }
    }
  }

  public Scenario createScenario(TestCase test, String removeMe) {
    Scenario scenario = new Scenario(true);
    List<String> allSteps = test.getAllStepNames();
    Collection<String> steps = new HashSet<>();
    steps.addAll(allSteps);
    TestMetrics metric = new TestMetrics(test);
    Map<String,Integer> counts = metric.getStepCounts();
    for (String step : steps) {
      int max = counts.get(step);
      if (step.equals(removeMe)) max--;
      if (max > 0) scenario.addSlice(step, 0, max);
    }
    return scenario;
  }
}

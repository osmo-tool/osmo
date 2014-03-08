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
 * Tries to reduce given test case by removing one step at a time.
 * For example, test has 2 times step A and 3 times step B.
 * This tries to generate a set of tests with 1 A and 3 B. 
 * If this does not reach target, it tries with 2 A and 2 B. Repeat for every step.
 * 
 * @author Teemu Kanstren
 */
public class ShortenerTask implements Runnable {
  private static final Logger log = new Logger(ShortenerTask.class);
  /** The generator configuration. */
  private final OSMOConfiguration osmoConfig;
  /** Current reduction state; found tests, iteration information, etc. */
  private final ReducerState state;
  /** Set of steps that we have not tried to shorten (remove) in current iteration. */
  private final Collection<String> untried = new HashSet<>();
  /** Base seed randomizer for the generators running in this task. Used to generate generator seeds. */
  private final Randomizer seeder;
  /** Number of tests to generate in one iteration. */
  private final int populationSize;
  /** Task iteration counter. */
  private static int nextId = 1;

  /**
   * 
   * @param osmoConfig Generator configuration.
   * @param seed Seed for seeding generators.
   * @param state Current reducer state.
   */
  public ShortenerTask(OSMOConfiguration osmoConfig, long seed, ReducerState state) {
    this.osmoConfig = new OSMOConfiguration(osmoConfig);
    this.state = state;
    this.seeder = new Randomizer(seed);
    this.populationSize = state.getConfig().getPopulationSize();
  }

  @Override
  public void run() {
    state.resetDone();
    while (!state.isDone()) {
      TestCase previousTest = state.getTest();
      //create a list of untried steps as all the steps in the test case. then try to remove each one at a time.
      untried.clear();
      untried.addAll(previousTest.getAllStepNames());
      while (untried.size() > 0) {
        String removeMe = untried.iterator().next();
        untried.remove(removeMe);
        //create a new generator scenario allowing the steps in the test and with the chosen step removed
        Scenario scenario = createScenario(previousTest, removeMe);

        OSMOTester tester = new OSMOTester();
        osmoConfig.setScenario(scenario);
        tester.setConfig(osmoConfig);
        tester.setPrintCoverage(false);
        int newMinimum = state.getMinimum();
        log.debug("removed:"+removeMe+" size now:"+newMinimum);
        tester.setTestEndCondition(new Length(newMinimum));
        //we need to try many as there can be many combinations possible
        tester.setSuiteEndCondition(new Length(populationSize));
        long seed = seeder.nextLong();
        int id = nextId++;
        log.debug("Starting shortener task "+id+" with seed "+seed + " and population "+populationSize);
        tester.generate(seed);
        TestSuite suite = tester.getSuite();
        List<TestCase> tests = suite.getAllTestCases();

        for (TestCase test : tests) {
          //if we debug, we ignore passing tests. if we look for requirements we look at them all
          if (!state.getConfig().isRequirementsSearch() && !test.isFailed()) continue;
          if (!state.check(test)) {
            //in debugging mode this should never happen, in requirements mode can happen often
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

  /**
   * Creates a scenario that can be used to configure the generator for producing shorter test case with given step
   * having maximum one less instance than before.
   * 
   * @param test The test to minimize.
   * @param removeMe We want to have one less of this step in new tests.
   * @return Scenario defining generator configuration for requested test + remove step.
   */
  public Scenario createScenario(TestCase test, String removeMe) {
    //create a strict scenario so undefined steps are forbidden
    //TODO: should reuse generator scenario and not overwrite one if user has define one before
    Scenario scenario = new Scenario(true);
    List<String> allSteps = test.getAllStepNames();
    Collection<String> steps = new HashSet<>();
    steps.addAll(allSteps);
    NumberOfSteps metric = new NumberOfSteps(test);
    Map<String,Integer> counts = metric.getStepCounts();
    for (String step : steps) {
      //find the one to reduce and reduce..
      int max = counts.get(step);
      if (step.equals(removeMe)) max--;
      //if we dont define it and scenario is strict, step is forbidden
      if (max > 0) scenario.addSlice(step, 0, max);
    }
    return scenario;
  }
}

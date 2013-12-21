package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.scenario.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ReducerTask implements Runnable {
  private static final Logger log = new Logger(ReducerTask.class);
  private OSMOConfiguration config;
  /** Base seed randomizer for the generators running in this task. */
  private final Randomizer seeder;
  private ReducerState state;
  private final int populationSize;
  private static int nextId = 1;

  public ReducerTask(OSMOConfiguration config, long seed, int populationSize, ReducerState state) {
    this.config = config;
    this.seeder = new Randomizer(seed);
    this.state = state;
    this.populationSize = populationSize;
  }

  @Override
  public void run() {
    try {
      runrun();
    } catch (Exception e) {
      log.error("Failed to run reducer task", e);
    }
  }
  
  public void runrun() {
    while (!state.isDone()) {
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.setPrintCoverage(false);
      int newMinimum = state.getMinimum();
//      int newMinimum = state.getMinimum()-1;
      if (newMinimum <= 0) {
        log.info("Stopping due to new minimun "+newMinimum);
        state.finish();
        return;
      }
      tester.setTestEndCondition(new Length(newMinimum));
      tester.setSuiteEndCondition(new Length(populationSize));
      long seed = seeder.nextLong();
      int id = nextId++;
      log.info("Starting reducer task "+id+" with seed "+seed + " and population "+populationSize);
      tester.generate(seed);
      state.testsDone(populationSize);
      TestSuite suite = tester.getSuite();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        if (!test.isFailed()) continue;
        if (!state.check(test)) continue;
        String filename = state.addTest(test);
//        System.out.println("filename:"+filename);
        if (filename != null) {
          List<TestCase> smallest = new ArrayList<>();
          smallest.add(test);
          OSMOTester.writeTrace(filename, smallest, test.getSeed(), config);
        }
        Scenario scenario = createScenario(test);
        config.setScenario(scenario);
      }
    }
  }

  public Scenario createScenario(TestCase test) {
    Scenario scenario = new Scenario(true);
    List<String> allSteps = test.getAllStepNames();
    Collection<String> steps = new HashSet<>();
    steps.addAll(allSteps);
    TestMetrics metric = new TestMetrics(test);
    Map<String,Integer> counts = metric.getStepCounts();
    for (String step : steps) {
      scenario.addSlice(step, 0, counts.get(step));
    }
    return scenario;
  }
}

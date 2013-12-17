package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.List;

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
    while (!state.isDone()) {
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.setPrintCoverage(false);
      int newMinimum = state.getMinimum() - 1;
      if (newMinimum <= 0) {
        state.finish();
        return;
      }
      tester.setTestEndCondition(new Length(newMinimum));
      tester.setSuiteEndCondition(new Length(populationSize));
      long seed = seeder.nextLong();
      int id = nextId++;
      log.info("Starting reducer task "+id+" with seed "+seed + " and population "+populationSize);
      tester.generate(seed);
      TestSuite suite = tester.getSuite();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        int failedLength = test.getAllStepNames().size();
        if (state.check(test, failedLength)) {
          List<TestCase> smallest = new ArrayList<>();
          smallest.add(test);
          OSMOTester.writeTrace("osmo-output/reducer-"+failedLength, smallest, test.getSeed(), config);
        }
      }
    }
    
  }
}

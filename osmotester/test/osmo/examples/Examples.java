package osmo.examples;

import org.junit.Ignore;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.listener.TracePrinter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.greedy.MultiGreedy;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;
import osmo.tester.unittests.testmodels.VendingMachine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren.
 */
public class Examples {
  @Test
  public void greedy() {
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setTestEndCondition(new LengthProbability(10, 20, 0.2d));
    oc.setFactory(models -> models.add(new VendingMachine()));
    ScoreConfiguration config = new ScoreConfiguration();
    config.setLengthWeight(0);
    MultiGreedy mg = new MultiGreedy(oc, config, 111, 1);
    mg.setPopulationSize(500);
    mg.setMaxIterations(5);
    List<TestCase> tests = mg.search();
    int i = 1;
    for (TestCase test : tests) {
      TestUtils.write(test.getAttribute("script").toString(), "example/tests/"+test.getName()+".txt");
      i++;
    }
    TestCoverage coverage = mg.getFinalCoverage();
    HTMLCoverageReporter html = new HTMLCoverageReporter(coverage, tests, mg.getFsm());
    String matrix = html.getTraceabilityMatrix();
    TestUtils.write(matrix, "example/example_report.html");
  }

  @Test
  public void online() {
    OSMOTester tester = new OSMOTester();
    OSMOConfiguration oc = tester.getConfig();
    oc.addListener(new TracePrinter());
    oc.setTestEndCondition(new LengthProbability(10, 20, 0.2d));
    oc.setSuiteEndCondition(new Length(10));
    oc.setFactory(models -> models.add(new VendingMachine()));
    ScoreConfiguration config = new ScoreConfiguration();
    config.setLengthWeight(0);
    tester.generate(112);
  }

  @Test
  public void findReduce() {
    for (int i = 0 ; i < 1000 ; i++) {
      OSMOTester tester = new OSMOTester();
      OSMOConfiguration oc = tester.getConfig();
      oc.setTestEndCondition(new Length(20));
      oc.setSuiteEndCondition(new Length(100));
      oc.setFactory(models -> models.add(new VendingMachineWithFails(270, 8)));
      int seed = 111 + i * 100;
      System.out.println("attempting seed:"+seed);
      try {
        tester.generate(seed);
      } catch (Exception e) {
        e.printStackTrace();
        TestSuite suite = tester.getSuite();
        List<TestCase> allTests = suite.getAllTestCases();
        TestCase test = allTests.get(allTests.size()-1);
        List<String> steps = test.getAllStepNames();
        System.out.println(steps);
        break;
      }
    }
  }

  @Test
  public void reducer1() {
//    tester.generate(16311);
    ReducerConfig config = new ReducerConfig(16311);
    config.setParallelism(1);
    config.setInitialTime(TimeUnit.MINUTES, 10);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.MINUTES, 10);
    config.setTargetLength(15);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration oc = reducer.getOsmoConfig();
    oc.setTestEndCondition(new Length(20));
    oc.setSuiteEndCondition(new Length(100));
    oc.setFactory(models -> models.add(new VendingMachineWithFails(270, 8)));
    reducer.search();
  }

  @Ignore
  @Test
  public void reducer2() {
    ReducerConfig config = new ReducerConfig(16311);
    config.setParallelism(4);
    config.setInitialTime(TimeUnit.MINUTES, 10);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.MINUTES, 60);
    config.setTargetLength(15);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration oc = reducer.getOsmoConfig();
    oc.setTestEndCondition(new Length(100));
    oc.setSuiteEndCondition(new Length(10));
    oc.setFactory(models -> models.add(new VendingMachineWithFails(-1, -1)));
    reducer.search();
  }
}

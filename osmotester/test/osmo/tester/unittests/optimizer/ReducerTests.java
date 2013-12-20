package osmo.tester.unittests.optimizer;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.reducer.Analyzer;
import osmo.tester.optimizer.reducer.Invariants;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerState;
import osmo.tester.optimizer.reducer.ReducerTask;
import osmo.tester.optimizer.reducer.TestMetrics;
import osmo.tester.scenario.Scenario;
import osmo.tester.scenario.Slice;
import osmo.tester.unittests.testmodels.ErrorModelProbability;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ReducerTests {
  @Test
  public void probableModel() throws Exception {
    TestUtils.recursiveDelete("osmo-output");
    Reducer reducer = new Reducer(1, 111);
    OSMOConfiguration config = reducer.getConfig();
    config.setFactory(new ReflectiveModelFactory(ErrorModelProbability.class));
    config.setTestEndCondition(new Length(50));
    config.setSuiteEndCondition(new Length(20));
    ReducerState state = reducer.search(5, TimeUnit.SECONDS, 50, 10);
    List<TestCase> tests = state.getTests();
    assertEquals("Number of tests", 1, tests.size());
    TestCase test1 = tests.get(0);
    assertEquals("Final test length", 1, test1.getAllStepNames().size());
    assertEquals("Iteration lengths", "[5, 3, 1]", state.getLengths().toString());
    String report = TestUtils.readFile("osmo-output/reducer.txt", "UTF8");
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals("Reducer report", expected, report);
    List<String> files = TestUtils.listFiles("osmo-output", ".html", false);
    for (Iterator<String> i = files.iterator() ; i.hasNext() ; ) {
      String file = i.next();
      if (!file.startsWith("reducer")) i.remove();
    }
    assertEquals("Generated report files", "[reducer-1-1.html, reducer-3-1.html, reducer-5-1.html]", files.toString());
  }
  
  @Test
  public void scenarioBuilding() {
    ReducerTask task = new ReducerTask(null, 0, 0, null);
    TestCase test = new TestCase(0);
    test.addStep(new FSMTransition("hello1"));
    test.addStep(new FSMTransition("hello2"));
    test.addStep(new FSMTransition("hello2"));
    Scenario scenario = task.createScenario(test);
    List<Slice> slices = scenario.getSlices();
    assertEquals("Number of slices", 2, slices.size());
    Slice slice1 = null;
    Slice slice2 = null;
    for (Slice slice : slices) {
      if (slice.getStepName().equals("hello1")) slice1 = slice;
      if (slice.getStepName().equals("hello2")) slice2 = slice;
    }
    assertEquals("Slice 1 name", "hello1", slice1.getStepName());
    assertEquals("Slice 1 min", 0, slice1.getMin());
    assertEquals("Slice 1 max", 1, slice1.getMax());
    assertEquals("Slice 2 name", "hello2", slice2.getStepName());
    assertEquals("Slice 2 min", 0, slice2.getMin());
    assertEquals("Slice 2 max", 2, slice2.getMax());
  }

  @Test
  public void metrics() {
    TestCase test22 = createTest22();
    TestMetrics metrics = new TestMetrics(test22);
    Map<String, Integer> counts = metrics.getStepCounts();
    assertEquals("Number of steps", 10, counts.size());
    assertEquals("'Unlock PIN bad' count", 10, (int)counts.get("Unlock PIN bad"));
    assertEquals("'Select EF LP' count", 2, (int)counts.get("Select EF LP"));
    assertEquals("'Select DF Roaming' count", 1, (int)counts.get("Select DF Roaming"));
    assertEquals("'Read Binary' count", 3, (int)counts.get("Read Binary"));
    assertEquals("'Select EF FR' count", 1, (int)counts.get("Select EF FR"));
    assertEquals("'Enable PIN 11' count", 1, (int)counts.get("Enable PIN 11"));
    assertEquals("'Select MF' count", 1, (int)counts.get("Select MF"));
    assertEquals("'Change new PIN' count", 1, (int)counts.get("Change new PIN"));
    assertEquals("'Select DF GSM' count", 1, (int)counts.get("Select DF GSM"));
    assertEquals("'Select EF IMSI' count", 1, (int)counts.get("Select EF IMSI"));
  }

  @Test
  public void invariants() {
    TestCase test22 = createTest22();
    TestCase test27 = createTest27();
    TestCase test30 = createTest30();
    TestCase test39 = createTest39();
    Analyzer analyzer = new Analyzer(null);
    Invariants invariants = analyzer.analyze(test22, test27, test30, test39);
    assertEquals("'Unlock PIN bad' min", 10, invariants.minFor("Unlock PIN bad"));
    assertEquals("'Unlock PIN bad' max", 11, invariants.maxFor("Unlock PIN bad"));
    assertEquals("Last steps", "[Read Binary]", invariants.getLastSteps().toString());
    assertEquals("Patterns", "", invariants.getPatterns());
  }

  @Test
  public void report() {
    TestCase test22 = createTest22();
    TestCase test27 = createTest27();
    TestCase test30 = createTest30();
    TestCase test39 = createTest39();
    ReducerState state = new ReducerState(50);
    state.addTest(test39);
    state.testsDone(50);
    state.addTest(test30);
    state.testsDone(50);
    state.addTest(test27);
    state.testsDone(50);
    state.addTest(test22);
    state.testsDone(50);
    Analyzer analyzer = new Analyzer(state);
    analyzer.analyze();
    String report = analyzer.createReport();
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer2.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals("Reducer report", expected, report);
  }
  
  private TestCase createTest22() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Unlock PIN bad"));//1 //1
    test.addStep(new FSMTransition("Unlock PIN bad"));    //2
    test.addStep(new FSMTransition("Unlock PIN bad"));    //3
    test.addStep(new FSMTransition("Select EF LP")); //2
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Select DF Roaming")); //3
    test.addStep(new FSMTransition("Read Binary")); //4
    test.addStep(new FSMTransition("Select EF FR")); //5
    test.addStep(new FSMTransition("Unlock PIN bad"));    //4
    test.addStep(new FSMTransition("Unlock PIN bad"));    //5
    test.addStep(new FSMTransition("Unlock PIN bad"));    //6
    test.addStep(new FSMTransition("Unlock PIN bad"));    //7
    test.addStep(new FSMTransition("Enable PIN 11")); //6
    test.addStep(new FSMTransition("Select MF")); //7
    test.addStep(new FSMTransition("Unlock PIN bad"));    //8
    test.addStep(new FSMTransition("Change new PIN")); //8
    test.addStep(new FSMTransition("Select DF GSM")); //9
    test.addStep(new FSMTransition("Select EF IMSI"));//10
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Unlock PIN bad"));   //9
    test.addStep(new FSMTransition("Unlock PIN bad")); //10
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest27() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Verify PIN 12"));
    test.addStep(new FSMTransition("Change same PIN"));
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Verify PIN 12"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //1
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Verify PIN 12"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Verify PIN 11"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //2
    test.addStep(new FSMTransition("Unlock PIN bad")); //3
    test.addStep(new FSMTransition("Unlock PIN bad")); //4
    test.addStep(new FSMTransition("Verify PIN 11"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //5
    test.addStep(new FSMTransition("Unlock PIN bad")); //6
    test.addStep(new FSMTransition("Unlock PIN bad")); //7
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Enable PIN 11"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //8
    test.addStep(new FSMTransition("Unlock PIN bad")); //9
    test.addStep(new FSMTransition("Change same PIN"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //9
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest30() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //1
    test.addStep(new FSMTransition("Unlock PIN bad")); //2
    test.addStep(new FSMTransition("Enable PIN 11"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Verify PIN 11"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //3
    test.addStep(new FSMTransition("Enable PIN 11"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Select MF"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //4
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //5
    test.addStep(new FSMTransition("Unlock PIN bad")); //6
    test.addStep(new FSMTransition("Unlock PIN bad")); //7
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //8
    test.addStep(new FSMTransition("Select DF Roaming"));
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //9
    test.addStep(new FSMTransition("Unlock PIN bad")); //10
    test.addStep(new FSMTransition("Verify PIN 11"));
    test.addStep(new FSMTransition("Change same PIN"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest39() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Unlock PIN bad")); //1
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //2
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //3
    test.addStep(new FSMTransition("Unlock PIN bad")); //4
    test.addStep(new FSMTransition("Change new PIN"));
    test.addStep(new FSMTransition("Read Binary"));
    test.addStep(new FSMTransition("Unlock PIN bad"));  //5
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Unlock PIN bad"));  //6
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Verify PIN 12"));
    test.addStep(new FSMTransition("Verify PIN 11"));
    test.addStep(new FSMTransition("Change same PIN"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Select MF"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Select DF Roaming"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Unlock PIN bad"));  //7
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Change new PIN"));
    test.addStep(new FSMTransition("Unlock PIN bad"));  //8
    test.addStep(new FSMTransition("Verify PIN 12"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //9
    test.addStep(new FSMTransition("Unlock PIN bad")); //10
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Select EF LP"));
    test.addStep(new FSMTransition("Unlock PIN bad")); //11
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Select EF FR"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }
}

package osmo.tester.unittests.optimizer;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.reducer.Analyzer;
import osmo.tester.optimizer.reducer.FuzzerTask;
import osmo.tester.optimizer.reducer.debug.Invariants;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.optimizer.reducer.ReducerState;
import osmo.tester.optimizer.reducer.debug.invariants.FlexPrecedence;
import osmo.tester.optimizer.reducer.debug.invariants.NumberOfSteps;
import osmo.tester.scenario.Scenario;
import osmo.tester.scenario.Slice;
import osmo.tester.unittests.ScriptBuilder;
import osmo.tester.unittests.testmodels.EmptyTestModel1;
import osmo.tester.unittests.testmodels.ErrorModelProbability;
import osmo.tester.unittests.testmodels.Model10Debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ReducerTests {
  @Test
  public void nothingFound() throws Exception {
//    Logger.fileLevel = Level.FINEST;
//    Logger.consoleLevel = Level.FINEST;
    Logger.packageName = "o.t.o.r";
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new ReflectiveModelFactory(EmptyTestModel1.class));
    config.setInitialTime(TimeUnit.SECONDS, 5);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.SECONDS, 5);
    config.setPopulationSize(500);
    config.setLength(10);
    ReducerState state = reducer.search();
    List<TestCase> tests = state.getTests();
    int testCount = state.getTestCount();
    assertTrue("Number of tests should be > 1000, was "+ testCount, testCount > 1000);
    assertEquals("Tests found", 0, tests.size());
  }

  @Test
  public void probableModel() throws Exception {
//    Logger.consoleLevel = Level.FINEST;
    Logger.packageName = "o.t.o.r";
    TestUtils.recursiveDelete("osmo-output");
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new ReflectiveModelFactory(ErrorModelProbability.class));
//    osmoConfig.setTestEndCondition(new Length(50));
//    osmoConfig.setSuiteEndCondition(new Length(20));
    config.setInitialTime(TimeUnit.SECONDS, 5);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.SECONDS, 5);
    config.setPopulationSize(50);
    config.setLength(10);
    config.setTestMode(true);
    ReducerState state = reducer.search();
    List<TestCase> tests = state.getTests();
    assertEquals("Number of tests", 1, tests.size());
    TestCase test1 = tests.get(0);
    assertEquals("Final test length", 1, test1.getAllStepNames().size());
    assertEquals("Iteration lengths", "[]", state.getLengths().toString());
    String report = TestUtils.readFile("osmo-output/reducer-111/reducer-final.txt", "UTF8");
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals("Reducer report", expected, report);
    List<String> files = TestUtils.listFiles("osmo-output/reducer-111", ".html", false);
    assertEquals("Generated report files", "[final-tests.html]", files.toString());
  }

  @Test
  public void model10() throws Exception {
    Logger.consoleLevel = Level.INFO;
    Logger.packageName = "o.t.o.r";
    ReducerConfig config = new ReducerConfig(111);
    config.setStrictReduction(false);
    config.setParallelism(1);
    //changed here on 8apr15
    config.setInitialTime(TimeUnit.SECONDS, 10);
    config.setFuzzTime(TimeUnit.SECONDS, 10);
    config.setShorteningTime(TimeUnit.SECONDS, 10);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new ReflectiveModelFactory(Model10Debug.class));
    //TODO: move this to config object
    reducer.setDeleteOldOutput(true);
    // config.setTotalTime(TimeUnit.SECONDS, 1);
    //TODO: length asettaa siis oikeasti sen pituuden ja jos osmo-config on mitään asetettu pitäisi herjata
    config.setPopulationSize(1500);
    config.setLength(50);
    config.setTestMode(true);
    ReducerState state = reducer.search();
    List<TestCase> tests = state.getTests();
    // assertEquals("Number of tests", 230, tests.size());
    TestCase test1 = tests.get(0);
    //this does not produce multiple tests as the initial fuzz only stores the shortest of all found failures
    //since finding equally short failing tests by fuzzing is very unlikely, we end up with just one
    //although it would be possible to have others as well..
    // assertEquals("Final test length", 11, test1.getAllStepNames().size());
    // assertEquals("Iteration lengths", "[25, 22, 17, 14, 13, 12, 11]", state.getLengths().toString());
    String report = TestUtils.readFile("osmo-output/reducer-111/reducer-final.txt", "UTF8");
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer3.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    String[] replaced = TestUtils.replace("##", expected, report);
    report = replaced[0];
    expected = replaced[1];
    assertEquals("Reducer report", expected, report);
    List<String> files = TestUtils.listFiles("osmo-output/reducer-111", ".html", false);
    assertEquals("Generated report files", "[final-tests.html]", files.toString());
  }

  @Test
  public void startTest() throws Exception {
//    Logger.consoleLevel = Level.FINEST;
    Logger.packageName = "o.t.o.r";
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    //changed here on 8apr15
    config.setInitialTime(TimeUnit.MINUTES, 10);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.MINUTES, 10);
    config.setTargetLength(11);
    Reducer reducer = new Reducer(config);
    ScriptBuilder builder = new ScriptBuilder();
    builder.add(5, "Step4");
    builder.add(5, "Step6");
    builder.add(1, "Step8");
    TestCase test = builder.create(11, new ReflectiveModelFactory(Model10Debug.class));
    reducer.setStartTest(test);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new ReflectiveModelFactory(Model10Debug.class));
    reducer.setDeleteOldOutput(true);
    config.setPopulationSize(1500);
    config.setLength(50);
    config.setTestMode(true);
    ReducerState state = reducer.search();
    List<TestCase> tests = state.getTests();
    TestCase test1 = tests.get(0);
    String report = TestUtils.readFile("osmo-output/reducer-111/reducer-final.txt", "UTF8");
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer4.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    String[] replaced = TestUtils.replace("##", expected, report);
    report = replaced[0];
    expected = replaced[1];
    assertEquals("Reducer report", expected, report);
    List<String> files = TestUtils.listFiles("osmo-output/reducer-111", ".html", false);
    assertEquals("Generated report files", "[final-tests.html]", files.toString());
    System.out.println("done");
  }

  @Test
  public void fuzzerScenarioBuilding() {
    ReducerState state = new ReducerState(null, new ReducerConfig(111));
    FuzzerTask task = new FuzzerTask(new OSMOConfiguration(), null, 0, state);
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
    NumberOfSteps metrics = new NumberOfSteps(test22);
    Map<String, Integer> counts = metrics.getStepCounts();
    assertEquals("Number of steps", 10, counts.size());
    assertEquals("'Unlock PIN bad' count", 10, (int) counts.get("Unlock PIN bad"));
    assertEquals("'Select EF LP' count", 2, (int) counts.get("Select EF LP"));
    assertEquals("'Select DF Roaming' count", 1, (int) counts.get("Select DF Roaming"));
    assertEquals("'Read Binary' count", 3, (int) counts.get("Read Binary"));
    assertEquals("'Select EF FR' count", 1, (int) counts.get("Select EF FR"));
    assertEquals("'Enable PIN 11' count", 1, (int) counts.get("Enable PIN 11"));
    assertEquals("'Select MF' count", 1, (int) counts.get("Select MF"));
    assertEquals("'Change new PIN' count", 1, (int) counts.get("Change new PIN"));
    assertEquals("'Select DF GSM' count", 1, (int) counts.get("Select DF GSM"));
    assertEquals("'Select EF IMSI' count", 1, (int) counts.get("Select EF IMSI"));
  }

  @Test
  public void flexPrecedence() {
    List<String> allSteps = new ArrayList<>();
    allSteps.add("A");
    allSteps.add("B");
    allSteps.add("C");
    allSteps.add("D");
    allSteps.add("E");
    FlexPrecedence fp = new FlexPrecedence(allSteps);
    List<String> t1 = new ArrayList<>();
    t1.add("A");
    t1.add("B");
    t1.add("B");
    t1.add("C");
    t1.add("B");
    t1.add("B");
    t1.add("D");
    t1.add("E");
    List<String> t2 = new ArrayList<>();
    t2.add("A");
    t2.add("C");
    t2.add("A");
    t2.add("B");
    t2.add("A");
//    t2.add("B");
    t2.add("C");
    t2.add("D");
    fp.process(t1);
    fp.process(t2);
    Collection<String> patterns = fp.getPatterns();
    assertEquals("Precedence patterns", "[A->B, A->C, A->D, A->E, B->D, B->E, C->D, C->E, D->E]", patterns.toString());
  }

  @Test
  public void invariants() {
    TestCase test14_1 = createTest14_1();
    TestCase test14_2 = createTest14_2();
    TestCase test14_3 = createTest14_3();
    TestCase test14_4 = createTest14_4();
    TestCase test22 = createTest22();
    TestCase test27 = createTest27();
    TestCase test30 = createTest30();
    TestCase test39 = createTest39();
    ReducerState state = new ReducerState(null, new ReducerConfig(111));
    Analyzer analyzer = new Analyzer(createStepList(), state);
    Invariants invariants = analyzer.analyze(test14_1, test14_2, test14_3, test14_4, test22, test27, test30, test39);
    assertEquals("Step counts", "[Unlock PIN bad : 10-11, Select EF LP : 0-5, Select DF Roaming : 0-1, Select EF FR : 0-4, Enable PIN 11 : 0-2, Select MF : 0-1, Change new PIN : 0-2, Select DF GSM : 1-3, Select EF IMSI : 1-3, Read Binary : 1-4, Verify PIN 11 : 0-2, Verify PIN 12 : 0-3, Disable PIN OK : 0-4, Change same PIN : 0-2]", invariants.getUsedStepCounts().toString());
    assertEquals("Missing steps", "[Miss me]", invariants.getMissingSteps().toString());
    assertEquals("Last steps", "[Read Binary]", invariants.getLastSteps().toString());
    assertEquals("Strict precedences", "[Select DF GSM->Select EF IMSI, Unlock PIN bad->Select EF IMSI]", invariants.getStrictPrecedences().toString());
    assertEquals("Flex precedences", "[Change new PIN->Change same PIN, Change new PIN->Select EF IMSI, Change new PIN->Verify PIN 11, Change new PIN->Verify PIN 12, Change same PIN->Select EF IMSI, Disable PIN OK->Change new PIN, Disable PIN OK->Select DF Roaming, Disable PIN OK->Select MF, Enable PIN 11->Change new PIN, Enable PIN 11->Select MF, Read Binary->Change new PIN, Select DF GSM->Change same PIN, Select DF GSM->Select EF IMSI, Select DF GSM->Verify PIN 11, Select DF GSM->Verify PIN 12, Select DF Roaming->Select EF IMSI, Select MF->Select EF IMSI, Unlock PIN bad->Change new PIN, Unlock PIN bad->Enable PIN 11, Unlock PIN bad->Select DF Roaming, Unlock PIN bad->Select EF FR, Unlock PIN bad->Select EF IMSI, Unlock PIN bad->Select EF LP, Unlock PIN bad->Select MF, Unlock PIN bad->Verify PIN 11, Verify PIN 11->Select DF Roaming, Verify PIN 11->Select MF, Verify PIN 12->Change same PIN, Verify PIN 12->Enable PIN 11, Verify PIN 12->Select DF Roaming, Verify PIN 12->Select EF IMSI, Verify PIN 12->Select MF, Verify PIN 12->Verify PIN 11]", invariants.getFlexPrecedences().toString());
    assertEquals("Sequences", "[[Read Binary], [Select DF GSM], [Select EF IMSI], [Unlock PIN bad, Unlock PIN bad]]", invariants.getSequences().toString());
  }

  @Test
  public void report() {
    TestCase test14_1 = createTest14_1();
    TestCase test14_2 = createTest14_2();
    TestCase test14_3 = createTest14_3();
    TestCase test14_4 = createTest14_4();
    TestCase test22 = createTest22();
    TestCase test27 = createTest27();
    TestCase test30 = createTest30();
    TestCase test39 = createTest39();
    ReducerConfig config = new ReducerConfig(111);
    ReducerState state = new ReducerState(createStepList(), config);
    state.startFinalFuzz();
    state.addTest(test39);
    state.testsDone(50);
    state.addTest(test30);
    state.testsDone(50);
    state.addTest(test27);
    state.testsDone(50);
    state.addTest(test22);
    state.testsDone(50);
    state.addTest(test14_4);
    state.testsDone(50);
    state.addTest(test14_3);
    state.testsDone(50);
    state.addTest(test14_2);
    state.testsDone(50);
    state.addTest(test14_1);
    state.testsDone(50);
    Analyzer analyzer = new Analyzer(createStepList(), state);
    analyzer.analyze();
    String report = analyzer.createReport();
    String expected = TestUtils.getResource(ReducerTests.class, "expected-reducer2.txt");
    report = TestUtils.unifyLineSeparators(report, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals("Reducer report", expected, report);
  }

  private TestCase createTest14_1() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest14_2() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest14_3() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
  }

  private TestCase createTest14_4() {
    TestCase test = new TestCase(1);
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Disable PIN OK"));
    test.addStep(new FSMTransition("Select DF GSM"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Select EF IMSI"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Unlock PIN bad"));
    test.addStep(new FSMTransition("Read Binary"));
    return test;
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

  private List<String> createStepList() {
    List<String> steps = new ArrayList<>();
    steps.add("Unlock PIN bad");
    steps.add("Select EF LP");
    steps.add("Select DF Roaming");
    steps.add("Select EF FR");
    steps.add("Enable PIN 11");
    steps.add("Select MF");
    steps.add("Change new PIN");
    steps.add("Select DF GSM");
    steps.add("Select EF IMSI");
    steps.add("Read Binary");
    steps.add("Verify PIN 11");
    steps.add("Verify PIN 12");
    steps.add("Disable PIN OK");
    steps.add("Change same PIN");
    steps.add("Miss me");
    return steps;
  }
}

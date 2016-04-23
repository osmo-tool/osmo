package osmo.tester.unittests.generation;

import static java.util.Arrays.stream;

import java.io.File;

import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.TestModels;
import osmo.tester.optimizer.multiosmo.GeneratorTask;
import osmo.tester.optimizer.multiosmo.MultiOSMO;
import osmo.tester.unittests.testmodels.ErrorModelSleepy;
import osmo.tester.unittests.testmodels.ValidTestModel2;

import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class MultiOSMOTests {
  private static final String OUTPUT_FOLDER = "osmo-output";

  @Before
  public void clearFiles() {
    final File outputFolder = new File(OUTPUT_FOLDER);
    stream(outputFolder.listFiles()).forEach(file -> file.delete());
  }

  @After
  public void restore() {
    TestUtils.endOutputCapture();
  }
  
  @Test
  public void defaultFactory() {
    MultiOSMO mosmo = new MultiOSMO(4);
    TestUtils.startOutputCapture();
    try {
      mosmo.generate(new Time(1), 444);
      fail("Generation without any model objects should fail.");
    } catch (Exception e) {
      //expected
    }
    String output = TestUtils.getOutput();
    assertEquals("Message for default factory", MultiOSMO.ERROR_MSG+System.getProperty("line.separator"), output);
  }

  @Test
  public void generate4() throws Exception {
    Thread.sleep(100);
    MultiOSMO mosmo = new MultiOSMO(4);
    OSMOConfiguration config = mosmo.getConfig();
    config.setSequenceTraceRequested(true);
    config.setFactory(new MyModelFactory());
    config.setTestEndCondition(new Length(10));
    config.setSuiteEndCondition(new Time(2));
    mosmo.generate(new Time(1), 444);
//    List<String> reports = TestUtils.listFiles(OUTPUT_FOLDER, ".csv", false);
//    assertEquals("Number of reports generated", 4, reports.size());
    List<String> traces = TestUtils.listFiles(OUTPUT_FOLDER, ".html", false);
    assertEquals("Number of HTML traces generated", 4, traces.size());
    List<String> xmls = TestUtils.listFiles(OUTPUT_FOLDER, ".xml", false);
    assertEquals("Number of XML traces generated", 4, xmls.size());
  }

  @Ignore
  @Test
  public void generate4times3() {
    MultiOSMO mosmo = new MultiOSMO(4);
    OSMOConfiguration config = mosmo.getConfig();
    config.setSequenceTraceRequested(true);
    config.setFactory(new MyModelFactory());
    config.setTestEndCondition(new Length(10));
    config.setSuiteEndCondition(new Time(2));
    mosmo.generate(new Time(6), 444);
    List<String> reports = TestUtils.listFiles(OUTPUT_FOLDER, ".csv", false);
    assertEquals("Number of reports generated", 5, reports.size());
    List<String> traces = TestUtils.listFiles(OUTPUT_FOLDER, ".html", false);
    assertEquals("Number of traces generated", 4, traces.size());
  }
  
  @Test
  public void errorInTest() {
    MultiOSMO mosmo = new MultiOSMO(2);
    OSMOConfiguration config = mosmo.getConfig();
    config.setSequenceTraceRequested(true);
    config.setFactory(new ErrorModelFactory());
    config.setTestEndCondition(new Length(30));
    config.setSuiteEndCondition(new Length(2));
    config.setStopGenerationOnError(false);
    GeneratorTask.Result result = mosmo.generate(new Time(1), 444);
    int steps = result.getCoverage().getTotalSteps();
    assertTrue("Number of generated steps from MOSMO should be less than 120 (generators (2) x steps (30) x tests (2) configured) due to assertion e in model, was "+steps, steps < 1200);
    assertTrue("Number of generated steps from MOSMO should be divisible by 2 due to e in step 2", steps %2 == 0);
    //if we run long test sets, the value is bigger due to some JVM optimizations. just one test is shorter
    assertTrue("Number of generated steps from MOSMO should be 12 to 20([6, 8, 10] x 2) was " + steps, 12 <= steps && steps <= 20);
  }

  private static class MyModelFactory implements ModelFactory {
    @Override
    public void createModelObjects(TestModels addHere) {
      addHere.add(new ValidTestModel2(new Requirements()));
    }
  }

  private static class ErrorModelFactory implements ModelFactory {
    @Override
    public void createModelObjects(TestModels addHere) {
      addHere.add(new ErrorModelSleepy());
    }
  }
}

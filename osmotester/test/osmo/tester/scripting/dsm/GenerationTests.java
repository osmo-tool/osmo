package osmo.tester.scripting.dsm;

import org.junit.Test;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.model.dataflow.ScriptedValueProvider;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static osmo.common.TestUtils.getResource;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class GenerationTests {
  @Test
  public void generateMinOnly() throws Exception {
    DSMConfiguration config = new DSMConfiguration();
    config.addStepMin("increase", 1);
    config.addStepMin("decrease", 1);
    DataCoverageRequirement counterReq = new DataCoverageRequirement("counter");
    counterReq.addRequirement(3);
    config.add(counterReq);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.dsm.TestModelFactory");
    config.setSeed(233);
    String expected = getResource(getClass(), "expected-generate1.txt");
    expected = unifyLineSeparators(expected, "\n");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    PrintStream old = System.out;
    System.setOut(out);
    DSMMain.execute(config);
    System.setOut(old);
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }

  @Test
  public void generateExact() throws Exception {
    DSMConfiguration config = new DSMConfiguration();
    config.addStepMin("decrease", 1);
    config.addStepMax("decrease", 1);
    DataCoverageRequirement counterReq = new DataCoverageRequirement("counter");
    counterReq.addRequirement(3);
    config.add(counterReq);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.dsm.TestModelFactory");
    config.setSeed(233);
    String expected = getResource(getClass(), "expected-generate2.txt");
    expected = unifyLineSeparators(expected, "\n");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    PrintStream old = System.out;
    System.setOut(out);
    DSMMain.execute(config);
    System.setOut(old);
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }

  @Test
  public void scriptedWithInvalidValues() throws Exception {
    DSMConfiguration config = new DSMConfiguration();
    ScriptedValueProvider scripter = new ScriptedValueProvider();
    scripter.addValue("set", "1");
    scripter.addValue("set", "2");
    config.setScripter(scripter);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.dsm.TestModelFactory2");
    config.setSeed(233);
    try {
      DSMMain.execute(config);
      fail("Scripted generation with invalid values should fail.");
    } catch (Exception e1) {
      Throwable t = e1.getCause().getCause();
      assertEquals("Error message for invalid scripted values", "Requested scripted value for variable 'set' not found: 1", t.getMessage());
    }
  }

  @Test
  public void scriptedWithSetValues() throws Exception {
    DSMConfiguration config = new DSMConfiguration();
    ScriptedValueProvider scripter = new ScriptedValueProvider();
    scripter.addValue("set", "v1");
    scripter.addValue("set", "v2");
//    config.setScripter(scripter);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.dsm.TestModelFactory2");
    config.setSeed(233);
    String expected = getResource(getClass(), "expected-generate3.txt");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    PrintStream old = System.out;
    System.setOut(out);
    DSMMain.execute(config);
    System.setOut(old);
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }
}

package osmo.tester.scripting.slicing;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.model.ScriptedValueProvider;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static osmo.common.TestUtils.getResource;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class GenerationTests {
  private PrintStream old = null;
  private ByteArrayOutputStream bos = null;
  
  @Before
  public void setup() {
    old = System.out;
    bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    System.setOut(out);
  }
  
  private void assertResult(String filename) {
    System.setOut(old);
    String expected = getResource(getClass(), filename);
    expected = unifyLineSeparators(expected, "\n");
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }
  
  @Test
  public void generateMinOnly() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    config.addStepMin("increase", 1);
    config.addStepMin("decrease", 1);
    DataCoverageRequirement counterReq = new DataCoverageRequirement("counter");
    counterReq.addRequirement(3);
    config.add(counterReq);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertResult("expected-generate1.txt");
  }

  @Test
  public void generateExact() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    config.addStepMin("decrease", 1);
    config.addStepMax("decrease", 1);
    DataCoverageRequirement counterReq = new DataCoverageRequirement("counter");
    counterReq.addRequirement(3);
    config.add(counterReq);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertResult("expected-generate2.txt");
  }

  @Test
  public void scriptedWithInvalidValues() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    ScriptedValueProvider scripter = new ScriptedValueProvider();
    scripter.addValue("set", "1");
    scripter.addValue("set", "2");
    config.setScripter(scripter);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory2");
    config.setSeed(233);
    try {
      SlicerMain.execute(config);
      fail("Scripted generation with invalid values should fail.");
    } catch (Exception e1) {
      Throwable t = e1.getCause().getCause();
      assertEquals("Error message for invalid scripted values", "Requested scripted value for variable 'set' not found: 1", t.getMessage());
    }
  }

  @Test
  public void scriptedWithSetValues() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    ScriptedValueProvider scripter = new ScriptedValueProvider();
    scripter.addValue("set", "v1");
    scripter.addValue("set", "v2");
//    config.setScripter(scripter);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory2");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertResult("expected-generate3.txt");
  }
  
  @Test
  public void customFilter() {
    Assert.fail("TBD");
  }
}

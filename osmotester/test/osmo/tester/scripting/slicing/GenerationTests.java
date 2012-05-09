package osmo.tester.scripting.slicing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

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

  @After
  public void teardown() {
    System.setOut(old);
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
  public void scriptedWithUndefinedValues() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    config.addValue("set", "1");
    config.addValue("set", "2");
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory2");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertResult("expected-generate5.txt");
  }

  @Test
  public void scriptedWithSetValues() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    config.addValue("set", "v1");
    config.addValue("set", "v2");
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory2");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertResult("expected-generate3.txt");
  }

  @Test
  public void customListener() throws Exception {
    SlicingConfiguration config = new SlicingConfiguration();
    TestSequenceListener listener = new TestSequenceListener();
    config.setListener(listener);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.scripting.slicing.TestModelFactory2");
    config.setSeed(233);
    SlicerMain.execute(config);
    assertEquals("Listener capture in slicing", "[suite-start, start, g:second, g:third, g:first, t:first, g:second, g:third, g:first, t:second, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, end, start, g:second, g:third, g:first, t:first, g:second, g:third, g:first, t:second, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, end, start, g:second, g:third, g:first, t:first, g:second, g:third, g:first, t:second, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, g:second, g:third, g:first, t:third, end, suite-end]", listener.getSteps().toString());
    assertResult("expected-generate4.txt");
  }
}

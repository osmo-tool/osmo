package osmo.tester.dsm;

import org.junit.Test;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class GenerationTests {
  @Test
  public void generate() throws Exception {
    DSMConfiguration config = new DSMConfiguration();
    config.addStep("increase");
    config.addStep("decrease");
    DataCoverageRequirement counterReq = new DataCoverageRequirement("counter");
    counterReq.addRequirement(3);
    config.add(counterReq);
    config.setAlgorithm("random");
    config.setModelFactory("osmo.tester.dsm.TestModelFactory");
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
}

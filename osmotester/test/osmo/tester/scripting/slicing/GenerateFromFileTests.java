package osmo.tester.scripting.slicing;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class GenerateFromFileTests {
  private PrintStream old = null;
  private ByteArrayOutputStream bos = null;
  private AsciiParser parser = null;

  @Before
  public void setup() {
    OSMOConfiguration.setSeed(324);
    OSMOConfiguration.reset();
    parser = new AsciiParser();
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

  private void runTest(String settingsFile, String resultsFile) throws Exception {
    String input = TestUtils.getResource(getClass(), settingsFile);
    SlicerMain.execute(parser.parse(input));
    assertResult(resultsFile);
  }

  @Test
  public void onlySettings() throws Exception {
    runTest("slice-config-only-settings.txt", "slice-result-settings-only.txt");
  }

  @Test
  public void noSettings() throws Exception {
    String input = TestUtils.getResource(getClass(), "slice-config-no-settings.txt");
    try {
      SlicerMain.execute(parser.parse(input));
      fail("Missing model factory setting should fail parsing");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for missing model factory", "Input does not define model object factory.", e.getMessage());
    }
  }

  @Test
  public void settingsAndSteps() throws Exception {
    runTest("slice-config-settings-steps.txt", "slice-result-settings-steps.txt");
  }

  @Test
  public void settingsAndVariableCoverage() throws Exception {
    runTest("slice-config-settings-varcov.txt", "slice-result-settings-varcov.txt");
  }

  @Test
  public void settingsAndVariableValues() throws Exception {
    runTest("slice-config-settings-varval.txt", "slice-result-settings-varval.txt");
  }

  @Test
  public void settingsStepsVariableCoverage() throws Exception {
    runTest("slice-config-settings-steps-varcov.txt", "slice-result-settings-steps-varcov.txt");
  }

  @Test
  public void settingsStepsVariableValues() throws Exception {
    OSMOConfiguration.setSeed(399);
    runTest("slice-config-settings-steps-varval.txt", "slice-result-settings-steps-varval.txt");
  }

  @Test
  public void all() throws Exception {
    runTest("slice-config-all.txt", "slice-result-all.txt");
  }
}

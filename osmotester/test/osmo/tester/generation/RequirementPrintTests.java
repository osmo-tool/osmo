package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel2;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class RequirementPrintTests {
  private RequirementTests real = new RequirementTests();
  private OutputStream out = null;
  private PrintStream sout = null;
  
  @Before
  public void setup() {
    sout = System.out;
    OSMOConfiguration.setSeed(111);
    out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    System.setOut(ps);
  }
  
  @After
  public void down() {
    System.setOut(sout);
  }
  
  @Test
  public void fullCoverage() {
    real.fullCoverage();
    String expected = "generated 1 tests.\n\n" +
            "Requirements:[epix, hello, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total = 3/3 (100%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Model printout for requirements testing", expected, actual);
  }

  @Test
  public void excessCoverage() {
    real.excessCoverage();
    String expected = "generated 1 tests.\n\n" +
            "Requirements:[epix]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total = 1/1 (100%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Model printout for requirements testing", expected, actual);
  }

  @Test
  public void fullExcessCoverage() {
    real.fullExcessCoverage();
    String expected = "generated 1 tests.\n\n" +
            "Requirements:[]\n" +
            "Covered:[epix, hello, world]\n" +
            "No requirements were defined as expected (with the add() method) so no percentage is calculated.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Model printout for requirements testing", expected, actual);
  }

  @Test
  public void lackingCoverageEndReport() {
    real.lackingCoverage();
    String expected = "generated 1 tests.\n\n" +
            "Requirements:[epix, hello, undefined, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total = 3/4 (75%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Model printout for requirements testing", expected, actual);
  }
}

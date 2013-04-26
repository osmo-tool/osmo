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
import osmo.tester.testmodels.RandomValueModel2;
import osmo.tester.testmodels.StateDescriptionModel;
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
    String expected = "generated 1 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 3\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 3\n" +
            "Unique requirements: 3\n" +
            "Unique states: 0\n" +
            "Unique state-pairs: 0\n" +
            "\n" +
            "Requirements:[epix, hello, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total unique requirements = 3/3 (100%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for requirements testing", expected, actual);
  }

  @Test
  public void excessCoverage() {
    real.excessCoverage();
    String expected = "generated 1 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 10\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 4\n" +
            "Unique requirements: 3\n" +
            "Unique states: 0\n" +
            "Unique state-pairs: 0\n" +
            "\n" +
            "Requirements:[hello]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total unique requirements = 1/1 (100%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for requirements testing", expected, actual);
  }

  @Test
  public void fullExcessCoverage() {
    real.fullExcessCoverage();
    String expected = "generated 1 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 3\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 3\n" +
            "Unique requirements: 3\n" +
            "Unique states: 0\n" +
            "Unique state-pairs: 0\n" +
            "\n" +
            "Requirements:[]\n" +
            "Covered:[epix, hello, world]\n" +
            "\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for requirements testing", expected, actual);
  }

  @Test
  public void lackingCoverageEndReport() {
    real.lackingCoverage();
    String expected = "generated 1 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 3\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 3\n" +
            "Unique requirements: 3\n" +
            "Unique states: 0\n" +
            "Unique state-pairs: 0\n" +
            "\n" +
            "Requirements:[epix, hello, undefined, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Total unique requirements = 3/4 (75%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for requirements testing", expected, actual);
  }

  @Test
  public void someAllCoverage() {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new RandomValueModel2());
    tester.generate();
    String actual = out.toString();
    String expected = "generated 55 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 530\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 12\n" +
            "Unique requirements: 0\n" +
            "Unique states: 0\n" +
            "Unique state-pairs: 0\n";
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for coverage", expected, actual);
  }

  @Test
  public void moreCoverage() {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new StateDescriptionModel());
    tester.generate();
    String actual = out.toString();
    String expected = "generated 55 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 530\n" +
            "Unique steps: 3\n" +
            "Unique step-pairs: 4\n" +
            "Unique requirements: 3\n" +
            "Unique states: 3\n" +
            "Unique state-pairs: 4\n" +
            "\n" +
            "Requirements:[]\n" +
            "Covered:[hello, world]\n" +
            "\n";
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for coverage", expected, actual);     
  }
}

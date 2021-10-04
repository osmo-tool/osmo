package osmo.tester.unittests.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.unittests.testmodels.CoverageValueModel1;
import osmo.tester.unittests.testmodels.RandomValueModel2;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class RequirementPrintTests {
  private RequirementTests real = new RequirementTests();
  private OutputStream out = null;
  private PrintStream sout = null;

  @Before
  public void setup() {
    sout = System.out;
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
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 3 (of 3)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 0\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n" +
            "\n" +
            "Requirements:[epix, hello, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Not covered:[]\n"+
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
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 4 (of 4)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 0\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n" +
            "\n" +
            "Requirements:[hello]\n" +
            "Covered:[epix, hello, world]\n" +
            "Not covered:[]\n"+
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
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 3 (of 3)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 0\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n" +
            "\n" +
            "Requirements:[]\n" +
            "Covered:[epix, hello, world]\n" +
            "Not covered:[]\n"+
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
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 3 (of 3)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 0\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n" +
            "\n" +
            "Requirements:[epix, hello, undefined, world]\n" +
            "Covered:[epix, hello, world]\n" +
            "Not covered:[undefined]\n"+
            "Total unique requirements = 3/4 (75%) requirements.\n";
    String actual = out.toString();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for requirements testing", expected, actual);
  }

  @Test
  public void nothingCoverage() {
    OSMOTester tester = new OSMOTester();
    tester.getConfig().setTrackOptions(true);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(new RandomValueModel2());
    tester.setModelFactory(factory);
    tester.generate(111);
    String actual = out.toString();
    String expected = "generated 5 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 73\n" +
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 11 (of 12) missing:[.osmo.tester.start.step->Step2]\n" +
            "Unique requirements: 0\n" +
            "Variable values: 34\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n";
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for coverage", expected, actual);
  }

  @Test
  public void moreCoverage() {
    OSMOTester tester = new OSMOTester();
    tester.getConfig().setTrackOptions(true);
    tester.setModelFactory(new ReflectiveModelFactory(CoverageValueModel1.class));
    tester.generate(111);
    String actual = out.toString();
    String expected = "generated 5 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 73\n" +
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 4 (of 4)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 3\n" +
            "Unique coverage-values: 3\n" +
            "Unique coverage-value-pairs: 4\n" +
            "\n" +
            "Requirements:[]\n" +
            //reason why we do not cover "epixx" requirement is because the model resets this between tests
            //dont ask me why, someday it must've seemed clever for this model.
            "Covered:[epix, hello, world]\n" +
            "Not covered:[]\n"+
            "\n";
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generator printout for coverage", expected, actual);
  }
}

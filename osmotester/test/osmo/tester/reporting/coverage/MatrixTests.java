package osmo.tester.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.getResource;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class MatrixTests {
  private TestSuite suite = null;
  private FSM fsm = null;

  @Before
  public void setup() {
    TestCase.reset();
    suite = new TestSuite();

    Requirements reqs = new Requirements();
    reqs.setTestSuite(suite);
    reqs.add("req-one");
    reqs.add("req-two");
    reqs.add("req-three");

    suite.startTest();
    addStep("one");
    addStep("one");
    addStep("two");
    addStep("three");
    reqs.covered("req-two");
    addStep("three");
    reqs.covered("req-two");
    addStep("four");
    addStep("four");
    addStep("five");
    suite.endTest();
    suite.startTest();
    addStep("one");
    addStep("two");
    reqs.covered("req-one");
    reqs.covered("req-one");
    addStep("five");
    addStep("six");
    addStep("six");
    reqs.covered("req-two");
    addStep("seven");
    addStep("eight");
    addStep("six");
    suite.endTest();

    fsm = new FSM();
    fsm.createTransition("one", 0);
    fsm.createTransition("two", 0);
    fsm.createTransition("three", 0);
    fsm.createTransition("four", 0);
    fsm.createTransition("five", 0);
    fsm.createTransition("six", 0);
    fsm.createTransition("seven", 0);
    fsm.createTransition("eight", 0);
    fsm.createTransition("ten", 0);

    fsm.setRequirements(reqs);
  }

  private void addStep(String name) {
    suite.addStep(new FSMTransition(name));
  }

  @Test
  public void htmlMatrix() {
    String expected = getResource(MatrixTests.class, "expected-matrix.txt");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite, fsm);
    String actual = html.getTraceabilityMatrix();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated HTML coverage matrix", expected, actual);
  }

  @Test
  public void csvMatrix() {
    String expected = getResource(MatrixTests.class, "expected-matrix.csv");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite, fsm);
    String actual = csv.getTraceabilityMatrix();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII coverage matrix", expected, actual);
  }

  @Test
  public void asciiMatrix() {
    String expected = getResource(MatrixTests.class, "expected-matrix-ascii.txt");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite, fsm);
    String actual = ascii.getTraceabilityMatrix();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII coverage matrix", expected, actual);
  }
}

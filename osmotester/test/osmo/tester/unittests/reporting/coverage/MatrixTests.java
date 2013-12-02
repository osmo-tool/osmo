package osmo.tester.unittests.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.model.TransitionName;
import osmo.tester.reporting.coverage.ASCIICoverageReporter;
import osmo.tester.reporting.coverage.CSVCoverageReporter;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class MatrixTests {
  private TestSuite suite = null;
  private FSM fsm = null;

  @Before
  public void setup() {
    TestCase.reset();
    suite = new TestSuite();
    suite.initRequirements(null);

    Requirements reqs = suite.getRequirements();
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
    fsm.setRequirements(reqs);
    createTransition("one", 0);
    createTransition("two", 0);
    createTransition("three", 0);
    createTransition("four", 0);
    createTransition("five", 0);
    createTransition("six", 0);
    createTransition("seven", 0);
    createTransition("eight", 0);
    createTransition("ten", 0);
  }

  private FSMTransition createTransition(String name, int weight) {
    return fsm.createTransition(new TransitionName("", name), weight);
  }

  private void addStep(String name) {
    suite.addStep(new FSMTransition(name));
  }

  @Test
  public void htmlMatrix() throws Exception {
    String expected = getResource(MatrixTests.class, "expected-matrix.txt");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = html.getTraceabilityMatrix();
    TestUtils.write(actual, "test-matrix.html");
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated HTML coverage matrix", expected, actual);
  }

  @Test
  public void csvMatrix() {
    String expected = getResource(MatrixTests.class, "expected-matrix.csv");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = csv.getTraceabilityMatrix();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated CSV coverage matrix", expected, actual);
  }

  @Test
  public void asciiMatrix() {
    String expected = getResource(MatrixTests.class, "expected-matrix-ascii.txt");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = ascii.getTraceabilityMatrix();
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII coverage matrix", expected, actual);
  }
}

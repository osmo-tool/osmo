package osmo.tester.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.model.TransitionName;

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
    createTransition("one", 0);
    createTransition("two", 0);
    createTransition("three", 0);
    createTransition("four", 0);
    createTransition("five", 0);
    createTransition("six", 0);
    createTransition("seven", 0);
    createTransition("eight", 0);
    createTransition("ten", 0);

    fsm.setRequirements(reqs);
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
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite, fsm);
    String actual = html.getTraceabilityMatrix();
    html.write(actual, "test-matrix.html");
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

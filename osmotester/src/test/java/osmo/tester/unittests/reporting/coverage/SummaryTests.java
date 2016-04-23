package osmo.tester.unittests.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
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
public class SummaryTests {
  private TestSuite suite = null;
  private FSM fsm = null;

  @Before
  public void setup() {
    suite = new TestSuite();
    suite.initRequirements(null);

    Requirements reqs = suite.getRequirements();
    reqs.setTestSuite(suite);
    reqs.add("req-one");
    reqs.add("req-two");
    reqs.add("req-three");

    suite.startTest(1);
    addStep("one");
    addStep("two");
    addStep("three");
    reqs.covered("req-two");
    addStep("four");
    addStep("five");
    suite.endTest();
    suite.startTest(1);
    addStep("one");
    addStep("two");
    addStep("five");
    addStep("six");
    addStep("seven");
    addStep("eight");
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
  public void csvTransitions() {
    String expected = getResource(getClass(), "expected-transitions.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = csv.getStepCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated CSV report for transition coverage", expected, actual);
  }

  @Test
  public void csvTransitionPairs() {
    String expected = getResource(getClass(), "expected-transitionpairs.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = csv.getStepPairCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated CSV report for transition coverage", expected, actual);
  }

  @Test
  public void csvRequirements() {
    String expected = getResource(getClass(), "expected-requirements.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = csv.getRequirementCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated CSV report for tag coverage", expected, actual);
  }

  @Test
  public void htmlTransitions() {
    String expected = getResource(getClass(), "expected-transitions.txt");
    expected = unifyLineSeparators(expected, "\n");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = html.getStepCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated HTML report for transition coverage", expected, actual);
  }

  @Test
  public void htmlTransitionPairs() {
    String expected = getResource(getClass(), "expected-transitionpairs.txt");
    expected = unifyLineSeparators(expected, "\n");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = html.getStepPairCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated HTML report for transition coverage", expected, actual);
  }

  @Test
  public void htmlRequirements() {
    String expected = getResource(getClass(), "expected-requirements.txt");
    expected = unifyLineSeparators(expected, "\n");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = html.getRequirementCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated HTML report for requirement coverage", expected, actual);
  }

  @Test
  public void asciiTransitions() {
    String expected = getResource(getClass(), "expected-transitions-ascii.txt");
    expected = unifyLineSeparators(expected, "\n");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = ascii.getStepCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII report for transition coverage", expected, actual);
  }

  @Test
  public void asciiTransitionPairs() {
    String expected = getResource(getClass(), "expected-transitionpairs-ascii.txt");
    expected = unifyLineSeparators(expected, "\n");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String actual = ascii.getStepPairCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII report for transition coverage", expected, actual);
  }
}

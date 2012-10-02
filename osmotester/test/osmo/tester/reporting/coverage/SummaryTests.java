package osmo.tester.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.model.TransitionName;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.getResource;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class SummaryTests {
  private TestSuite suite = null;
  private FSM fsm = null;

  @Before
  public void setup() {
    suite = new TestSuite();

    Requirements reqs = new Requirements();
    reqs.setTestSuite(suite);
    reqs.add("req-one");
    reqs.add("req-two");
    reqs.add("req-three");

    suite.startTest();
    addStep("one");
    addStep("two");
    addStep("three");
    reqs.covered("req-two");
    addStep("four");
    addStep("five");
    suite.endTest();
    suite.startTest();
    addStep("one");
    addStep("two");
    addStep("five");
    addStep("six");
    addStep("seven");
    addStep("eight");
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
  public void csvTransitions() {
    String expected = getResource(getClass(), "expected-transitions.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite, fsm);
    String actual = csv.getTransitionCounts();
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
    CSVCoverageReporter csv = new CSVCoverageReporter(suite, fsm);
    String actual = csv.getTransitionPairCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated CSV report for transition coverage", expected, actual);
  }

  @Test
  public void csvRequirements() {
    String expected = getResource(getClass(), "expected-requirements.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSVCoverageReporter csv = new CSVCoverageReporter(suite, fsm);
    String actual = csv.getRequirementCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated CSV report for requirement coverage", expected, actual);
  }

  @Test
  public void htmlTransitions() {
    String expected = getResource(getClass(), "expected-transitions.txt");
    expected = unifyLineSeparators(expected, "\n");
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite, fsm);
    String actual = html.getTransitionCounts();
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
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite, fsm);
    String actual = html.getTransitionPairCounts();
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
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite, fsm);
    String actual = html.getRequirementCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated HTML report for requirement coverage", expected, actual);
  }

  @Test
  public void asciiTransitions() {
    String expected = getResource(getClass(), "expected-transitions-ascii.txt");
    expected = unifyLineSeparators(expected, "\n");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite, fsm);
    String actual = ascii.getTransitionCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII report for transition coverage", expected, actual);
  }

  @Test
  public void asciiTransitionPairs() {
    String expected = getResource(getClass(), "expected-transitionpairs-ascii.txt");
    expected = unifyLineSeparators(expected, "\n");
    ASCIICoverageReporter ascii = new ASCIICoverageReporter(suite, fsm);
    String actual = ascii.getTransitionPairCounts();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated ASCII report for transition coverage", expected, actual);
  }
}

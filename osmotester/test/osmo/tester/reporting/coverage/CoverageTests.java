package osmo.tester.reporting.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class CoverageTests {
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
  public void csvTransitions() {
    String expected = getResource(getClass(), "expected-transitions.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSV csv = new CSV(suite, fsm);
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
    CSV csv = new CSV(suite, fsm);
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
    CSV csv = new CSV(suite, fsm);
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
    String expected = getResource(getClass(), "expected-transitions.html");
    expected = unifyLineSeparators(expected, "\n");
    HTML html = new HTML(suite, fsm);
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
    String expected = getResource(getClass(), "expected-transitionpairs.html");
    expected = unifyLineSeparators(expected, "\n");
    HTML html = new HTML(suite, fsm);
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
    String expected = getResource(getClass(), "expected-requirements.html");
    expected = unifyLineSeparators(expected, "\n");
    HTML html = new HTML(suite, fsm);
    String actual = html.getRequirementCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated HTML report for requirement coverage", expected, actual);
  }
}

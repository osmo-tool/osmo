package osmo.tester.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import static junit.framework.Assert.*;
import static osmo.tester.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class CoverageTests {
  private TestSuite suite = null;

  @Before
  public void setup() {
    suite = new TestSuite();
    suite.startTest();
    addStep("one");
    addStep("two");
    addStep("three");
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
  }

  private void addStep(String name) {
    suite.addStep(new FSMTransition(name));
  }

  @Test
  public void csvTransitions() {
    String expected = getResource(getClass(), "expected-transitions.csv");
    expected = unifyLineSeparators(expected, "\n");
    CSV csv = new CSV(suite);
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
    CSV csv = new CSV(suite);
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
  public void htmlTransitions() {
    String expected = getResource(getClass(), "expected-transitions.html");
    expected = unifyLineSeparators(expected, "\n");
    HTML html = new HTML(suite);
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
    HTML html = new HTML(suite);
    String actual = html.getTransitionPairCounts();
    actual = unifyLineSeparators(actual, "\n");
/*    System.out.println("----------------");
    System.out.println("expected:\n"+expected);
    System.out.println("----------------");
    System.out.println("actual:\n"+actual);
    System.out.println("----------------");*/
    assertEquals("Generated HTML report for transition coverage", expected, actual);
  }
}

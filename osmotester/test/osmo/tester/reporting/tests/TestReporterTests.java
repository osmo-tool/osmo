package osmo.tester.reporting.tests;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static osmo.tester.TestUtils.getResource;
import static osmo.tester.TestUtils.unifyLineSeparators;

/**
 * @author Teemu Kanstren
 */
public class TestReporterTests {
  @Test
  public void csvSteps() {
    Logger.debug = true;
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("one"));
    suite.setStepProperty("p1", "hello");
    suite.setStepProperty("p2", "world");
    suite.setStepProperty("p3", "dude");
    suite.addStep(new FSMTransition("two"));
    suite.setStepProperty("p4", "extra");
    suite.addStep(new FSMTransition("two"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("one111"));
    suite.setStepProperty("p11", "hello11");
    suite.endTest();
    CSV csv = new CSV(suite, null);
    List<String> report = csv.report();
    assertEquals("Should generate 2 reports for 2 tests", 2, report.size());
    String report1 = unifyLineSeparators(report.get(0), "\n");
    String report2 = unifyLineSeparators(report.get(1), "\n");
    String expected = getResource(getClass(), "expected-test1.csv");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generated report for test1", expected, report1);
    expected = getResource(getClass(), "expected-test2.csv");
    assertEquals("Generated report for test2", expected, report2);
  }
}

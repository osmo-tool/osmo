package osmo.tester.endconditions;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.StateCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.testmodels.StateDescriptionModel;
import osmo.tester.testmodels.StateDescriptionModel2;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class StateCoverageTests {
  @Test
  public void testCaseState3() {
    StateDescriptionModel model = new StateDescriptionModel();
    OSMOTester tester = new OSMOTester(model);
    tester.setTestEndCondition(new StateCoverage("3"));
    tester.setSuiteEndCondition(new Length(1));
    tester.generate(55);
    List<TestCase> history = tester.getSuite().getFinishedTestCases();
    assertEquals("Number of tests generated", 1, history.size());
    TestCase test = history.get(0);
    assertEquals("Number of steps in a test case", 3, test.getSteps().size());
  }

  @Test
  public void testCaseState2() {
    StateDescriptionModel model = new StateDescriptionModel();
    OSMOTester tester = new OSMOTester(model);
    tester.setTestEndCondition(new StateCoverage("2"));
    tester.setSuiteEndCondition(new Length(1));
    tester.generate(55);
    List<TestCase> history = tester.getSuite().getFinishedTestCases();
    assertEquals("Number of tests generated", 1, history.size());
    TestCase test = history.get(0);
    assertEquals("Number of steps in a test case", 2, test.getSteps().size());
  }

  @Test
  public void testCaseState3twice() {
    StateDescriptionModel model = new StateDescriptionModel();
    OSMOTester tester = new OSMOTester(model);
    tester.setTestEndCondition(new StateCoverage("3", "3"));
    tester.setSuiteEndCondition(new Length(1));
    tester.generate(55);
    List<TestCase> history = tester.getSuite().getFinishedTestCases();
    assertEquals("Number of tests generated", 1, history.size());
    TestCase test = history.get(0);
    assertEquals("Number of steps in a test case", 4, test.getSteps().size());
  }

  @Test
  public void suiteState() {
    StateDescriptionModel2 model = new StateDescriptionModel2();
    OSMOTester tester = new OSMOTester(model);
    tester.setTestEndCondition(new Length(1));
    //multiple repetitions of the same value are not counted for a suite so this should be equals to 1,2,3,4
    tester.setSuiteEndCondition(new StateCoverage("1", "2", "3", "4", "4", "4", "4", "4"));
    tester.generate(55);
    List<TestCase> history = tester.getSuite().getFinishedTestCases();
    assertEquals("Number of tests generated", 8, history.size());
    String expected = "[TestCase:[t2], TestCase:[t2], TestCase:[t3], TestCase:[t2], TestCase:[t1], TestCase:[t2], TestCase:[t2], TestCase:[t4]]";
    assertEquals(expected, history.toString());
  }
}

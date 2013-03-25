package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.data.DataCoverage;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.VariableModel2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class DataCoverageTests {
  private OSMOTester osmo = null;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(555);
  }
  
  @Test
  public void rangeWith3RequiredDataForSingleTest() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.addRequirement(1);
    req.addRequirement(2);
    req.addRequirement(3);
    dc.addRequirement(req);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2, 2, 2, 3, 2, 5, 2, 5, 4, 5, 1]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
  }

  @Test
  public void rangeWith5RequiredDataForSingleTest() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.addRequirement(1);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(3);
    dc.addRequirement(req);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2, 2, 2, 3, 2, 5, 2, 5, 4, 5, 1]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expected, actualSuite);
  }

  @Test
  public void rangeAndSetWith2DataRequirements() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.addRequirement(1);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(3);
    dc.addRequirement(req);
    DataCoverageRequirement req1 = new DataCoverageRequirement("named-set");
    req1.addRequirement("v1");
    req1.addRequirement("v1");
    req1.addRequirement("v2");
    dc.addRequirement(req1);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2, 2, 2, 3, 2, 5, 2, 5, 4, 5, 1]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expected, actualSuite);
    String expected2 = "[v3, v3, v2, v2, v1, v1, v1, v3, v1, v2, v3]";
    String actual2 = test.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for range", expected2, actual2);
    //suite should be exactly same for one test
    String actualSuite2 = suite.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for suite range", expected2, actualSuite2);
  }

  @Test
  public void allCoverageRequirementForTwo() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.requireAll();
    dc.addRequirement(req);
    DataCoverageRequirement req2 = new DataCoverageRequirement("named-set");
    req2.requireAll();
    dc.addRequirement(req2);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2, 2, 2, 3, 2, 5, 2, 5, 4, 5, 1]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expected, actualSuite);

    String expected2 = "[v3, v3, v2, v2, v1, v1, v1, v3, v1, v2, v3]";
    String actual2 = test.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for set", expected2, actual2);
    //suite should be exactly same for one test
    String actualSuite2 = suite.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for suite set", expected2, actualSuite2);
  }

  @Test
  public void allCoverageRequirementForSet() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req2 = new DataCoverageRequirement("named-set");
    req2.requireAll();
    dc.addRequirement(req2);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2, 2, 2, 3, 2]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expected, actualSuite);

    String expected2 = "[v3, v3, v2, v2, v1]";
    String actual2 = test.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for set", expected2, actual2);
    //suite should be exactly same for one test
    String actualSuite2 = suite.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for suite set", expected2, actualSuite2);
  }

  @Test
  public void anyCoverageRequirementForSet() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req2 = new DataCoverageRequirement("named-set");
    req2.addRequirement("v2");
    req2.requireAny();
    dc.addRequirement(req2);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expected = "[2]";
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expected, actualSuite);

    String expected2 = "[v3]";
    String actual2 = test.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for set", expected2, actual2);
    //suite should be exactly same for one test
    String actualSuite2 = suite.getStepVariables().get("named-set").getValues().toString();
    assertEquals("Expected value for suite set", expected2, actualSuite2);
  }

  @Test
  public void coverageForOnePlainVariable() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("second");
    req.addRequirement(true);
    req.addRequirement(false);
    dc.addRequirement(req);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    assertEquals("Length of generated test", 2, test.getSteps().size());
    Collection<Object> actual = test.getStepVariables().get("first").getValues();
    assertEquals("Expected value count for 'first'", 2, actual.size());
    assertTrue("Should have true value", actual.contains(true));
    assertFalse("Should not have false value", actual.contains(false));
    actual = test.getStepVariables().get("second").getValues();
    assertEquals("Expected value count for 'second'", 2, actual.size());
    assertTrue("Should have true value", actual.contains(true));
    assertTrue("Should have false value", actual.contains(false));
    //suite should be exactly same for one test
    Map<String,ModelVariable> suiteVariables = suite.getTestVariables();
    String actualSuite = suiteVariables.get("first").getValues().toString();
    assertEquals("Expected value for 'first'", "[true]", actualSuite);
    actualSuite = suiteVariables.get("second").getValues().toString();
    assertEquals("Expected value for 'second'", "[false, true]", actualSuite);
  }

  @Test
  public void coverageForTwoPlainVariables() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("first");
    req.addRequirement(true);
    dc.addRequirement(req);
    DataCoverageRequirement req2 = new DataCoverageRequirement("second");
    req2.addRequirement(true);
    req2.addRequirement(false);
    dc.addRequirement(req2);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    assertEquals("Length of generated test", 2, test.getSteps().size());
    String expected = "[true]";
    String actual = test.getTestVariables().get("first").getValues().toString();
    assertEquals("Expected values for boolean variable", expected, actual);
    //suite should be exactly same for one test
    String actualSuite = suite.getTestVariables().get("first").getValues().toString();
    assertEquals("Expected values for suite boolean variable", expected, actualSuite);

    String expected2 = "[false, true]";
    String actual2 = test.getStepVariables().get("second").getValues().toString();
    assertEquals("Expected values for boolean variable", expected2, actual2);
    //suite should be exactly same for one test
    String actualSuite2 = suite.getStepVariables().get("second").getValues().toString();
    assertEquals("Expected values for suite boolean variable", expected2, actualSuite2);
  }

  @Test
  public void nonExistentVariable() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("non-existent");
    dc.addRequirement(req);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Generation with coverage for non-existent variable should fail.");
    } catch (IllegalStateException e) {
      //Expected
      assertEquals("Reported error", "Impossible coverage requirements, defined variables [non-existent] not found.", e.getMessage());
    }
  }

  @Test
  public void invalidCoverageReq() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.addRequirement(6);
    dc.addRequirement(req);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(dc);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Generation with coverage for invalid values should fail.");
    } catch (IllegalArgumentException e) {
      //Expected
      assertEquals("Reported error", "Impossible coverage requirements, defined variables [range] can not have value 6.", e.getMessage());
    }
  }

  @Test
  public void rangeWith5RequiredDataForSuite() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    DataCoverage dc = new DataCoverage();
    DataCoverageRequirement req = new DataCoverageRequirement("range");
    req.addRequirement(1);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(2);
    req.addRequirement(3);
    dc.addRequirement(req);
    Length length5 = new Length(5);
    osmo.addTestEndCondition(length5);
    osmo.addSuiteEndCondition(dc);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    assertEquals("Number of tests", 4, tests.size());

    assertRange(tests.get(0), "[2, 2, 2]");
    assertRange(tests.get(1), "[3, 2, 5]");
    assertRange(tests.get(2), "[2, 5, 4]");
    assertRange(tests.get(3), "[5, 1, 3]");

    //suite is evaluated after each test, so the last tests gets two 1's even if just one is enough to end suite
    String expectedSuite = "[2, 2, 2, 3, 2, 5, 2, 5, 4, 5, 1, 3]";
    String actualSuite = suite.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for suite range", expectedSuite, actualSuite);
  }

  private void assertRange(TestCase test, String expected) {
    assertEquals("Test length", 5, test.getSteps().size());
    String actual = test.getStepVariables().get("range").getValues().toString();
    assertEquals("Expected value for range", expected, actual);
  }
}

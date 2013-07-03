package osmo.tester.reporting.trace;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.testmodels.VariableModel2;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class TraceGenerationTests {
  @Before
  public void reset() {
    TestCase.reset();
  }

  @Test
  public void testNoSteps() throws Exception {
    TestSuite suite = new TestSuite();
    suite.init();
    suite.startTest();
    assertTrace(suite, "expected-no-steps.txt");
  }

  private void assertTrace(TestSuite suite, String filename) {
    TraceReportWriter writer = new TraceReportWriter();
    String actual = writer.createReport(suite);
    String expected = getResource(TraceGenerationTests.class, filename);
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated HTML trace", expected, actual);
  }

  @Test
  public void oneTestOneStepNoParams() throws Exception {
    TestSuite suite = new TestSuite();
    suite.init();
    suite.startTest();
    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    suite.endTest();
    assertTrace(suite, "expected-one-step-no-params.txt");
  }

  @Test
  public void oneTestOneStepWithParams() throws Exception {
    TestSuite suite = new TestSuite();
    suite.init();
    suite.startTest();
    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addVariableValue("Username", "bob");
    loginStep.addVariableValue("Password", "1nt3rn4l");
    loginStep.addVariableValue("Fancy Pants", "true");
    loginStep.addVariableValue("Funny", "not so");
    loginStep.addVariableValue("Address", "Rock of Gelato, 3rd rock from the Sun");
    suite.endTest();
    assertTrace(suite, "expected-one-step-with-params.txt");
  }

  @Test
  public void twoStepWithParams() throws Exception {
    TestSuite suite = new TestSuite();
    suite.init();
    suite.startTest();

    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addVariableValue("Username", "bob");
    loginStep.addVariableValue("Password", "1nt3rn4l");
    loginStep.addVariableValue("Fancy Pants", "true");
    loginStep.addVariableValue("Funny", "not so");
    loginStep.addVariableValue("Address", "Rock of Gelato, 3rd rock from the Sun");

    FSMTransition buy = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep = suite.addStep(buy);
    buyStep.addVariableValue("Stuff", "Lawnmover");
    buyStep.addVariableValue("Quantity", "3");
    buyStep.addVariableValue("Price", "$5");

    suite.endTest();
    assertTrace(suite, "expected-two-steps-with-params.txt");
  }

  @Test
  public void twoTestsWithParams() throws Exception {
    TestSuite suite = new TestSuite();
    suite.init();
    suite.startTest();

    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addVariableValue("Username", "bob");
    loginStep.addVariableValue("Password", "1nt3rn4l");
    loginStep.addVariableValue("Fancy Pants", "true");
    loginStep.addVariableValue("Funny", "not so");
    loginStep.addVariableValue("Address", "Rock of Gelato, 3rd rock from the Sun");

    FSMTransition buy = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep = suite.addStep(buy);
    buyStep.addVariableValue("Stuff", "Lawnmover");
    buyStep.addVariableValue("Quantity", "3");
    buyStep.addVariableValue("Price", "$5");

    suite.endTest();
    suite.startTest();

    FSMTransition login2 = new FSMTransition("Login");
    TestCaseStep loginStep2 = suite.addStep(login2);
    loginStep2.addVariableValue("Username", "bob");
    loginStep2.addVariableValue("Password", "1nt3rn4l");
    loginStep2.addVariableValue("Fancy Pants", "true");
    loginStep2.addVariableValue("Funny", "not so");
    loginStep2.addVariableValue("Address", "Rock of Gelato, 3rd rock from the Sun");
    FSMTransition buy2 = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep2 = suite.addStep(buy2);
    buyStep2.addVariableValue("Stuff", "Lawnmover");
    buyStep2.addVariableValue("Quantity", "3");
    buyStep2.addVariableValue("Price", "$5");

    assertTrace(suite, "expected-two-tests-with-params.txt");
  }

  @Test
  public void traceFromGenerator() {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new VariableModel2());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(3));
    tester.generate(5);
    TestSuite suite = tester.getSuite();
    assertTrace(suite, "expected-generator-trace.txt");
  }

  @Test
  public void failTraceFromGenerator() {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new VariableModel2());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(3));
    tester.generate(5);
    TestSuite suite = tester.getSuite();
    suite.getAllTestCases().get(1).setFailed(true);
    assertTrace(suite, "expected-generator-fail-trace.txt");
  }
}

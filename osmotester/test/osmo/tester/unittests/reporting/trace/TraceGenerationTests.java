package osmo.tester.unittests.reporting.trace;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.reporting.trace.TraceReportWriter;
import osmo.tester.unittests.testmodels.VariableModel2;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class TraceGenerationTests {
  private TestSuite suite;

  @Before
  public void reset() {
    TestCase.reset();
    suite = new TestSuite();
    suite.enableParameterTracking();
  }

  @Test
  public void testNoSteps() throws Exception {
    suite.startTest();
    assertTrace(suite, "expected-no-steps.txt");
  }

  private void assertTrace(TestSuite suite, String filename) {
    TraceReportWriter writer = new TraceReportWriter();
    String actual = writer.createReport(suite.getAllTestCases());
    String expected = getResource(TraceGenerationTests.class, filename);
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Generated HTML trace", expected, actual);
  }

  @Test
  public void oneTestOneStepNoParams() throws Exception {
    suite.startTest();
    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    suite.endTest();
    assertTrace(suite, "expected-one-step-no-params.txt");
  }

  @Test
  public void oneTestOneStepWithParams() throws Exception {
    suite.startTest();
    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addValue("Username", "bob");
    loginStep.addValue("Password", "1nt3rn4l");
    loginStep.addValue("Fancy Pants", "true");
    loginStep.addValue("Funny", "not so");
    loginStep.addValue("Address", "Rock of Gelato, 3rd rock from the Sun");
    suite.endTest();
    assertTrace(suite, "expected-one-step-with-params.txt");
  }

  @Test
  public void twoStepWithParams() throws Exception {
    suite.startTest();

    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addValue("Username", "bob");
    loginStep.addValue("Password", "1nt3rn4l");
    loginStep.addValue("Fancy Pants", "true");
    loginStep.addValue("Funny", "not so");
    loginStep.addValue("Address", "Rock of Gelato, 3rd rock from the Sun");

    FSMTransition buy = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep = suite.addStep(buy);
    buyStep.addValue("Stuff", "Lawnmover");
    buyStep.addValue("Quantity", "3");
    buyStep.addValue("Price", "$5");

    suite.endTest();
    assertTrace(suite, "expected-two-steps-with-params.txt");
  }

  @Test
  public void twoTestsWithParams() throws Exception {
    TestSuite suite = new TestSuite();
    suite.startTest();

    FSMTransition login = new FSMTransition("Login");
    TestCaseStep loginStep = suite.addStep(login);
    loginStep.addValue("Username", "bob");
    loginStep.addValue("Password", "1nt3rn4l");
    //duplicate for same step
    loginStep.addValue("Password", "1nt3rn4l");
    loginStep.addValue("Fancy Pants", "true");
    loginStep.addValue("Funny", "not so");
    loginStep.addValue("Address", "Rock of Gelato, 3rd rock from the Sun");

    FSMTransition buy = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep = suite.addStep(buy);
    buyStep.addValue("Password", "1nt3rn4l");
    buyStep.addValue("Stuff", "Lawnmover");
    buyStep.addValue("Quantity", "3");
    buyStep.addValue("Price", "$5");

    suite.endTest();
    suite.startTest();

    FSMTransition login2 = new FSMTransition("Login");
    TestCaseStep loginStep2 = suite.addStep(login2);
    loginStep2.addValue("Username", "bob");
    loginStep2.addValue("Password", "1nt3rn4l");
    loginStep2.addValue("Fancy Pants", "true");
    loginStep2.addValue("Funny", "not so");
    loginStep2.addValue("Address", "Rock of Gelato, 3rd rock from the Sun");
    FSMTransition buy2 = new FSMTransition("Buy Stuff");
    TestCaseStep buyStep2 = suite.addStep(buy2);
    buyStep2.addValue("Stuff", "Lawnmover");
    buyStep2.addValue("Quantity", "3");
    buyStep2.addValue("Price", "$5");

    assertTrace(suite, "expected-two-tests-with-params.txt");
  }

  @Test
  public void traceFromGenerator() {
    OSMOTester tester = new OSMOTester();
    tester.getConfig().setDataTraceRequested(true);
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
    tester.getConfig().setDataTraceRequested(true);
    tester.addModelObject(new VariableModel2());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(3));
    tester.generate(5);
    TestSuite suite = tester.getSuite();
    suite.getAllTestCases().get(1).setFailed(true);
    assertTrace(suite, "expected-generator-fail-trace.txt");
  }
}

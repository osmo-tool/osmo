package osmo.tester.unittests.issues;

import org.junit.Before;
import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;
import osmo.tester.reporting.trace.TraceReportWriter;
import osmo.tester.unittests.issues.models.Issue32;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class Issue32to36 {
  @Before
  public void reset() {
    TestCase.reset();
  }
  
  @Test
  public void issue32() {
    OSMOTester tester = new OSMOTester();
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(5));
    tester.setAlgorithm(new WeightedBalancingAlgorithm());
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    tester.setModelFactory(new MyModelFactory(ps));
    tester.generate(333);
    String expected = TestUtils.getResource(Issue32to36.class, "expected/issue32.txt");
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("WeightedBalancing should not interfere with guards", expected, actual);
  }

  @Test
  public void issue33() throws IOException {
    OSMOTester tester = new OSMOTester();
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(5));
    tester.setAlgorithm(new WeightedBalancingAlgorithm());
    tester.setModelFactory(new MyModelFactory(NullPrintStream.stream));
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    FSM fsm = tester.getFsm();
    HTMLCoverageReporter reporter = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String matrix = reporter.getTraceabilityMatrix();
    String expected = TestUtils.getResource(Issue32to36.class, "expected/issue33.txt");
    matrix = unifyLineSeparators(matrix, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Reporting of test trace order", expected, matrix);
  }

  @Test
  public void issue34() throws Exception {
    OSMOTester tester = new OSMOTester();
    tester.getConfig().setDataTraceRequested(true);
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(5));
    tester.setAlgorithm(new WeightedBalancingAlgorithm());
    tester.setModelFactory(new MyModelFactory(NullPrintStream.stream));
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    TraceReportWriter tracer = new TraceReportWriter();
    String report = tracer.createReport(suite.getAllTestCases());
    String expected = TestUtils.getResource(Issue32to36.class, "expected/issue34.txt");
    report = unifyLineSeparators(report, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Reporting of test trace order", expected, report);
  }

  @Test
  public void issue36() throws Exception {
    OSMOTester tester = new OSMOTester();
    CustomEndCondition testCustom = new CustomEndCondition();
    CustomEndCondition suiteCustom = new CustomEndCondition();
    tester.setTestEndCondition(new And(new Length(5), new Length(3), testCustom));
    tester.setSuiteEndCondition(new Or(new Length(5), new Length(3), suiteCustom));
    tester.setAlgorithm(new WeightedBalancingAlgorithm());
    tester.setModelFactory(new MyModelFactory(NullPrintStream.stream));
    tester.generate(333);
    assertTrue("And should initialize end conditions", testCustom.isInitialized());
    assertTrue("Or should initialize end conditions", suiteCustom.isInitialized());
  }
  
  private static class CustomEndCondition implements EndCondition {
    private boolean initialized = false;

    public boolean isInitialized() {
      return initialized;
    }

    @Override
    public boolean endSuite(TestSuite suite, FSM fsm) {
      return false;
    }

    @Override
    public boolean endTest(TestSuite suite, FSM fsm) {
      return true;
    }

    @Override
    public void init(long seed, FSM fsm, OSMOConfiguration config) {
      initialized = true;
    }

    @Override
    public EndCondition cloneMe() {
      return this;
    }
  }
  
  private static class MyModelFactory implements ModelFactory {
    private final PrintStream ps;

    private MyModelFactory(PrintStream ps) {
      this.ps = ps;
    }

    @Override
    public void createModelObjects(TestModels addHere) {
      addHere.add(new Issue32(ps));
    }
  }
}

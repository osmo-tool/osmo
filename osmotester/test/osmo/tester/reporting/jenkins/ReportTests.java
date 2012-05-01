package osmo.tester.reporting.jenkins;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.CalculatorModel;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;


/** @author Teemu Kanstren */
public class ReportTests {
  private OSMOTester tester;

  @Before
  public void setup() {
    tester = new OSMOTester();
    tester.setSeed(333);
  }

  @Test
  public void calculatorSteps() {
    CalculatorModel calculator = new CalculatorModel();
    tester.addModelObject(calculator);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(0);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateStepReport();
    String expected = getResource(ReportTests.class, "expected-step-report.txt");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for steps", expected, actual);
  }

  @Test
  public void calculatorTests() {
    CalculatorModel calculator = new CalculatorModel();
    tester.addModelObject(calculator);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateTestReport();
    String expected = getResource(ReportTests.class, "expected-test-report.txt");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void partialModelSteps() {
    Requirements req = new Requirements();
    TestSuite suite = new TestSuite();
    PartialModel1 p1 = new PartialModel1(req, suite);
    PartialModel2 p2 = new PartialModel2(req, suite);
    tester.addModelObject(p1);
    tester.addModelObject(p2);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateStepReport();
    String expected = getResource(ReportTests.class, "expected-2step-report.txt");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for steps", expected, actual);
  }

  @Test
  public void partialModelTests() {
    Requirements req = new Requirements();
    TestSuite suite = new TestSuite();
    PartialModel1 p1 = new PartialModel1(req, suite);
    PartialModel2 p2 = new PartialModel2(req, suite);
    tester.addModelObject(p1);
    tester.addModelObject(p2);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateTestReport();
    String expected = getResource(ReportTests.class, "expected-2test-report.txt");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void writeSteps() throws Exception {
    CalculatorModel calculator = new CalculatorModel();
    tester.addModelObject(calculator);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(0);
    listener.getSuite().setEndTime(3234);
    listener.writeStepReport("jenkins-test.xml");
    String expected = getResource(ReportTests.class, "expected-step-report.txt");
    String actual = readFile("jenkins-test.xml");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for steps", expected, actual);
  }
  
  private String readFile(String name) throws IOException {
    StringBuilder text = new StringBuilder();
    try (Scanner scanner = new Scanner(new FileInputStream(name))) {
      while (scanner.hasNextLine()) {
        text.append(scanner.nextLine());
        text.append("\n");
      }
    }
    return text.toString();
  }

  @Test
  public void writeTests() throws Exception {
    CalculatorModel calculator = new CalculatorModel();
    tester.addModelObject(calculator);
    JenkinsReportGenerator listener = new JenkinsReportGenerator();
    tester.addListener(listener);
    tester.generate();
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    listener.writeTestReport("jenkins-test.xml");
    String expected = getResource(ReportTests.class, "expected-test-report.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile("jenkins-test.xml");
    assertEquals("Jenkins report for tests", expected, actual);
  }
}

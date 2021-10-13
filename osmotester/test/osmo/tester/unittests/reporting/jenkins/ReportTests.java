package osmo.tester.unittests.reporting.jenkins;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.reporting.jenkins.JenkinsReportGenerator;
import osmo.tester.reporting.jenkins.JenkinsSuite;
import osmo.tester.reporting.jenkins.JenkinsTest;
import osmo.tester.unittests.testmodels.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Scanner;
import java.util.TimeZone;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;


/** @author Teemu Kanstren */
public class ReportTests {
  private OSMOTester tester;
  private static final String filename = "jenkins-test.xml";

  @Before
  public void setup() {
    File file = new File(filename);
    if (file.exists()) {
      file.delete();
    }
    tester = new OSMOTester();
    tester.getConfig().setMethodBasedNaming(true);
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    JenkinsSuite.format = format;
  }

  @Test
  public void calculatorSteps() {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, true, true);
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(0);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateStepReport();
    String expected = getResource(ReportTests.class, "expected-step-report.xml");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for steps", expected, actual);
  }

  @Test
  public void calculatorTests() {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, true, true);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateTestReport();
    String expected = getResource(ReportTests.class, "expected-test-report.xml");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void partialModelSteps() {
    Requirements req = new Requirements();
    PartialModel1 p1 = new PartialModel1(req);
    PartialModel2 p2 = new PartialModel2(req);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(p1);
    factory.add(p2);
    tester.setModelFactory(factory);
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, false, true);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateStepReport();
    String expected = getResource(ReportTests.class, "expected-2step-report.xml");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for steps", expected, actual);
  }

  @Test
  public void partialModelTests() {
    Requirements req = new Requirements();
    PartialModel1 p1 = new PartialModel1(req);
    PartialModel2 p2 = new PartialModel2(req);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(p1);
    factory.add(p2);
    tester.setModelFactory(factory);
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, false, true);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    String actual = listener.generateTestReport();
    String expected = getResource(ReportTests.class, "expected-2test-report.xml");
    actual = unifyLineSeparators(actual, "\n");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void writeSteps() throws Exception {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, true, true);
    listener.getSuite().setStartTime(0);
    listener.getSuite().setEndTime(3234);
    tester.addListener(listener);
    tester.generate(333);
    String expected = getResource(ReportTests.class, "expected-step-report.xml");
    String actual = readFile(filename);
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
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false, true);
    listener.enableTestMode();
    tester.addListener(listener);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    tester.generate(333);
    String expected = getResource(ReportTests.class, "expected-test-report.xml");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void error5Tests() throws Exception {
    testError(false,"expected-error5-steps.xml");
  }

  @Test
  public void error5Tests2() throws Exception {
    testError(true,"expected-error5-steps2.xml");
  }

  private void testError(boolean reportSteps, String actualFileName) throws Exception{
    tester.setModelFactory(new ReflectiveModelFactory(ErrorModel5.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, reportSteps, true);
    listener.enableTestMode();
    tester.addListener(listener);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    boolean exceptionHappened = false;
    try {
      tester.generate(333);
      fail("Expected failure not observed.");
    } catch (Exception e) {
      //this should happen..
      exceptionHappened = true;
    }
    assertTrue(exceptionHappened);
    String expected = unifyLineSeparators(getResource(ReportTests.class, actualFileName), "\n");
    expected = unifyLineSeparators(expected, "\n");
    expected = unifyErrorString(expected);
    String actual = readFile(filename);
    actual = unifyErrorString(actual);
    assertEquals("Jenkins report for tests", expected, actual);
  }

  public String unifyErrorString(String original) {
    int end = original.indexOf("</error");
    int start = original.indexOf("osmo.tester.OSMOTester.generate");
    String reduced = original.substring(0, start) + original.substring(end, original.length());
    return reduced;
  }

  //@Ignore ?
  @Test
  public void errorTests() throws Exception {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false, true);
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    listener.writeTestReport();
    String expected = getResource(ReportTests.class, "expected-test-report.xml");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void xmlEscaping() throws Exception {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModelXmlOutput.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false, true);
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    listener.writeTestReport();
    String expected = getResource(ReportTests.class, "expected-xml-test-report.xml");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }
}

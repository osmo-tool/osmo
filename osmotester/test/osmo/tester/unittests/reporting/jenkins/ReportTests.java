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
import osmo.tester.unittests.testmodels.CalculatorModel;
import osmo.tester.unittests.testmodels.ErrorModel5;
import osmo.tester.unittests.testmodels.PartialModel1;
import osmo.tester.unittests.testmodels.PartialModel2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    JenkinsSuite.format = format;
  }

  @Test
  public void calculatorSteps() {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, true);
    tester.addListener(listener);
    tester.generate(333);
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
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, true);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
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
    PartialModel1 p1 = new PartialModel1(req);
    PartialModel2 p2 = new PartialModel2(req);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(p1);
    factory.add(p2);
    tester.setModelFactory(factory);
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, false);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
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
    PartialModel1 p1 = new PartialModel1(req);
    PartialModel2 p2 = new PartialModel2(req);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(p1);
    factory.add(p2);
    tester.setModelFactory(factory);
    JenkinsReportGenerator listener = new JenkinsReportGenerator(null, false);
    listener.enableTestMode();
    tester.addListener(listener);
    tester.generate(333);
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
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, true);
    listener.getSuite().setStartTime(0);
    listener.getSuite().setEndTime(3234);
    tester.addListener(listener);
    tester.generate(333);
    String expected = getResource(ReportTests.class, "expected-step-report.txt");
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
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false);
    listener.enableTestMode();
    tester.addListener(listener);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    tester.generate(333);
    String expected = getResource(ReportTests.class, "expected-test-report.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Test
  public void error5Tests() throws Exception {
    tester.setModelFactory(new ReflectiveModelFactory(ErrorModel5.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false);
    tester.addListener(listener);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    try {
      tester.generate(333);
      fail("Expected failure not observed.");
    } catch (Exception e) {
      //this should happen..
    }
    String expected = getResource(ReportTests.class, "expected-error5-steps.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }

  @Ignore
  public void errorTests() throws Exception {
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    JenkinsReportGenerator listener = new JenkinsReportGenerator(filename, false);
    tester.addListener(listener);
    tester.generate(333);
    listener.getSuite().setStartTime(1234);
    listener.getSuite().setEndTime(3234);
    listener.writeTestReport();
    String expected = getResource(ReportTests.class, "expected-test-report.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = readFile(filename);
    assertEquals("Jenkins report for tests", expected, actual);
  }
}

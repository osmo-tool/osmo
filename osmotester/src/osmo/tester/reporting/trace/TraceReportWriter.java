package osmo.tester.reporting.trace;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.io.FileOutputStream;
import java.io.StringWriter;

/** @author Teemu Kanstren */
public class TraceReportWriter {
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();

  public void write(TestSuite suite, String filename) throws Exception {
    String report = createReport(suite);
    FileOutputStream out = new FileOutputStream(filename);
    out.write(report.getBytes());
  }

  public static void main(String[] args) throws Exception {
    TestSuite suite = new TestSuite();
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
    suite.getCurrentTest().setFailed(true);
    new TraceReportWriter().write(suite, "teemu-testaa.html");
  }

  public String createReport(TestSuite suite) {
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    //TODO: Test with no parameters, with parameters, ...
    vc.put("tests", suite.getAllTestCases());

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/reporting/trace/trace-template.txt", "UTF8", vc, sw);
    return sw.toString();
  }
}

package osmo.tester.scripter.robotframework;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates scripts that can be executed with the Robot Framework.
 *
 * @author Teemu Kanstren
 */
public class Scripter {
  /** Test library to be used by Robot Framework */
  private String testLibrary;
  /** For template->script generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();
  /** The test case variables. */
  private Map<String, String> variables = new HashMap<String, String>();
  private Collection<RFTestCase> tests = new ArrayList<RFTestCase>();
  private RFTestCase currentTest = null;
  private final int cellCount;

  public Scripter(int cellCount) {
    this.cellCount = cellCount;
  }

  public void setTestLibrary(String testLibrary) {
    this.testLibrary = testLibrary;
  }

  public void addVariable(String name, String value) {
    variables.put("${"+name+"}", value);
  }

  public String scriptFor(TestCase test) {
    return createScript();
  }

  public void startTest(String name) {
    if (currentTest != null) {
      tests.add(currentTest);
    }
    currentTest = new RFTestCase(name, cellCount);
  }

//  public void endTest() {
//    tests.add(currentTest);
//  }

  public void addStep(String keyword, RFParameter... params) {
    currentTest.addStep(keyword, params);
  }

  public void addStepWithResult(String keyword, String variableName, RFParameter... params) {
    currentTest.addStepWithResult(keyword, variableName, params);
  }

  public void addStep(String keyword, String... params) {
    RFParameter[] rfParams = new RFParameter[params.length];
    for (int i = 0 ; i < params.length ; i++) {
      rfParams[i] = new RFParameter(params[i]);
    }
    currentTest.addStep(keyword, rfParams);
  }

  public void addStepWithResult(String keyword, String variableName, String... params) {
    RFParameter[] rfParams = new RFParameter[params.length];
    for (int i = 0 ; i < params.length ; i++) {
      rfParams[i] = new RFParameter(params[i]);
    }
    currentTest.addStepWithResult(keyword, variableName, rfParams);
  }

  public String createScript() {
    if (!tests.contains(currentTest)) {
      tests.add(currentTest);
    }
    vc.put("library",  testLibrary);
    vc.put("argument_headers", getArgumentHeaders());
    vc.put("variables", variables.entrySet());
    vc.put("css", new CSSHelper());
    vc.put("tests", tests);
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/scripter/robotframework/script.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  private Collection<String> getArgumentHeaders() {
    Collection<String> headers = new ArrayList<String>();
    int count = 0;
    for (RFTestCase test : tests) {
      for (RFTestStep step : test.getSteps()) {
        int stepCount = step.getParameters().size();
        if (stepCount > count) {
          count = stepCount;
        }
      }
    }
    for (int i = 0 ; i < count ; i++) {
      headers.add("Argument");
    }
    return headers;
  }

}

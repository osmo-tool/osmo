package osmo.tester.scripter.robotframework;

import osmo.tester.reporting.coverage.CoverageMetric;

import java.io.StringWriter;
import java.util.*;

/**
 * Creates scripts that can be executed with the Robot Framework.
 *
 * @author Teemu Kanstren
 */
public class RFScripter {
  /** Test library to be used by Robot Framework */
  private String testLibrary;
  /** The test case variables. */
  private Map<String, String> variables = new LinkedHashMap<>();
  /** The test cases to be generated. */
  private Collection<RFTestCase> tests = new ArrayList<>();
  /** Test currently being scripted. */
  private RFTestCase currentTest = null;
  /** How many cells should the RF test script table have? Used to fill empty cells and make a nice looking table. */
  private final int cellCount;

  public RFScripter(int cellCount) {
    this.cellCount = cellCount;
  }

  public void setTestLibrary(String testLibrary) {
    this.testLibrary = testLibrary;
  }

  /**
   * Adds a variable to the RF script. These are given in a separate table in the script file beginning.
   *
   * @param name  Name of the variable.
   * @param value Value for the variable.
   */
  public void addVariable(String name, String value) {
    variables.put("${" + name + "}", value);
  }

  /**
   * Starts a new test case in the script.
   *
   * @param name Name of the new test case.
   */
  public void startTest(String name) {
    if (currentTest != null) {
      tests.add(currentTest);
    }
    currentTest = new RFTestCase(name, cellCount);
  }

  /**
   * Adds a test step (keyword) into the currently generated test case.
   *
   * @param keyword The keyword for the test step.
   * @param params  The parameters of the test step.
   */
  public void addStep(String keyword, RFParameter... params) {
    currentTest.addStep(keyword, params);
  }

  /**
   * Adds a test step (keyword) into the currently generated test case,
   * with a definition of a variable storing the output of the step.
   *
   * @param keyword      The keyword for the test step.
   * @param variableName The name of the variable to generate in RF for storing the output.
   * @param params       The parameters for the test step.
   */
  public void addStepWithResult(String keyword, String variableName, RFParameter... params) {
    currentTest.addStepWithResult(keyword, variableName, params);
  }

  /**
   * Creates the test script based on the given test cases, test steps, variables, and the template.
   *
   * @return The test (suite) script as text.
   */
  public String createScript() {
    if (!tests.contains(currentTest)) {
      tests.add(currentTest);
    }
    String templateName = "osmo/tester/scripter/robotframework/script.vm";
    Map<String, Object> context = new HashMap<>();
    context.put("library", testLibrary);
    context.put("argument_headers", getArgumentHeaders());
    context.put("variables", variables.entrySet());
    context.put("css", new CSSHelper());
    context.put("tests", tests);
    return CoverageMetric.mustacheIt(context, templateName);

  }

  /**
   * Creates the set of table headers for the test case table in order to enable template rendering.
   *
   * @return The Argument headers for the test table, according to the number of cells defined.
   */
  private Collection<String> getArgumentHeaders() {
    Collection<String> headers = new ArrayList<>();
    for (int i = 0 ; i < cellCount ; i++) {
      headers.add("Argument");
    }
    return headers;
  }

}

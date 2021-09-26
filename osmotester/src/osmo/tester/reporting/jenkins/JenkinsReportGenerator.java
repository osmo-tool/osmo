package osmo.tester.reporting.jenkins;

import osmo.common.Logger;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.TestModels;
import osmo.tester.parser.ModelObject;
import osmo.tester.reporting.coverage.CoverageMetric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Listens to OSMO Tester test generation and builds a test report suitable for the Jenkins JUnit reporting task.
 * To use, add this as a listener to OSMO Tester and once generation is finished, call methods writeStepReport()
 * or writeTestReport() to produce a suitable report. The step report reports each test step as a different test case
 * in Jenkins. The test report reports test cases as JUnit tests.
 *
 * @author Teemu Kanstren
 */
public class JenkinsReportGenerator implements GenerationListener {
  private static final Logger log = new Logger(JenkinsReportGenerator.class);
  /** We use the generation configuration to provide us with properties to the test report. */
  private OSMOConfiguration config = null;
  /** The Jenkins (Ant) report format requires a name for the test suite. */
  private JenkinsSuite suite = new JenkinsSuite("OSMO Test Suite", false);
  /** Prefix for the name of the file where the report should be written. */
  private final String filename;
  /** If true, steps are described in the report, if false, test cases are described. */
  private final boolean steps;
  /** Seed used in test generation. */
  private Long seed = null;
  /** The test model used to generate the tests. */
  private FSM fsm;
  /** If we are in a test mode, we set times for all steps to 0 to enable deterministic test reporting and asserts. */
  private boolean testMode = false;

  private Exception exception = null;

  /**
   * @param filename The name of the report file.
   * @param steps    If true, the report describes generated test steps as test cases, else generated tests as test cases.
   */
  public JenkinsReportGenerator(String filename, boolean steps) {
    this.filename = filename;
    this.steps = steps;
  }

  /**
   * @param filename The name of the report file.
   * @param steps    If true, the report describes generated test steps as test cases, else generated tests as test cases.
   * @param testMode If true, the step duration is set to 0 (to enable deterministic unit tests).
   */
  public JenkinsReportGenerator(String filename, boolean steps, boolean testMode) {
    this.filename = filename;
    this.steps = steps;
    this.testMode = testMode;
  }

  private static void createJenkinsReport(String filename, List<TestCase> tests, long seed, OSMOConfiguration config) {
    JenkinsReportGenerator jenkins = new JenkinsReportGenerator(null, false);
    jenkins.init(seed, null, config);
    jenkins.suiteStarted(null);
    for (TestCase test : tests) {
      jenkins.testEnded(test);
    }
    jenkins.suiteEnded(null);
    String report = jenkins.generateTestReport();
    TestUtils.write(report, filename + ".xml");
  }

  public void enableTestMode() {
    suite = new JenkinsSuite("OSMO Test Suite", true);
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    this.seed = seed;
    this.fsm = fsm;
    this.config = config;
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void stepStarting(TestCaseStep step) {

  }

  @Override
  public void stepDone(TestCaseStep step) {
  }

  @Override
  public void lastStep(String name) {
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
  }

  @Override
  public void testEnded(TestCase test) {
    suite.add(test, testMode, exception);
    exception = null;
  }

  @Override
  public void testError(TestCase test, Throwable error) {
    exception = new Exception(error);
  }

  @Override
  public void suiteStarted(TestSuite suite) {
    this.suite.start();
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    this.suite.end();
    if (filename == null) {
      log.d("No filename defined, not writing jenkins report to file");
      return;
    }
    if (steps) {
      writeStepReport();
    } else {
      writeTestReport();
    }
  }

  /**
   * Generates a test report where each taken test step is reported as a separate test case.
   * This makes Jenkins show reports, where each model object class is reported as its own test class and
   * each time a step is taken, this is also shown.
   * In the end, it is possible to identify failed test steps from this..
   */
  public void writeStepReport() {
    String report = generateReport("steps");
    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(filename));
      out.write(report);
      out.flush();
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to write report", ioe);
    }
  }

  /** Writes a test report where generated tests are reported as the actual test cases by Jenkins. */
  public void writeTestReport() {
    String report = generateReport("tests");
    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(filename));
      out.write(report);
      out.flush();
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to write report", ioe);
    }
  }

  /**
   * Generates a report for test steps.
   *
   * @return The generated report.
   */
  public String generateStepReport() {
    return generateReport("steps");
  }

  /**
   * Generates a report for test cases.
   *
   * @return The generated report.
   */
  public String generateTestReport() {
    return generateReport("tests");
  }

  /**
   * Generates a report for generated tests using the given template.
   * The templates are loaded from classpath in the same package as this class.
   * Mustache is used to merge the template with generated tests.
   * Property "suite" contains the test suite generated.
   * Property "properties" contain miscellaneous properties about test generation configuration.
   *
   * @param templateNamePostfix The template name path to use for the report.
   * @return The generated report.
   */
  public String generateReport(String templateNamePostfix) {
    String templateName = "osmo/tester/reporting/jenkins/jenkins-" + templateNamePostfix + ".mustache";
    Map<String, Object> context = new HashMap<>();
    context.put("suite", suite);
    context.put("properties", fillProperties());
    return CoverageMetric.mustacheIt(context, templateName);
  }

  private static final String NULL = "Null";
  private static final String SCRIPTER = "Scripter";
  private static final String ALGORITHM = "Algorithm";
  private static final String FILTER = "Filter";
  private static final String LISTENER = "Listener";
  private static final String MODEL_OBJECT = "Model Object";
  private static final String SEED = "Seed";
  private static final String SUITE_END_CONDITION = "Suite End Condition";
  private static final String TEST_CASE_END_CONDITION = "Test Case End Condition";


  /**
   * Fills in the generation configuration properties for reports.
   *
   * @return The properties of generator configuration.
   */
  private Collection<Property> fillProperties() {
    Collection<Property> properties = new ArrayList<>();
    String algorithm = config.cloneAlgorithm(seed, fsm).getClass().getName();
    properties.add(new Property(ALGORITHM, algorithm));
    Collection<StepFilter> filters = config.getFilters();
    for (StepFilter filter : filters) {
      properties.add(new Property(FILTER, filter.getClass().getName()));
    }
    Collection<GenerationListener> listeners = config.getListeners().getListeners();
    for (GenerationListener listener : listeners) {
      properties.add(new Property(LISTENER, listener.getClass().getName()));
    }
    TestModels testModels = new TestModels();
    config.createModelObjects(testModels);
    for (ModelObject mo : testModels.getModels()) {
      String prefix = mo.getPrefix();
      String name = mo.getObject().getClass().getName();
      if (prefix.length() > 0) {
        properties.add(new Property(MODEL_OBJECT, prefix + "::" + name));
      } else {
        properties.add(new Property(MODEL_OBJECT, name));
      }
    }
    properties.add(new Property(SEED, "" + seed));
    EndCondition suiteEndCondition = config.getSuiteEndCondition();
    properties.add(new Property(SUITE_END_CONDITION, suiteEndCondition.toString()));
    EndCondition testEndCondition = config.getTestCaseEndCondition();
    properties.add(new Property(TEST_CASE_END_CONDITION, testEndCondition.toString()));
    return properties;
  }

  public JenkinsSuite getSuite() {
    return suite;
  }
}

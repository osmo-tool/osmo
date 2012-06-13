package osmo.tester.reporting.jenkins;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.parser.ModelObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Listens to OSMO Tester test generation and builds a test report suitable for the Jenkins JUnit reporting task.
 * To use, add this as a listener to OSMO Tester and once generation is finished, call methods writeStepReport()
 * or writeTestReport() to produce a suitable report. The step report reports each test step as a different test case
 * in Jenkins. The test report reports test cases as Jenkins tests.
 * 
 * @author Teemu Kanstren 
 */
public class JenkinsReportGenerator implements GenerationListener {
  private static Logger log = new Logger(JenkinsReportGenerator.class);
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();
  /** We use the generation configuration to provide us with properties to the test report. */
  private OSMOConfiguration config = null;
  /** The Jenkins (Ant) report format requires a name for the test suite. */
  private final JenkinsSuite suite = new JenkinsSuite("OSMO Test Suite");
  /** Prefix for the name of the file where the report should be written. */
  private final String filename;
  /** If true, steps are described in the report, if false, test cases are described. */
  private final boolean steps;

  /**
   * @param filename The name of the report file.
   * @param steps If true, the report describes generated test steps as test cases, else generated tests as test cases.
   */
  public JenkinsReportGenerator(String filename, boolean steps) {
    this.filename = filename;
    this.steps = steps;
  }

  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
    this.config = config;
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void transition(FSMTransition transition) {
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
    suite.add(test);
  }

  @Override
  public void testError(TestCase test, Exception error) {

  }

  @Override
  public void suiteStarted(TestSuite suite) {
    this.suite.start();
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    this.suite.end();
    if (filename == null) {
      log.debug("No filename defined, not writing jenkins report to file");
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

  /**
   * Writes a test report where generated tests are reported as the actual test cases by Jenkins.
   */
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
   * Apache Velocity is used to merge the template with generated tests.
   * Property "suite" contains the test suite generated.
   * Property "properties" contain miscellamous properties about test generation configuration.
   * 
   * @param templateName The velocity template name to use for the report.
   * @return The generated report.
   */
  public String generateReport(String templateName) {
    vc.put("suite", suite);
    vc.put("properties", fillProperties());
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/reporting/jenkins/jenkins-" + templateName + ".vm", "UTF8", vc, sw);
    return sw.toString();
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
    String algorithm = config.getAlgorithm().getClass().getName();
    properties.add(new Property(ALGORITHM, algorithm));
    Collection<TransitionFilter> filters = config.getFilters();
    for (TransitionFilter filter : filters) {
      properties.add(new Property(FILTER, filter.getClass().getName()));
    }
    ScriptedValueProvider scripter = config.getScripter();
    if (scripter == null) {
      properties.add(new Property(SCRIPTER, NULL));
    } else {
      properties.add(new Property(SCRIPTER, scripter.getClass().getName()));
    }
    Collection<GenerationListener> listeners = config.getListeners().getListeners();
    for (GenerationListener listener : listeners) {
      properties.add(new Property(LISTENER, listener.getClass().getName()));
    }
    Collection<ModelObject> modelObjects = config.getModelObjects();
    for (ModelObject mo : modelObjects) {
      String prefix = mo.getPrefix();
      String name = mo.getObject().getClass().getName();
      if (prefix.length() > 0) {
        properties.add(new Property(MODEL_OBJECT, prefix + "::" + name));
      } else {
        properties.add(new Property(MODEL_OBJECT, name));
      }
    }
    properties.add(new Property(SEED, "" + config.getSeed()));
    Collection<EndCondition> suiteEndConditions = config.getSuiteEndConditions();
    for (EndCondition ec : suiteEndConditions) {
      properties.add(new Property(SUITE_END_CONDITION, ec.toString()));
    }
    Collection<EndCondition> testEndConditions = config.getTestCaseEndConditions();
    for (EndCondition ec : testEndConditions) {
      properties.add(new Property(TEST_CASE_END_CONDITION, ec.toString()));
    }
    return properties;
  }

  public JenkinsSuite getSuite() {
    return suite;
  }
}

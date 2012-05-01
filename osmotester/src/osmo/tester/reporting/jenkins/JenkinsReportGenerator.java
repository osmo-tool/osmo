package osmo.tester.reporting.jenkins;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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

/** @author Teemu Kanstren */
public class JenkinsReportGenerator implements GenerationListener {
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();
  private OSMOConfiguration config = null;
  private final JenkinsSuite suite = new JenkinsSuite("OSMO Test Suite");

  public JenkinsReportGenerator() {
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
  }

  public void writeStepReport(String filename) {
    String report = generateReport("steps");
    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(filename));
      out.write(report);
      out.flush();
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to write report", ioe);
    }
  }

  public void writeTestReport(String filename) {
    String report = generateReport("tests");
    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(filename));
      out.write(report);
      out.flush();
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to write report", ioe);
    }
  }

  public String generateStepReport() {
    return generateReport("steps");
  }

  public String generateTestReport() {
    return generateReport("tests");
  }

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

  private Collection<Property> fillProperties() {
    Collection<Property> properties = new ArrayList<>();
    String algorithm = config.getAlgorithm().getClass().getName();
    properties.add(new Property("Algorithm", algorithm));
    Collection<TransitionFilter> filters = config.getFilters();
    for (TransitionFilter filter : filters) {
      properties.add(new Property("Filter", filter.getClass().getName()));
    }
    ScriptedValueProvider scripter = config.getScripter();
    if (scripter == null) {
      properties.add(new Property(SCRIPTER, NULL));
    } else {
      properties.add(new Property(SCRIPTER, scripter.getClass().getName()));
    }
    Collection<GenerationListener> listeners = config.getListeners().getListeners();
    for (GenerationListener listener : listeners) {
      properties.add(new Property("Listener", listener.getClass().getName()));
    }
    Collection<ModelObject> modelObjects = config.getModelObjects();
    for (ModelObject mo : modelObjects) {
      String prefix = mo.getPrefix();
      String name = mo.getObject().getClass().getName();
      if (prefix.length() > 0) {
        properties.add(new Property("Model Object", prefix + "::" + name));
      } else {
        properties.add(new Property("Model Object", name));
      }
    }
    properties.add(new Property("Seed", "" + config.getSeed()));
    Collection<EndCondition> suiteEndConditions = config.getSuiteEndConditions();
    for (EndCondition ec : suiteEndConditions) {
      properties.add(new Property("Suite End Condition", ec.toString()));
    }
    Collection<EndCondition> testEndConditions = config.getTestCaseEndConditions();
    for (EndCondition ec : testEndConditions) {
      properties.add(new Property("Test Case End Condition", ec.toString()));
    }
    return properties;
  }

  public JenkinsSuite getSuite() {
    return suite;
  }
}

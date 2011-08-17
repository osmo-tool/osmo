package osmo.tester.reporting.tests;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Teemu Kanstren
 */
public class ParameterReportBuilder {
  private static Logger log = new Logger(ParameterReportBuilder.class);
  /** For template->report generation. */
  private final VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private final VelocityContext vc = new VelocityContext();
  private final TestSuite suite;
  private TestCase test = null;
  private TestStep step = null;
  private Collection<Collection<Map<String, String>>> tests = new ArrayList<Collection<Map<String, String>>>();
  private Collection<Map<String, String>> testParameters;
  private Map<String, String> parameters = null;

  public ParameterReportBuilder(TestSuite suite) {
    this.suite = suite;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  public void addParameter(String name, Object value) {
    TestCase currentTest = suite.getCurrentTest();
    if (test != currentTest) {
      test = currentTest;
      testParameters = new ArrayList<Map<String, String>>();
      tests.add(testParameters);
    }
    TestStep currentStep = currentTest.getCurrentStep();
    if (step != currentStep) {
      step = currentStep;
      parameters = new LinkedHashMap<String, String>();
      parameters.put("id", Integer.toString(step.getId()));
      testParameters.add(parameters);
    }
    parameters.put(name, ""+value);
  }

  public List<String> report(String templateName) {
    List<String> result = new ArrayList<String>();
    for (Collection<Map<String, String>> test : tests) {
      List<String> headers = new ArrayList<String>();
      List<List<String>> rows = new ArrayList<List<String>>();
      rows.add(headers);
      for (Map<String, String> map : test) {
        log.debug("processing parameters:"+map);
        Set<String> names = map.keySet();
        for (String name : names) {
          if (!headers.contains(name)) {
            headers.add(name);
          }
        }
      }
      log.debug("headers:"+headers);
      for (Map<String, String> map : test) {
        List<String> row = new ArrayList<String>();
        for (String name : headers) {
          Object value = map.get(name);
          if (value == null) {
            row.add("-");
          } else {
            row.add(value.toString());
          }
        }
        rows.add(row);
      }
      vc.put("rows", rows);
      StringWriter sw = new StringWriter();
      velocity.mergeTemplate(templateName, "UTF8", vc, sw);
      result.add(sw.toString());
    }
    return result;
  }
}

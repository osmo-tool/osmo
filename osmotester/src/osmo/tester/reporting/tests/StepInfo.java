package osmo.tester.reporting.tests;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class StepInfo {
  private static Logger log = new Logger(StepInfo.class);
  /** For template->report generation. */
  private final VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private final VelocityContext vc = new VelocityContext();
  private final TestSuite suite;
  private final FSM fsm;

  public StepInfo(TestSuite suite, FSM fsm) {
    this.suite = suite;
    this.fsm = fsm;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  public List<String> report(String templateName) {
    List<TestCase> tests = suite.getTestCases();
    List<String> result = new ArrayList<String>();
    for (TestCase test : tests) {
      log.debug("processing test:"+test);
      List<String> headers = new ArrayList<String>();
      List<List<String>> rows = new ArrayList<List<String>>();
      rows.add(headers);
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        log.debug("processing header for step:"+step);
        Map<String, Object> parameters = step.getProperties();
        for (String name : parameters.keySet()) {
          if (!headers.contains(name)) {
            headers.add(name);
          }
        }
      }
      log.debug("headers:"+headers);
      for (TestStep step : steps) {
        List<String> row = new ArrayList<String>();
        row.add(Integer.toString(step.getId()));
        Map<String, Object> parameters = step.getProperties();
        for (String name : headers) {
          Object value = parameters.get(name);
          if (value == null) {
            row.add("-");
          } else {
            row.add(value.toString());
          }
        }
        rows.add(row);
      }
      headers.add(0, "id");
      vc.put("rows", rows);
      StringWriter sw = new StringWriter();
      velocity.mergeTemplate(templateName, "UTF8", vc, sw);
      result.add(sw.toString());
    }
    return result;
  }
}

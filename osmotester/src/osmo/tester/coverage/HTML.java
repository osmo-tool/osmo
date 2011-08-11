package osmo.tester.coverage;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This returns coverage tables in HTML format
 *
 * @author Olli-Pekka Puolitaival
 */
public class HTML extends CoverageMetric {

  public HTML(TestSuite ts) {
    super(ts);
  }

  public static void main(String[] args) {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("test"));
    suite.endTest();
    System.out.println(new HTML(suite).getTransitionCoverage());
  }

  @Override
  public String getTransitionCoverage() {
    VelocityEngine velocity = new VelocityEngine();
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    VelocityContext vc = new VelocityContext();
    Map<FSMTransition, Integer> coverage = countTransitionCoverage();
    Collection<TransitionCoverage> tc = new ArrayList<TransitionCoverage>();
    for (Map.Entry<FSMTransition, Integer> a : coverage.entrySet()) {
      tc.add(new TransitionCoverage(a.getKey(), a.getValue()));
    }
    vc.put("transitions", tc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/coverage/templates/html-transition-coverage.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  @Override
  public String getTransitionPairCoverage() {
    String ret = "<html>\n";
    ret += "<head></head>\n";
    ret += "<body>\n";
    ret += "<table border=\"1\">\n";
    ret += "<tr><td>From</td><td>To</td><td>Count</td></tr>\n";
    Map<String, Integer> coverage = countTransitionPairCoverage();
    for (Map.Entry<String, Integer> a : coverage.entrySet()) {
      String[] b = a.getKey().split(";");
      ret += "<tr><td>" + b[0] + "</td><td>" + b[1] + "</td><td>" + a.getValue() + "</td></tr>\n";
    }
    ret += "</table>\n";
    ret += "</body>\n";
    ret += "</html>\n";
    return ret;
  }

  @Override
  public String getRequirementsCoverage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTraceabilityMatrix() {
    // TODO Auto-generated method stub
    return null;
  }

}

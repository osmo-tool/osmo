package osmo.tester.examples.helloworld.scripter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.examples.helloworld.scripter.HelloScripter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class HelloVelocityScripter implements HelloScripter {
  private Collection<TestCase> tests = new ArrayList<TestCase>();
  private TestCase test = null;

  public void hello(String name, double size) {
    TestStep step = new TestStep("hello");
    step.addArg("name", name);
    step.addArg("size", ""+size);
    test.addStep(step);
  }

  public void world(String name, double range) {
    TestStep step = new TestStep("world");
    step.addArg("name", name);
    step.addArg("range", ""+range);
    test.addStep(step);
  }

  public void startTest() {
    test = new TestCase();
    tests.add(test);
  }

  public void endTest() {
  }

  @Override
  public void write() {
    String script = giefScript();
    System.out.println(script);
  }

  public String giefScript() {
    /** For template->script generation. */
    VelocityEngine velocity = new VelocityEngine();
    /** For storing template variables. */
    VelocityContext vc = new VelocityContext();
    vc.put("tests", tests);
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/examples/helloworld/scripter/hello-template.vm", "UTF8", vc, sw);
    return sw.toString();
 }
}

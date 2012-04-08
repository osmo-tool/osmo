package osmo.tester.examples.fuzz;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.TemplateWrapper;
import osmo.tester.model.dataflow.Words;

import java.io.StringWriter;

/** @author Teemu Kanstren */
public class JSONFuzzing {
  public static void main(String[] args) {
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty("resource.loader", "class");
    ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    ve.init();
    VelocityContext context = new VelocityContext();
    context.put("username", new Words(1, 5).setStrategy(DataGenerationStrategy.FUZZY_RANDOM).wrapper());
    Template template = ve.getTemplate("/osmo/tester/examples/fuzz/login_manual.vm");
    StringWriter sw = new StringWriter();
    template.merge(context, sw);
    System.out.println(sw);
  }
}

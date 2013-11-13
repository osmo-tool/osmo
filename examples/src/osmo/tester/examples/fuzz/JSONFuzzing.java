package osmo.tester.examples.fuzz;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.Text;

import java.io.StringWriter;

/** @author Teemu Kanstren */
public class JSONFuzzing {
  public static void main(String[] args) {
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty("resource.loader", "class");
    ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    ve.init();
    VelocityContext context = new VelocityContext();
    Text text5 = new Text(1, 5);
    text5.setRandomToString(true).setSeed(55);
    Text numbers5 = new Text(1, 5).numbersOnly();
    numbers5.setRandomToString(true).setSeed(55);
    context.put("username", text5);
    context.put("password", text5);
    context.put("sessionid", text5);
    context.put("hash", numbers5);
    Template template = ve.getTemplate("/osmo/tester/examples/fuzz/login_manual.vm");
    StringWriter sw = new StringWriter();
    template.merge(context, sw);
    System.out.println(sw);
  }
}

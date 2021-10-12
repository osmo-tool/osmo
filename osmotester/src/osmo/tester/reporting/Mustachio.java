package osmo.tester.reporting;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import osmo.common.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class Mustachio {
  private static final Logger log = new Logger(Mustachio.class);

  public static String mustacheIt(Map<String, Object> context, String templateName) {
    DefaultMustacheFactory mf = new DefaultMustacheFactory();
//    Reader reader = new StringReader(TestUtils.getResource(CoverageMetric.class, templatename));
    //mustache docs are not very clear on what the name is for but lets go with this until someone tells better
//    Mustache mustache = mf.compile(reader, templateName);
    Mustache mustache = mf.compile(templateName);
    StringWriter sw = new StringWriter();

    try {
      mustache.execute(sw, context).flush();
    } catch (IOException e) {
      log.e("Failed to process Mustache template: "+e.getMessage());
      throw new RuntimeException(e);
    }

    return sw.toString();
  }
}

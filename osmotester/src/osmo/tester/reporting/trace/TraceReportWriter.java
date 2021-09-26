package osmo.tester.reporting.trace;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.reporting.coverage.CoverageMetric;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Writes a report for a test suite. Includes steps taken, as well as any observed parameters in those steps.
 * Also marks failed tests as red. The output is HTML formatted.
 * 
 * @author Teemu Kanstren 
 */
public class TraceReportWriter {

  public void write(List<TestCase> tests, String filename) throws Exception {
    String report = createReport(tests);
    TestUtils.write(report, filename);
  }

  public String createReport(List<TestCase> tests) {
    String templateName = "osmo/tester/reporting/trace/trace-template.txt";
    Map<String, Object> context = new HashMap<>();
    context.put("tests", tests);
    return CoverageMetric.mustacheIt(context, templateName);
  }
}

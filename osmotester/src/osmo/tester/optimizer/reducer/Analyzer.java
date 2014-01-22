package osmo.tester.optimizer.reducer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.io.StringWriter;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class Analyzer {
  private static final Logger log = new Logger(Analyzer.class);
  private final ReducerConfig config;
  private final ReducerState state;
  private Invariants invariants = null;
  private final List<String> steps;

  public Analyzer(List<String> steps, ReducerState state) {
    this.steps = steps;
    this.state = state;
    this.config = state.getConfig();
  }

  public Invariants analyze() {
    List<TestCase> tests = state.getTests();
    TestCase[] array = tests.toArray(new TestCase[tests.size()]);
    return analyze(array);
  }

  public Invariants analyze(TestCase... tests) {
    invariants = new Invariants(steps);
    for (TestCase test : tests) {
      invariants.process(test);
    }
    return invariants;
  }

  public void writeReport(String name) {
    TestUtils.write(createReport(), getPath() + name + ".txt");
  }

  public String createReport() {
    if (state.getTests().size() == 0) return "No failing tests found.";
    VelocityEngine velocity = new VelocityEngine();
    VelocityContext vc = new VelocityContext();
    vc.put("testCount", state.getTestCount());
    vc.put("lengths", state.getLengths());
    vc.put("finalLength", state.getTests().get(0).getAllStepNames().size());
    vc.put("shortests", state.getTests().size());
    vc.put("stepCounts", invariants.getUsedStepCounts());
    vc.put("missingSteps", invariants.getMissingSteps());
    vc.put("finalSteps", invariants.getLastSteps());
    vc.put("precedences", invariants.getPrecedencePatterns());
    vc.put("sequences", invariants.getSequencePatterns());

    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/optimizer/reducer/template.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  public String getPath() {
    long seed = config.getSeed();
    long time = config.getIterationTime();
    String extension = config.getPathExtension();
    if (extension.length() > 0) extension = "-" + extension;
    return "osmo-output/reducer-" + seed + "-" + time + extension+"/";
  }
}

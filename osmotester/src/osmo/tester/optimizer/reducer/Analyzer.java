package osmo.tester.optimizer.reducer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.reducer.debug.Invariants;

import java.io.StringWriter;
import java.util.List;

/**
 * Analyzes a set of traces for invariants and writes reports.
 * 
 * @author Teemu Kanstren
 */
public class Analyzer {
  private static final Logger log = new Logger(Analyzer.class);
  /** Configuration for reducer. */
  private final ReducerConfig config;
  /** State of reducer, the tests found, timeouts, etc. */
  private final ReducerState state;
  /** Any invariants for the current set of tests will be stored here. */
  private Invariants invariants = null;
  /** All possible steps in the model. */
  private final List<String> allSteps;

  /**
   * 
   * @param allSteps All possible steps in the model regardless if taken in tests.
   * @param state The current reducer state to analyze.
   */
  public Analyzer(List<String> allSteps, ReducerState state) {
    this.allSteps = allSteps;
    this.state = state;
    this.config = state.getConfig();
  }

  /**
   * Take all tests generated and analyze them for invariants.
   * 
   * @return Found invariants.
   */
  public Invariants analyze() {
    List<TestCase> tests = state.getTests();
    TestCase[] array = tests.toArray(new TestCase[tests.size()]);
    return analyze(array);
  }

  /**
   * Analyze the given set of tests for invariants.
   * 
   * @param tests To analyze.
   * @return Found invariants.
   */
  public Invariants analyze(TestCase... tests) {
    invariants = new Invariants(allSteps);
    for (TestCase test : tests) {
      invariants.process(test);
      log.debug("Processed:"+test);
    }
    return invariants;
  }

  /**
   * Writes a report of current found invariants into a file on disk. Uses velocity templates.
   * 
   * @param name Name for the file. Path is determined with getPath().
   */
  public void writeReport(String name) {
    TestUtils.write(createReport(), getPath() + name + ".txt");
  }

  /**
   * Create the string for the report to write to disk about found invariants.
   * 
   * @return The report to write.
   */
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
    vc.put("s_precedences", invariants.getStrictPrecedences());
    vc.put("f_precedences", invariants.getFlexPrecedences());
    vc.put("sequences", invariants.getSequences());

    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/optimizer/reducer/template.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * The path for writing a report for the given reducer configuration.
   * 
   * @return The path string. Relative to current directory.
   */
  public String getPath() {
    long seed = config.getSeed();
    long time = config.getIterationTime();
    String extension = config.getPathExtension();
    if (extension.length() > 0) extension = "-" + extension;
//    return "osmo-output/reducer-" + seed + "-" + time + extension+"/";
    return "osmo-output/reducer-" + seed + extension+"/";
  }
}

package osmo.tester.optimizer.reducer;

import osmo.common.TestUtils;
import osmo.common.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.reducer.debug.Invariants;
import osmo.tester.reporting.coverage.CoverageMetric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      log.d("Processed:" + test);
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

    Map<String, Object> context = new HashMap<>();
    context.put("testCount", state.getTestCount());
    context.put("lengths", state.getLengths());
    context.put("finalLength", state.getTests().get(0).getAllStepNames().size());
    context.put("shortests", state.getTests().size());
    context.put("stepCounts", invariants.getUsedStepCounts());
    context.put("missingSteps", invariants.getMissingSteps());
    context.put("finalSteps", invariants.getLastSteps());
    context.put("s_precedences", invariants.getStrictPrecedences());
    context.put("f_precedences", invariants.getFlexPrecedences());
    context.put("sequences", invariants.getSequences());

    String templateName = "osmo/tester/optimizer/reducer/template.mustache";
    return CoverageMetric.mustacheIt(context, templateName);
  }

  /**
   * The path for writing a report for the given reducer configuration.
   * 
   * @return The path string. Relative to current directory.
   */
  public String getPath() {
    long seed = config.getSeed();
    String extension = config.getPathExtension();
    if (extension.length() > 0) extension = "-" + extension;
    return "osmo-output/reducer-" + seed + extension+"/";
  }
}

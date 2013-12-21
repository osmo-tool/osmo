package osmo.tester.optimizer.reducer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Analyzer {
  private static final Logger log = new Logger(Analyzer.class);
  private final ReducerState state;
  private Invariants invariants = null;
  private final List<String> steps;

  public Analyzer(List<String> steps, ReducerState state) {
    this.steps = steps;
    this.state = state;
  }
  
  public void analyze() {
    List<TestCase> tests = state.getTests();
    TestCase[] array = tests.toArray(new TestCase[tests.size()]);
    analyze(array);
  }

  public Invariants analyze(TestCase... tests) {
    invariants = new Invariants(steps);
    for (TestCase test : tests) {
      invariants.process(test);
    }
    return invariants;
  }

  public void writeReport(String name) {
    TestUtils.write(createReport(), "osmo-output/"+name+".txt");
  }

  public String createReport() {
    if (state.getTests().size() == 0) return "No failing tests found.";
    VelocityEngine velocity = new VelocityEngine();
    VelocityContext vc = new VelocityContext();
    vc.put("testCount", state.getTestCount());
    vc.put("lengths", state.getLengths());
    vc.put("finalLength", state.getTests().get(0).getAllStepNames().size());
    vc.put("shortests", state.getTests().size());
    Map<String, Integer> stepCounts = invariants.getStepCounts();
    List<StepCount> counts = new ArrayList<>();
    for (String name : stepCounts.keySet()) {
      StepCount sc = new StepCount(name, stepCounts.get(name));
      counts.add(sc);
    }
    vc.put("stepCounts", counts);
    vc.put("finalSteps", invariants.getLastSteps());
    vc.put("precedences", invariants.getPrecedencePatterns());
    vc.put("sequences", invariants.getSequencePatterns());

    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/optimizer/reducer/template.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  public static class StepCount {
    private final String step;
    private final int count;

    private StepCount(String step, int count) {
      this.step = step;
      this.count = count;
    }

    public String getStep() {
      return step;
    }

    public int getCount() {
      return count;
    }
  }
}

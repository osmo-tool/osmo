package osmo.tester.scenario;

import osmo.tester.model.FSMTransition;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 
 * Filters the set of enabled steps for the generator to match those allowed by the defined scenario.
 * This takes care of startup sequence and maximum values for steps in a slice. Also removes forbidden steps.
 * However, minimum numbers are taken care of by the end condition created when the scenario is initialized.
 * See the {@link Scenario} class for that.
 * 
 * @author Teemu Kanstren 
 */
public class ScenarioFilter {
  /** The test scenario. */
  private final Scenario scenario;
  /** Index where the slice should start being evaluated, excludes initial startup sequence. */
  private final int sliceIndex;
  /** Key = step name, value = slice for that step. */
  private Map<String, Slice> slices = new LinkedHashMap<>();

  public ScenarioFilter(Scenario scenario) {
    this.scenario = scenario;
    if (scenario == null) {
      sliceIndex = -1;
      return;
    }
    List<Slice> slices = scenario.getSlices();
    for (Slice slice : slices) {
      this.slices.put(slice.getStepName(), slice);
    }
    sliceIndex = scenario.getStartup().size();
  }

  /**
   * Filters the given set of enabled test steps to match the ones required by the scenario definition.
   * If a startup sequence is defined, the only allowed step is the one next in the startup sequence.
   * If maximum number(s) are defined for a scenario slice(s), any that have reached that number are removed.
   * 
   * @param enabled    The currently enabled steps for the generator.
   * @param stepsSoFar The steps taken so far in the current test case.
   */
  public void filter(List<FSMTransition> enabled, List<String> stepsSoFar) {
    if (scenario == null) return;
    if (stepsSoFar.size() < scenario.getStartup().size()) {
      filterStartup(enabled, stepsSoFar);
    } else {
      filterSlice(enabled, stepsSoFar);
    }
  }

  /**
   * Filter the enabled steps according to the slices for those steps. 
   * If no slice is defined for a step, nothing is done for that step.
   * 
   * @param enabled    Currently enabled steps in generator.
   * @param stepsSoFar Steps taken so far in current test case.
   */
  private void filterSlice(List<FSMTransition> enabled, List<String> stepsSoFar) {    
    //first we count how many times each step appears in the current trace after startup sequence
    Map<String, Integer> stepCount = new LinkedHashMap<>();
    for (int si = sliceIndex ; si < stepsSoFar.size() ; si++) {
      String step = stepsSoFar.get(si);
      Integer count = stepCount.get(step);
      if (count == null) count = 0;
      count++;
      stepCount.put(step, count);
    }

    //next we remove all steps that are forbidden or have appeared the max number of times
    for (Iterator<FSMTransition> ei = enabled.iterator() ; ei.hasNext() ; ) {
      FSMTransition t = ei.next();
      String name = t.getStringName();
      if (scenario.getForbidden().contains(name)) {
        ei.remove();
        continue;
      }
      Slice slice = slices.get(name);
      if (slice != null) {
        Integer count = stepCount.get(name);
        if (count == null) count = 0;
        int max = slice.getMax();
        if (max > 0 && max <= count) ei.remove();
      }
    }
  }

  /**
   * Forces the startup sequence to be taken.
   * 
   * @param enabled    Set of enabled steps.
   * @param stepsSoFar Set of steps taken so far for the test case.
   */
  private void filterStartup(List<FSMTransition> enabled, List<String> stepsSoFar) {
    String now = scenario.getStartup().get(stepsSoFar.size());
    for (Iterator<FSMTransition> i = enabled.iterator() ; i.hasNext() ; ) {
      FSMTransition t = i.next();
      if (!t.getStringName().equals(now)) i.remove();
    }
  }
  
}

package osmo.tester.scenario;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 
 * 
 * @author Teemu Kanstren 
 */
public class ScenarioFilter {
  private final Scenario scenario;
  private final int sliceIndex;
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

  public void filter(List<FSMTransition> enabled, List<String> stepsSoFar) {
    if (scenario == null) return;
    if (stepsSoFar.size() < scenario.getStartup().size()) {
      filterStartup(enabled, stepsSoFar);
    } else {
      filterSlice(enabled, stepsSoFar);
    }
  }
  
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
  
  private void filterStartup(List<FSMTransition> enabled, List<String> stepsSoFar) {
    String now = scenario.getStartup().get(stepsSoFar.size());
    for (Iterator<FSMTransition> i = enabled.iterator() ; i.hasNext() ; ) {
      FSMTransition t = i.next();
      if (!t.getStringName().equals(now)) i.remove();
    }
  }
  
}

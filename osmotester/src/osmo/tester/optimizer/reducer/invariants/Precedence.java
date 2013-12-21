package osmo.tester.optimizer.reducer.invariants;

import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Precedence {
  private Map<String, Collection<String>> previousMap = new HashMap<>();
  private final List<String> allSteps;

  public Precedence(List<String> steps) {
    this.allSteps = steps;
    for (String step : steps) {
      List<String> previous = new ArrayList<>();
      previous.addAll(steps);
      previous.remove(step);
      previousMap.put(step, previous);
    }
  }
  
  public void process(List<String> names) {
    for (String step : allSteps) {
      //if one is never present, we toss it as it is not required to reach failure
      if (!names.contains(step)) {
        previousMap.get(step).clear();
      }
    }
    List<String> steps = new ArrayList<>();
    steps.add(FSM.START_STEP_NAME);
    steps.addAll(names);
    //start from index 1 to avoid looking for what is before START_STEP
    for (int i = 1 ; i < steps.size() ; i++) {
      String step = steps.get(i);
      Collection<String> previous = previousMap.get(step);
      //we remove all that are not in given trace before this step
      previous.retainAll(steps.subList(0, i));
    }
  }

  public Collection<String> getPatterns() {
    List<String> result = new ArrayList<>();
    for (String name : previousMap.keySet()) {
      Collection<String> values = previousMap.get(name);
      for (String value : values) {
        result.add(value+"->"+name);
      }
    }
    Collections.sort(result);
    return result;
  }
}

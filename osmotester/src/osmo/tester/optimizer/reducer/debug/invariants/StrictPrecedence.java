package osmo.tester.optimizer.reducer.debug.invariants;

import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyzes if some step is always before some other step in all given tests.
 * If strict, keeps only those that were before another in all tests.
 * If flexible, keeps those that were before another in the tests where they were present.
 * That is, the first one can never appear if the step is absent from one of the traces.
 * 
 * @author Teemu Kanstren
 */
public class StrictPrecedence {
  /** Key = step name, Value = Other steps observed before this step. */
  private Map<String, Collection<String>> previousMap = new HashMap<>();
  /** List of all possible steps in the test model. */
  private final List<String> allSteps;

  /**
   * @param allSteps All steps in the model.
   */
  public StrictPrecedence(List<String> allSteps) {
    this.allSteps = allSteps;
    for (String step : allSteps) {
      //for each step, we initially claim that all other steps are before it. the we remove ones that are not later..
      List<String> previous = new ArrayList<>();
      previous.addAll(allSteps);
      previous.remove(step);
      previousMap.put(step, previous);
    }
  }

  /**
   * Check for precedences.
   * Removes all steps from list of ones observed before specific step, if one is not found in this trace.
   * 
   * @param names List of steps in test case to check.
   */
  public void process(List<String> names) {
    for (String step : allSteps) {
      //never present -> remove
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

  /**
   * For report building.
   * 
   * @return List of all precedence patterns still valid.
   */
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

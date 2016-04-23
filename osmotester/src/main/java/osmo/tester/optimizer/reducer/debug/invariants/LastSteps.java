package osmo.tester.optimizer.reducer.debug.invariants;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * What steps did the set of tests end with? 
 * May tell about different bugs as the last one is typically the one to trigger the failure.
 * 
 * @author Teemu Kanstren
 */
public class LastSteps {
  /** Set of last steps observed. */
  private Collection<String> lastSteps = new HashSet<>();

  /**
   * Collect last observed steps from these.
   * 
   * @param steps To process.
   */
  public void process(List<String> steps) {
    lastSteps.add(steps.get(steps.size()-1));
  }

  public Collection<String> getLastSteps() {
    return lastSteps;
  }
}

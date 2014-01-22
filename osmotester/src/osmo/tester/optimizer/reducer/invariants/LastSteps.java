package osmo.tester.optimizer.reducer.invariants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class LastSteps {
  private Collection<String> lastSteps = new HashSet<>();
  
  public void process(List<String> steps) {
    lastSteps.add(steps.get(steps.size()-1));
  }

  public Collection<String> getLastSteps() {
    return lastSteps;
  }
}

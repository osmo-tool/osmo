package osmo.tester.scripting.manual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A manually defined script for a test case.
 *
 * @author Teemu Kanstren
 */
public class TestScript implements Iterable<ScriptStep> {
  /** The list of steps for the test case, in order. */
  private List<ScriptStep> steps = new ArrayList<>();
  /** The step to which add variable values. */
  private ScriptStep lastAdded = null;

  public void addStep(String name) {
    ScriptStep step = new ScriptStep(name);
    this.lastAdded = step;
    steps.add(step);
  }

  /**
   * Adds a value for the given variable to the latest added test step.
   *
   * @param variable The name of the variable.
   * @param value    The value for the variable.
   */
  public void addValue(String variable, String value) {
    lastAdded.addValue(variable, value);
  }

  @Override
  public Iterator<ScriptStep> iterator() {
    return steps.iterator();
  }

  public List<ScriptStep> getSteps() {
    return steps;
  }
}

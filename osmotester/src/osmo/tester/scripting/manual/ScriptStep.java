package osmo.tester.scripting.manual;

import java.util.ArrayList;
import java.util.List;

/**
 * A test step in manually defined test script.
 *
 * @author Teemu Kanstren
 */
public class ScriptStep {
  /** Name of the step transition. */
  private final String transition;
  /** List of values for this test step. Processed in given order. */
  private final List<ScriptValue> values = new ArrayList<>();

  public ScriptStep(String transition) {
    this.transition = transition;
  }

  public String getTransition() {
    return transition;
  }

  /**
   * Add a scripted value for given variable in this step in the added order.
   *
   * @param variable The name of the variable.
   * @param value    The value for the variable.
   */
  public void addValue(String variable, String value) {
    values.add(new ScriptValue(variable, value));
  }

  public List<ScriptValue> getValues() {
    return values;
  }
}

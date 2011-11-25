package osmo.tester.scripting.manual;

import java.util.ArrayList;
import java.util.List;

/** @author Teemu Kanstren */
public class ScriptStep {
  private final String transition;
  private final List<ScriptValue> values = new ArrayList<ScriptValue>();

  public ScriptStep(String transition) {
    this.transition = transition;
  }

  public String getTransition() {
    return transition;
  }

  public void addValue(String variable, String value) {
    values.add(new ScriptValue(variable, value));
  }

  public List<ScriptValue> getValues() {
    return values;
  }
}

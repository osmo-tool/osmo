package osmo.tester.model;

import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueSet;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class ScriptedValueProvider {
  private Map<String, ValueSet<String>> scripts = new HashMap<>();

  public String next(String variable) {
    ValueSet<String> values = scripts.get(variable);
    if (values == null) {
      throw new IllegalArgumentException("Script requests unknown variable:" + variable);
    }
    return values.next();
  }

  public void addValue(String variable, String value) {
    ValueSet<String> values = scripts.get(variable);
    if (values == null) {
      values = new ValueSet<>(DataGenerationStrategy.ORDERED_LOOP);
      scripts.put(variable, values);
    }
    values.add(value);
  }

  public Map<String, ValueSet<String>> getScripts() {
    return scripts;
  }
}

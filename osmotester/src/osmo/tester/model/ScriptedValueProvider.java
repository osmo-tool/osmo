package osmo.tester.model;

import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.ValueSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides values for specific variables according to given script.
 * Used to execute manually defined test scripts over the test models.
 *
 * @author Teemu Kanstren
 */
public class ScriptedValueProvider {
  /** List of values for specific variables. Key = variable name, value = set of values in order, looped if needed. */
  private Map<String, ValueSet<String>> scripts = new HashMap<>();

  public ScriptedValueProvider() {
  }

  /**
   * Provides the next defined value for the given variable.
   *
   * @param variable The name of the variable.
   * @return The next value (or its string representation..)
   */
  public String next(String variable) {
    ValueSet<String> values = scripts.get(variable);
    if (values == null) {
      throw new IllegalArgumentException("Script requests unknown variable:" + variable);
    }
    return values.next();
  }

  /**
   * Adds a new value for the specified variable.
   *
   * @param variable The variable name.
   * @param value    The new value (or its string representation..)
   */
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

package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.model.VariableValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a variable value that represents a combination of the given set of other variables.
 * To use, create a field in the class with this type and pass references to the variables to combine.
 * This is represented to the generator as a {@link VariableValue} object, which leads it to store the value
 * of this object after any step is executed.
 * When the value() method is invoked, it asks the referenced objects for their
 * values and combines these to produce a new value as their combination.
 * The combined value is a combination of all referenced values with "{@literal &}" character in between.
 *
 * To have the annotated variable tracked for coverage, annotate using {@link osmo.tester.annotation.Variable} as usual.
 *
 * @author Teemu Kanstren
 */
public class CombinationCoverage implements VariableValue<String> {
  private static final Logger log = new Logger(CombinationCoverage.class);
  /** The references to the variables this is combining. */
  private List<VariableValue> inputs = new ArrayList<>();

  /** @param inputs The set of input variables to combine. */
  public CombinationCoverage(VariableValue... inputs) {
    if (inputs == null || inputs.length == 0) {
      throw new IllegalArgumentException("You must specify some input variables to combine.");
    }
    for (VariableValue input : inputs) {
      if (input == null) {
        throw new NullPointerException("Input variable cannot be null.");
      }
      if (this.inputs.contains(input)) {
        throw new IllegalArgumentException("Variable only allowed once in combination:" + input);
      }
      this.inputs.add(input);
    }
  }

  /**
   * This is where the combined value is provided. It is the values of given variables concatenated with the char "{@literal &}"
   * in between.
   *
   * @return The value to store for coverage.
   */
  @Override
  public String value() {
    String result = "";
    for (VariableValue input : inputs) {
      if (result.length() > 0) result +="&";
      result += ""+input.value();
    }
    return result;
  }

  /**
   * Access to the combined variables.
   *
   * @return List of variables that are being combined.
   */
  public List<VariableValue> getInputs() {
    return inputs;
  }
}



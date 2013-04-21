package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.model.VariableValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a variable value that represents a combination of the given set of other variables.
 * Typical use is to create a {@link osmo.tester.coverage.ScoreConfiguration} and use the methods provided there to directly
 * add the combinations of interest.
 * Thus, the user typically does not use this class directly, although it is possible if desired.
 *
 * @author Teemu Kanstren
 */
public class CombinationCoverage implements VariableValue<String> {
  private static Logger log = new Logger(CombinationCoverage.class);
  /** The names of the variables this is calculating for. */
  private List<VariableValue> inputs = new ArrayList<>();

  /** @param inputs The set of input variable names. */
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

  @Override
  public String value() {
    String result = "";
    for (VariableValue input : inputs) {
      if (result.length() > 0) result +="&";
      result += ""+input.value();
    }
    return result;
  }

  public List<VariableValue> getInputs() {
    return inputs;
  }
}



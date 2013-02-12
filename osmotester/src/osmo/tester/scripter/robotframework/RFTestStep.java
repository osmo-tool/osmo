package osmo.tester.scripter.robotframework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a test step for the Robot Framework scripter.
 *
 * @author Teemu Kanstren
 */
public class RFTestStep {
  /** The action to be performed on the test target (e.g., method to invoke, specified in the associated robot library). */
  private final String action;
  /** Name of variable for the return value, if any. */
  private final String returnValueVariableName;
  /** Parameters for the action. */
  private final RFParameter[] params;
  /** How many cells should the overall RF test case HTML table have. Used for pretty tables. */
  private final int cellCount;

  public RFTestStep(String action, int cellCount, RFParameter... params) {
    this.action = action;
    this.returnValueVariableName = null;
    this.params = params;
    this.cellCount = cellCount;
  }

  public RFTestStep(String action, int cellCount, String variableName, RFParameter... params) {
    this.action = action;
    this.returnValueVariableName = variableName;
    this.params = params;
    this.cellCount = cellCount;
  }

  public String getAction() {
    if (returnValueVariableName != null) {
      return "${" + returnValueVariableName + "}=";
    }
    return action;
  }

  public Collection<String> getParameters() {
    Collection<String> parameters = new ArrayList<>();
    if (returnValueVariableName != null) {
      parameters.add(action);
    }
    for (RFParameter param : params) {
      parameters.add(param.toString());
    }
    int i = parameters.size();
    for ( ; i < cellCount ; i++) {
      parameters.add("");
    }
    return parameters;
  }
}

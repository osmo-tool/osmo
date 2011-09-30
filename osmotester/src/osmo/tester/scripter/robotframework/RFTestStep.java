package osmo.tester.scripter.robotframework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class RFTestStep {
  private final String action;
  private final String variableName;
  private final RFParameter[] params;
  private final int cellCount;

  public RFTestStep(String action, int cellCount, RFParameter... params) {
    this.action = action;
    this.variableName = null;
    this.params = params;
    this.cellCount = cellCount;
  }

  public RFTestStep(String action, int cellCount, String variableName, RFParameter... params) {
    this.action = action;
    this.variableName = variableName;
    this.params = params;
    this.cellCount = cellCount;
  }

  public String getAction() {
    if (variableName != null) {
      return "${"+variableName+"}=";
    }
    return action;
  }

  public Collection<String> getParameters() {
    Collection<String> parameters = new ArrayList<String>();
    if (variableName != null) {
      parameters.add(action);
    }
    for (RFParameter param : params) {
      parameters.add(param.toString());
    }
    int i = parameters.size();
    for (; i < cellCount ; i++) {
      parameters.add("");
    }
    return parameters;
  }
}

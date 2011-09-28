package osmo.tester.scripter.robotframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Teemu Kanstren
 */
public class RFTestStep {
  private final String action;
  private final String variableName;
  private final String[] params;
  private final int cellCount;

  public RFTestStep(String action, int cellCount, String... params) {
    this.action = action;
    this.variableName = null;
    this.params = params;
    this.cellCount = cellCount;
  }

  public RFTestStep(String action, int cellCount, String variableName, String... params) {
    this.action = action;
    this.variableName = variableName;
    this.params = params;
    this.cellCount = cellCount;
  }

  public String getAction() {
    return action;
  }

  public Collection<String> getParameters() {
    Collection<String> parameters = new ArrayList<String>();
    if (variableName != null) {
      parameters.add(variableName+"=");
    }
    Collections.addAll(parameters, params);
    int i = parameters.size();
    for (; i < cellCount ; i++) {
      parameters.add("");
    }
    return parameters;
  }
}

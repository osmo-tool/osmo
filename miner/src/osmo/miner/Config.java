package osmo.miner;

/**
 * @author Teemu Kanstren
 */
public class Config {
  private String variableId;
  private String stepId;
  private String variableNameId;
  private String variableValueId;
  private String stepNameId;

  public void validate() {
    String errors = "";
    if (variableId == null) {
      errors += "Variable ID must be non-null. ";
    }
    if (variableNameId == null) {
      errors += "Variable name ID must be non-null. ";
    }
    if (variableValueId == null) {
      errors += "Variable value ID must be non-null. ";
    }
    if (stepId == null) {
      errors += "Step ID must be non-null. ";
    }
    if (stepNameId == null) {
      errors += "Step name ID must be non-null. ";
    }
    if (errors.length() > 0) {
      throw new IllegalStateException(errors);
    }
  }

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public String getStepId() {
    return stepId;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  public String getVariableNameId() {
    return variableNameId;
  }

  public void setVariableNameId(String variableNameId) {
    this.variableNameId = variableNameId;
  }

  public String getVariableValueId() {
    return variableValueId;
  }

  public void setVariableValueId(String variableValueId) {
    this.variableValueId = variableValueId;
  }

  public String getStepNameId() {
    return stepNameId;
  }
}

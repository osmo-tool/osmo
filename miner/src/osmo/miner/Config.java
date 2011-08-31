package osmo.miner;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Teemu Kanstren
 */
public class Config {
  public static String variableId;
  public static String variableNameId;
  public static String variableValueId;
  public static String stepId;
  public static String stepNameId;

  private Config() {
  }

  public static void init() {
    Properties props = new Properties();
    try {
      props.load(Config.class.getResourceAsStream("/osmo-miner.properties"));
    } catch (IOException e) {
      //TODO: logging..
      e.printStackTrace();
    }
    variableId = props.getProperty("variable_id");
    variableNameId = props.getProperty("variable_name_id");
    variableValueId = props.getProperty("variable_value_id");
    stepId = props.getProperty("step_id");
    stepNameId = props.getProperty("step_name_id");
  }

  public static void validate() {
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
}

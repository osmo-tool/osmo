package osmo.miner;

import org.apache.velocity.app.VelocityEngine;
import osmo.common.log.Logger;

import javax.swing.UIManager;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Teemu Kanstren
 */
public class Config {
  private static Logger log = new Logger(Config.class);
  public static String variableId;
  public static String variableNameId;
  public static String variableValueId;
  public static String stepId;
  public static String stepNameId;
  public static String baseDir;
  public static String variablePre;
  public static String variablePost;

  static {
    init();
  }

  private Config() {
  }

  public static void init() {
    Properties props = new Properties();
    try {
      props.load(Config.class.getResourceAsStream("/osmo-miner.properties"));
    } catch (IOException e) {
      log.error("Failed to load configuration file 'osmo-miner.properties'", e);
    }
    variableId = props.getProperty("variable_id");
    variableNameId = props.getProperty("variable_name_id");
    variableValueId = props.getProperty("variable_value_id");
    stepId = props.getProperty("step_id");
    stepNameId = props.getProperty("step_name_id");
    baseDir = props.getProperty("base_dir");
    variablePre = props.getProperty("variable_pre");
    variablePost = props.getProperty("variable_post");
    String debug = props.getProperty("debug");
    if ("true".equals(debug)) {
      Logger.debug = true;
    }

    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
      if ("Nimbus".equals(info.getName())) {
        try {
          UIManager.setLookAndFeel(info.getClassName());
        } catch (Exception e) {
          log.error("Failed to set Nimbus look and feel", e);
        }
        break;
      }
    }
  }

  public static VelocityEngine createVelocity() {
    VelocityEngine velocity = new VelocityEngine();
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    return velocity;
  }

  public static void validate() {
    if (variableId == null) {
      init();
    }
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

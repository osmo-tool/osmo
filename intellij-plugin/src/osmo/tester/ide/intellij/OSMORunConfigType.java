package osmo.tester.ide.intellij;

import com.intellij.execution.Location;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/** @author Teemu Kanstren */
public class OSMORunConfigType implements ConfigurationType {
  private static final Logger LOGGER = Logger.getInstance("OSMO Runner");

  private final ConfigurationFactory myFactory;

  public OSMORunConfigType() {
    myFactory = new ConfigurationFactoryEx(this) {
      @Override
      public RunConfiguration createTemplateConfiguration(Project project) {
        LOGGER.info("Create OSMO Template Configuration");
        return new OSMORunConfig("", project, this);
      }

      @Override
      public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
        ((ModuleBasedConfiguration) configuration).onNewConfigurationCreated();
      }
    };
  }

//  public static TestNGConfigurationType getInstance() {
//    return ConfigurationTypeUtil.findConfigurationType(TestNGConfigurationType.class);
//  }

  public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location) {
    OSMORunConfig config = (OSMORunConfig) runConfiguration;
    return false;
  }

  public String getDisplayName() {
    return "OSMO";
  }

  public String getConfigurationTypeDescription() {
    return "OSMO Configuration";
  }

  public Icon getIcon() {
    return Resources.RUN_OSMO;
  }

  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{myFactory};
  }

  @NotNull
  public String getId() {
    return "OSMO";
  }

  public static OSMORunConfigType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(OSMORunConfigType.class);
  }
}

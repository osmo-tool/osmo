package osmo.tester.ide.intellij;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;

import java.util.Collection;

/** @author Teemu Kanstren */
public class OSMORunConfig extends ModuleBasedConfiguration<JavaRunConfigurationModule> {
  private OSMORunParameters runParameters = new OSMORunParameters();
  
  public OSMORunConfig(String name, Project project, ConfigurationFactory factory) {
    super(name, new JavaRunConfigurationModule(project, false), factory);
  }

  public OSMORunParameters getRunParameters() {
    return runParameters;
  }

  /**
   * This seems likely to be a way to limit what modules are used as options for module choices etc.
   *
   * @return Null as we really do not care and hope it matches "all modules".
   */
  @Override
  public Collection<Module> getValidModules() {
    return null;
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new OSMORunConfig(getName(), getProject(), OSMORunConfigType.getInstance().getConfigurationFactories()[0]);
  }

  /**
   * This seems to be used to create an instance of the configuration editor for defining the run parameters.
   * The contents are based on what the IntelliJ plugins do, I have no idea how much sense all this makes.
   * 
   * @return The editor for OSMO run instances.
   */
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    SettingsEditorGroup<OSMORunConfig> group = new SettingsEditorGroup<>();
    group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new OSMORunConfigEditor(getProject()));
    JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
    group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<OSMORunConfig>());
    return group;
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
    return new OSMORunState(executionEnvironment, this);
  }
}

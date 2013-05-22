package osmo.tester.ide.intellij;

import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class OSMORunParameters implements CommonJavaRunConfigurationParameters {
  private String vmParameters = null;
  private boolean alternativeJREPathEnabled = false;
  private String alternativeJREPath = null;
  private Project project = null;
  private String runClass = null;
  private String packageName = null;
  private String programParameters = null;
  private String workingDirectory = null;
  private Map<String, String> envs = new HashMap<>();
  private boolean passParentEnvs = false;

  @Override
  public void setVMParameters(String value) {
    this.vmParameters = value;
  }

  @Override
  public String getVMParameters() {
    return vmParameters;
  }

  @Override
  public boolean isAlternativeJrePathEnabled() {
    return alternativeJREPathEnabled;
  }

  @Override
  public void setAlternativeJrePathEnabled(boolean enabled) {
    this.alternativeJREPathEnabled = enabled;
  }

  @Override
  public String getAlternativeJrePath() {
    return alternativeJREPath;
  }

  @Override
  public void setAlternativeJrePath(String path) {
    this.alternativeJREPath = path;
  }

  @Nullable
  @Override
  public String getRunClass() {
    return runClass;
  }

  @Nullable
  @Override
  public String getPackage() {
    return packageName;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProgramParameters(@Nullable String value) {
    this.programParameters = value;
  }

  @Nullable
  @Override
  public String getProgramParameters() {
    return programParameters;
  }

  @Override
  public void setWorkingDirectory(@Nullable String value) {
    this.workingDirectory = value;
  }

  @Nullable
  @Override
  public String getWorkingDirectory() {
    return workingDirectory;
  }

  @Override
  public void setEnvs(@NotNull Map<String, String> envs) {
    this.envs = envs;
  }

  @NotNull
  @Override
  public Map<String, String> getEnvs() {
    return envs;
  }

  @Override
  public void setPassParentEnvs(boolean passParentEnvs) {
    this.passParentEnvs = passParentEnvs;
  }

  @Override
  public boolean isPassParentEnvs() {
    return passParentEnvs;
  }
}

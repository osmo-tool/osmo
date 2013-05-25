package osmo.tester.ide.intellij;

import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import osmo.common.log.Logger;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.ide.intellij.endconditions.DefaultConfiguration;
import osmo.tester.ide.intellij.endconditions.EndConditionConfiguration;
import osmo.tester.ide.intellij.endconditions.EndConditions;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class OSMORunParameters implements CommonJavaRunConfigurationParameters {  
  public static final Logger log = new Logger(OSMORunParameters.class);
  public String vmParameters = null;
  public boolean alternativeJREPathEnabled = false;
  public String alternativeJREPath = null;
  public Project project = null;
  public String runClass = null;
  public String packageName = null;
  public String programParameters = null;
  public String workingDirectory = null;
  public Map<String, String> envs = new HashMap<>();
  public boolean passParentEnvs = false;
  public boolean stopOnError;
  public boolean unWrapExceptions;
  public Map<String, String> testEndCondition = new HashMap<>();
  public Map<String, String> suiteEndCondition = new HashMap<>();
  public Long seed = null;
  public String algorithm = "";
  private transient Map<String, EndConditionConfiguration> testEndConditions = new HashMap<>();
  private transient Map<String, EndConditionConfiguration> suiteEndConditions = new HashMap<>();
  public static final String EC_CLASS_NAME = "osmo.end.condition.class";

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

  public void setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
  }

  public boolean isStopOnError() {
    return stopOnError;
  }

  public void setUnWrapExceptions(boolean unWrapExceptions) {
    this.unWrapExceptions = unWrapExceptions;
  }

  public boolean isUnWrapExceptions() {
    return unWrapExceptions;
  }

  /**
   * Gives the configuration for the current selected test case end condition.
   * 
   * @param className The name of the current test end condition class.
   * @return The configuration (e.g., probability etc.)
   */
  public EndConditionConfiguration getTestConfigurationFor(String className) {
    return getConfigurationFor(testEndConditions, className);
  }

  public EndConditionConfiguration getSuiteConfigurationFor(String className) {
    return getConfigurationFor(suiteEndConditions, className);
  }

  private EndConditionConfiguration getConfigurationFor(Map<String, EndConditionConfiguration> map, String className) {
    EndConditionConfiguration ecc = map.get(className);
    if (ecc == null) {
      ecc = EndConditions.getConfigurationFor(className);
      if (!(ecc instanceof DefaultConfiguration)) {
        map.put(className, ecc);
      }
    }
    return ecc;
  }

  public void setAlgorithm(String className) {
    this.algorithm = className;
  }

  public void setTestEndCondition(String className) {
    this.testEndCondition = getTestConfigurationFor(className).getMap();
    this.testEndCondition.put(EC_CLASS_NAME, className);
  }

  public void setSuiteEndCondition(String className) {
    this.suiteEndCondition = getSuiteConfigurationFor(className).getMap();
    this.suiteEndCondition.put(EC_CLASS_NAME, className);
  }

  public Map<String, String> getTestEndCondition() {
    return testEndCondition;
  }

  public Map<String, String> getSuiteEndCondition() {
    return suiteEndCondition;
  }

  public void setSeed(String seed) {
    try {
      this.seed = Long.parseLong(seed);
      System.out.println("seed now:"+seed);
    } catch (NumberFormatException e) {
      log.debug("Failed to set seed from:" + seed, e);
    }
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public OSMORunParameters cloneMe() {
    OSMORunParameters clone = new OSMORunParameters();
    clone.setFrom(this);
    return clone;
  }
  
  public void setFrom(OSMORunParameters from) {
    this.algorithm = from.algorithm;
    this.vmParameters = from.vmParameters;
    this.alternativeJREPathEnabled = from.alternativeJREPathEnabled;
    this.alternativeJREPath = from.alternativeJREPath;
    this.project = from.project;
    this.runClass = from.runClass;
    this.packageName = from.packageName;
    this.programParameters = from.programParameters;
    this.workingDirectory = from.workingDirectory;
    this.passParentEnvs = from.passParentEnvs;
    this.stopOnError = from.stopOnError;
    this.unWrapExceptions = from.unWrapExceptions;
    this.seed = from.seed;
    this.testEndCondition.putAll(from.testEndCondition);
    this.suiteEndCondition.putAll(from.suiteEndCondition);
  }

  public Long getSeed() {
    return seed;
  }
}

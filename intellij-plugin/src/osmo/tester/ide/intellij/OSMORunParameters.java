package osmo.tester.ide.intellij;

import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import osmo.common.log.Logger;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.ide.intellij.endconditions.EndConditionConfiguration;
import osmo.tester.ide.intellij.endconditions.EndConditions;
import osmo.tester.ide.intellij.endconditions.UndefinedConfiguration;
import osmo.tester.model.ModelFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/** @author Teemu Kanstren */
public class OSMORunParameters {  
  public static final Logger log = new Logger(OSMORunParameters.class);
  public transient Project project = null;
  public String factoryClass = null;
  public String packageName = null;
  public boolean stopOnError;
  public boolean unWrapExceptions;
  private boolean packageInUse = false;
  private boolean factoryInUse = false;
  private boolean classesInUse = false;
  public EndConditionConfiguration testEndCondition = null;
  public EndConditionConfiguration suiteEndCondition = null;
  public Long seed = null;
  public String algorithm = "";
  public Collection<String> classes = new TreeSet<>();
  public Collection<String> filters = new TreeSet<>();
  public Collection<String> listeners = new TreeSet<>();
  
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

  public void setAlgorithm(String className) {
    this.algorithm = className;
  }

  public void setTestEndCondition(String className) {
    this.testEndCondition = EndConditions.getConfigurationFor(className);
    if (testEndCondition == null) {
      testEndCondition = new UndefinedConfiguration(className);
    }
  }

  public void setSuiteEndCondition(String className) {
    this.suiteEndCondition = EndConditions.getConfigurationFor(className);
    if (suiteEndCondition == null) {
      suiteEndCondition = new UndefinedConfiguration(className);
    }
  }

  public EndConditionConfiguration getTestEndCondition() {
    
    return testEndCondition;
  }

  public EndConditionConfiguration getSuiteEndCondition() {
    return suiteEndCondition;
  }

  public void setSeed(String seed) {
    try {
      this.seed = Long.parseLong(seed);
    } catch (NumberFormatException e) {
      log.debug("Failed to set seed from:" + seed, e);
    }
  }

  public void setPackageInUse(boolean packageInUse) {
    this.packageInUse = packageInUse;
  }

  public void setFactoryInUse(boolean factoryInUse) {
    this.factoryInUse = factoryInUse;
  }

  public void setClassesInUse(boolean classesInUse) {
    this.classesInUse = classesInUse;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public Long getSeed() {
    return seed;
  }

  public String getFactoryClass() {
    return factoryClass;
  }

  public void setFactoryClass(String factoryClass) {
    this.factoryClass = factoryClass;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public boolean isPackageInUse() {
    return packageInUse;
  }

  public boolean isFactoryInUse() {
    return factoryInUse;
  }

  public boolean isClassesInUse() {
    return classesInUse;
  }

  public void addClass(String qualifiedName) {
    this.classes.add(qualifiedName);
  }

  public void addFilter(String qualifiedName) {
    this.filters.add(qualifiedName);
  }

  public void addListener(String qualifiedName) {
    this.listeners.add(qualifiedName);
  }

  public void removeClass(String selection) {
    this.classes.remove(selection);
  }

  public void removeFilter(String selection) {
    this.filters.remove(selection);
  }

  public void removeListener(String selection) {
    this.listeners.remove(selection);
  }

  public Collection<String> getClasses() {
    return classes;
  }

  public void setClasses(Collection<String> classes) {
    this.classes = classes;
  }

  public Collection<String> getFilters() {
    return filters;
  }

  public void setFilters(Collection<String> filters) {
    this.filters = filters;
  }

  public Collection<String> getListeners() {
    return listeners;
  }

  public void setListeners(Collection<String> listeners) {
    this.listeners = listeners;
  }
}

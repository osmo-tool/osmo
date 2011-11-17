package osmo.tester.dsm;

import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class DSMConfiguration {
  private final List<String> stepRequirements = new ArrayList<String>();
  private final List<DataCoverageRequirement> dataRequirements = new ArrayList<DataCoverageRequirement>();
  private final Collection<String> testEndConditions = new ArrayList<String>();
  private final Collection<String> suiteEndConditions = new ArrayList<String>();
  private String modelFactory = null;
  private String algorithm = null;

  public void addStep(String req) {
    stepRequirements.add(req);
  }

  public void add(DataCoverageRequirement req) {
    dataRequirements.add(req);
  }

  public List<String> getStepRequirements() {
    return stepRequirements;
  }

  public List<DataCoverageRequirement> getDataRequirements() {
    return dataRequirements;
  }

  public boolean hasRequiments() {
    return dataRequirements.size() > 0 || stepRequirements.size() > 0;
  }

  public String getModelFactory() {
    return modelFactory;
  }

  public void setModelFactory(String modelFactory) {
    this.modelFactory = modelFactory;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getAlgorithm() {
    return algorithm;
  }
}

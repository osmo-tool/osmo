package osmo.tester.dsm;

import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedLessRandomAlgorithm;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for a DSM configured test generation session.
 *
 * @author Teemu Kanstren
 */
public class DSMConfiguration {
  /** List of names for test steps that need to be covered. */
  private final List<String> stepRequirements = new ArrayList<String>();
  /** List of data values for variables that needs to be covered. */
  private final List<DataCoverageRequirement> dataRequirements = new ArrayList<DataCoverageRequirement>();
  /** The object providing model objects. */
  private String modelFactory = null;
  /** The test generation algorithm. */
  private String algorithm = null;
  /** The random seed for OSMOTester. */
  private long seed = System.currentTimeMillis();

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

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

  /**
   * Sets the test generation algorithm. Can be custom, in which case it needs to be a fully qualified class name.
   * For built-in algorithms, it can be the fully-qualified name or "random"/"weighted random"/"optimized random".
   *
   * @param algorithm The name of the algorithm to use.
   */
  public void setAlgorithm(String algorithm) {
    if (algorithm.equalsIgnoreCase("random")) {
      algorithm = RandomAlgorithm.class.getName();
    }
    if (algorithm.equalsIgnoreCase("weighted random") || algorithm.equalsIgnoreCase("weighted-random") || algorithm.equalsIgnoreCase("weightedrandom")) {
      algorithm = WeightedLessRandomAlgorithm.class.getName();
    }
    if (algorithm.equalsIgnoreCase("optimized random") || algorithm.equalsIgnoreCase("optimized-random") || algorithm.equalsIgnoreCase("optimizedrandom")) {
      algorithm = WeightedLessRandomAlgorithm.class.getName();
    }
    this.algorithm = algorithm;
  }

  public String getAlgorithm() {
    return algorithm;
  }
}
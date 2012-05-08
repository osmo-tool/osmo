package osmo.tester.scripting.slicing;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.StepCoverage;
import osmo.tester.generator.endcondition.data.DataCoverage;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.generator.filter.MaxTransitionFilter;
import osmo.tester.scripting.OSMOConfigurationFactory;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

/**
 * The main class to be used as a basis to start the model slicing based test generation.
 * Reads the configuration from a file. The filename is given as argument from command-line.
 *
 * @author Teemu Kanstren
 */
public class SlicerMain {
  private static Logger log = new Logger(SlicerMain.class);
  /**
   * Start here.
   *
   * @param args First argument should be configuration filename.
   * @throws Exception If all fails (file not found, classes not found, ...)
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      throw new IllegalArgumentException("No argument given. You need to provide the configuration filename.");
    }
    String filename = args[0];
    FileInputStream in = new FileInputStream(filename);
    String input = TestUtils.getResource(in);
    AsciiParser parser = new AsciiParser();
    SlicingConfiguration conf = parser.parse(input);
    execute(conf);
  }

  /**
   * Generates tests based on model slicing configuration.
   *
   * @param slicingConfig The configuration for test generation.
   * @throws Exception If classes cannot instantiated, etc.
   */
  public static void execute(SlicingConfiguration slicingConfig) throws Exception {
    Class<?> fClass = Class.forName(slicingConfig.getModelFactory());
    OSMOConfigurationFactory factory = (OSMOConfigurationFactory) fClass.newInstance();
    OSMOConfiguration osmoConfig = factory.createConfiguration();
    log.debug("Starting slicer execution");
    GenerationListener listener = slicingConfig.getListener();
    if (listener != null) {
      log.debug("Adding listener:"+listener);
      osmoConfig.addListener(listener);
    }
    if (slicingConfig.getAlgorithm() != null) {
      Class<?> aClass = Class.forName(slicingConfig.getAlgorithm());
      FSMTraversalAlgorithm algorithm = (FSMTraversalAlgorithm) aClass.newInstance();
      log.debug("Setting algorithm:"+algorithm);
      osmoConfig.setAlgorithm(algorithm);
    }
    log.debug("Created OSMO Configuration:"+osmoConfig);
    if (slicingConfig.getSeed() != null) {
      osmoConfig.setSeed(slicingConfig.getSeed());
    }
    List<DataCoverageRequirement> dataRequirements = slicingConfig.getDataRequirements();
    DataCoverage dc = new DataCoverage();
    for (DataCoverageRequirement req : dataRequirements) {
      log.debug("Adding data coverage requirement:"+req);
      dc.addRequirement(req);
    }
    osmoConfig.addTestEndCondition(dc);
    MaxTransitionFilter filter = new MaxTransitionFilter();
    StepCoverage sc = new StepCoverage();
    Collection<StepRequirement> stepRequirements = slicingConfig.getStepRequirements();
    for (StepRequirement req : stepRequirements) {
      log.debug("Adding step coverage requirement:"+req);
      String step = req.getStep();
      Integer min = req.getMin();
      if (min != null) {
        for (int i = 0; i < min; i++) {
          sc.addRequiredStep(step);
        }
      }
      Integer max = req.getMax();
      if (max != null) {
        filter.setMax(step, max);
      }
    }
    osmoConfig.addTestEndCondition(sc);
    osmoConfig.addFilter(filter);
    osmoConfig.setSeed(osmoConfig.getSeed());
    osmoConfig.setScripter(slicingConfig.getScriptedValueProvider());
    log.debug("Starting generator");
    OSMOTester osmo = new OSMOTester();
    osmo.setConfig(osmoConfig);
    osmo.generate();
  }
}

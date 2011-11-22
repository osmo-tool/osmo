package osmo.tester.dsm;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.StepCoverage;
import osmo.tester.generator.endcondition.data.DataCoverage;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.generator.filter.MaxTransitionFilter;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

/**
 * The main class to be used as a basis to start the DSM based test generation.
 * Reads the configuration from a file. The filename is given as argument from command-line.
 *
 * @author Teemu Kanstren
 */
public class DSMMain {
  /**
   * Start here.
   *
   * @param args First argument should be configuration filename.
   * @throws Exception If all fails (file not found, classes not found, ...)
   */
  public static void main(String[] args) throws Exception {
    Logger.debug = true;
    if (args.length < 1) {
      throw new IllegalArgumentException("No argument given. You need to provide the configuration filename.");
    }
    String filename = args[0];
    System.out.println("partsing file:"+filename);
    FileInputStream in = new FileInputStream(filename);
    String input = TestUtils.getResource(in);
    AsciiParser parser = new AsciiParser();
    DSMConfiguration conf = parser.parse(input);
    execute(conf);
  }

  /**
   * Generates tests based on DSM configuration.
   *
   * @param config The configuration for test generation.
   * @throws Exception If classes cannot instantiated, etc.
   */
  public static void execute(DSMConfiguration config) throws Exception {
    OSMOTester osmo = new OSMOTester();
    Class<?> aClass = Class.forName(config.getAlgorithm());
    FSMTraversalAlgorithm algorithm = (FSMTraversalAlgorithm) aClass.newInstance();
    osmo.setAlgorithm(algorithm);
    Class<?> fClass = Class.forName(config.getModelFactory());
    ModelObjectFactory factory = (ModelObjectFactory) fClass.newInstance();
    Collection<Object> modelObjects = factory.createModelObjects();
    for (Object mo : modelObjects) {
      osmo.addModelObject(mo);
    }
    Collection<EndCondition> testEndConditions = factory.createTestEndConditions();
    for (EndCondition ec : testEndConditions) {
      osmo.addTestEndCondition(ec);
    }
    Collection<EndCondition> suiteEndConditions = factory.createSuiteEndConditions();
    for (EndCondition ec : suiteEndConditions) {
      osmo.addSuiteEndCondition(ec);
    }
    List<DataCoverageRequirement> dataRequirements = config.getDataRequirements();
    DataCoverage dc = new DataCoverage();
    for (DataCoverageRequirement req : dataRequirements) {
      dc.addRequirement(req);
    }
    osmo.addTestEndCondition(dc);
    MaxTransitionFilter filter = new MaxTransitionFilter();
    StepCoverage sc = new StepCoverage();
    Collection<StepRequirement> stepRequirements = config.getStepRequirements();
    for (StepRequirement req : stepRequirements) {
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
    osmo.addTestEndCondition(sc);
    osmo.addFilter(filter);
    osmo.setSeed(config.getSeed());
    osmo.setScriptedValueProvider(config.getScriptedValueProvider());
    osmo.generate();
  }
}

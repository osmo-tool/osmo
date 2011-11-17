package osmo.tester.dsm;

import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class DSMMain {
  public static void main(String[] args) throws Exception {
    String filename = args[0];
    FileInputStream in = new FileInputStream(filename);
    String input = TestUtils.getResource(in);
    AsciiParser parser = new AsciiParser();
    DSMConfiguration conf = parser.parse(input);
    OSMOTester osmo = new OSMOTester();
    Class<?> aClass = Class.forName(conf.getAlgorithm());
    FSMTraversalAlgorithm algorithm = (FSMTraversalAlgorithm) aClass.newInstance();
    osmo.setAlgorithm(algorithm);
    Class<?> fClass = Class.forName(conf.getModelFactory());
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
    osmo.generate();
  }
}

package osmo.tester.scripting.slicing;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.scripting.OSMOConfigurationFactory;
import osmo.tester.testmodels.VariableModel2;

/** @author Teemu Kanstren */
public class TestModelFactory2 implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setSeed(324);
    config.setFailWhenNoWayForward(false);
    config.addModelObject(new VariableModel2(System.out));
    config.addTestEndCondition(new Probability(0.05));
    config.addSuiteEndCondition(new Length(3));
    return config;
  }
}

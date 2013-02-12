package osmo.tester.scripting.slicing;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.testmodels.CalculatorModel;

/** @author Teemu Kanstren */
public class TestModelFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setSeed(132);
    config.addModelObject(new CalculatorModel());
    config.addTestEndCondition(new Probability(0.05));
    config.addSuiteEndCondition(new Length(3));
    return config;
  }
}

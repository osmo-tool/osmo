package osmo.tester.examples.helloworld;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scripting.OSMOConfigurationFactory;

/** @author Teemu Kanstren */
public class HelloFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setSeed(111);
    config.setFailWhenNoWayForward(false);
    config.addSuiteEndCondition(new Length(3));
    config.addTestEndCondition(new Length(5));
    config.addModelObject(new HelloModel());
    return config;
  }
}

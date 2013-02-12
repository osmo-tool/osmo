package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scripting.slicing.OSMOConfigurationFactory;

/** @author Teemu Kanstren */
public class SlicerFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setFailWhenNoWayForward(false);
    config.addSuiteEndCondition(new Length(3));
    config.addTestEndCondition(new Length(5));
    config.addModelObject(new HelloModel());
    return config;
  }
}

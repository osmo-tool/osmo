package osmo.tester.examples.tutorial.online;

import org.junit.runner.RunWith;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.reporting.junit.OSMOConfigurationProvider;
import osmo.tester.reporting.junit.OSMORunner;

/** @author Teemu Kanstren */
@RunWith(OSMORunner.class)
public class JUnitMain {
  @OSMOConfigurationProvider
  public static OSMOConfiguration configure() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.addModelObject(new HelloModel3());
    config.setJUnitLength(2);
    config.setTestEndCondition(new Length(5));
    return config;
  }
}

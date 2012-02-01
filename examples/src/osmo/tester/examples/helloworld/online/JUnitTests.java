package osmo.tester.examples.helloworld.online;

import org.junit.runner.RunWith;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.junit.OSMOConfigurationFactory;
import osmo.tester.junit.OSMORunner;

/** @author Teemu Kanstren */
@RunWith(OSMORunner.class)
public class JUnitTests {
  @OSMOConfigurationFactory
  public static OSMOConfiguration configure() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setSeed(345);
    config.addModelObject(new OnlineHelloModel());
    config.setJUnitLength(3);
    config.addTestEndCondition(new Length(5));
    return config;
  }
}

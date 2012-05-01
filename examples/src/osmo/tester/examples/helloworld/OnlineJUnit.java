package osmo.tester.examples.helloworld;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import osmo.tester.OSMOConfiguration;
import osmo.tester.reporting.junit.OSMOConfigurationFactory;
import osmo.tester.reporting.junit.OSMORunner;

/** @author Teemu Kanstren */
@RunWith(OSMORunner.class)
public class OnlineJUnit {
  @Before
  public void bfff() {
    System.out.println("BFF");
  }

  @OSMOConfigurationFactory
  public static OSMOConfiguration giefConf() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.addModelObject(new HelloModel());
    config.setJUnitLength(5);
    return config;
  }

  @After
  public void afff() {
    System.out.println("HLO");
  }
}

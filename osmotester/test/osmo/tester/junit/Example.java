package osmo.tester.junit;

import org.junit.runner.RunWith;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;

/** @author Teemu Kanstren */
@RunWith(OSMORunner.class)
public class Example {
  @OSMOConfigurationFactory
  public static OSMOConfiguration giefConf() {
//    Logger.debug = true;
    OSMOConfiguration config = new OSMOConfiguration();
    config.addModelObject(new ValidTestModel2(new Requirements(), System.out));
    config.setJunitLength(5);
    return config;
  }
}

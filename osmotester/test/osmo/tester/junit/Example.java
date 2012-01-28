package osmo.tester.junit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.CalculatorModel;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
@RunWith(OSMORunner.class)
public class Example {
  private static ByteArrayOutputStream out;
  private static PrintStream ps;

  static {
    out = new ByteArrayOutputStream(1000);
    ps = new PrintStream(out);
  }

  @Before
  public void hi() {
    ps.println("hi there");
  }

  @OSMOConfigurationFactory
  public static OSMOConfiguration giefConf() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.addModelObject(new CalculatorModel(ps));
    config.setJunitLength(5);
    return config;
  }

  @AfterClass
  public static void afterEight() {
//    System.out.println("all things:"+out.toString());
    //it is not possible to assert here or JUnit will swallow any error
//    assertEquals("", ps.toString());
  }
}

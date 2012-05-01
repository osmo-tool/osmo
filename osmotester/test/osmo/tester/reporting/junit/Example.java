package osmo.tester.reporting.junit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import osmo.tester.OSMOConfiguration;
import osmo.tester.testmodels.CalculatorModel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    config.setJUnitLength(5);
    return config;
  }

  @AfterClass
  public static void afterEight() {
//    System.out.println("all things:"+out.toString());
    //it is not possible to assert here or JUnit will swallow any error
//    assertEquals("", ps.toString());
  }
}

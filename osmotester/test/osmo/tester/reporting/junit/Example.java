package osmo.tester.reporting.junit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.CalculatorModel;
import osmo.tester.testmodels.ValidTestModel5;

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

  @OSMOConfigurationProvider
  public static OSMOConfiguration giefConf() {
    OSMOConfiguration config = new OSMOConfiguration();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(new CalculatorModel(ps));
    config.setFactory(factory);
    config.setJUnitLength(5);
    return config;
  }
}

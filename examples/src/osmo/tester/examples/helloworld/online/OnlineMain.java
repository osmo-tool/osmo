package osmo.tester.examples.helloworld.online;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class OnlineMain {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new OnlineHelloModel());
    OSMOConfiguration config = tester.getConfig();
    config.setSeed(345);
    config.setFailWhenError(true);
    config.addTestEndCondition(new Length(5));
    config.addSuiteEndCondition(new Length(3));
    config.setUnwrapExceptions(true);
    tester.generate();
  }
}

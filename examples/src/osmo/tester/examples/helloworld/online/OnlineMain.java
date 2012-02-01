package osmo.tester.examples.helloworld.online;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class OnlineMain {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new OnlineHelloModel());
    tester.setSeed(345);
    OSMOConfiguration config = tester.getConfig();
    config.setFailWhenError(false);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    tester.generate();
  }
}

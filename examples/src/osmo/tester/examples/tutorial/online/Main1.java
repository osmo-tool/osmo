package osmo.tester.examples.tutorial.online;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main1 {
  public static void main(String[] args) {
    OSMOConfiguration.setSeed(52);
    OSMOTester tester = new OSMOTester(new HelloModel1());
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(2));
    tester.generate();
  }
}

package osmo.tester.examples.helloworld;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.setSeed(345);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    tester.generate();
  }
}

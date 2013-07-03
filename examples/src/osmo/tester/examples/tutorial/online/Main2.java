package osmo.tester.examples.tutorial.online;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel2());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(2));
    tester.generate(52);
  }
}

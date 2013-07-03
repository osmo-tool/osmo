package osmo.tester.examples.tutorial.online;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main4 {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel3());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(4));
    tester.getConfig().setUnwrapExceptions(true);
    tester.getConfig().setFailWhenError(true);
    tester.generate(52);
  }
}

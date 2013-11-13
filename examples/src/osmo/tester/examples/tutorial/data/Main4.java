package osmo.tester.examples.tutorial.data;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main4 {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new HelloModel4());
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(2));
    tester.generate(52);
  }
}

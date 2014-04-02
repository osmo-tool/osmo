package osmo.tester.examples.tutorial.modularization;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) {
    ModelState state = new ModelState();
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(state);
    tester.addModelObject(new HelloModule(state));
    tester.addModelObject(new WorldModule(state));
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(2));
    tester.generate(52);
  }
}

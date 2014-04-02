package osmo.tester.examples.tutorial.scenario;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scenario.Scenario;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    scenario.addSlice("world", 0, 1);
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(8));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}

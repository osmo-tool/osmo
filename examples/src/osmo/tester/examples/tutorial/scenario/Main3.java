package osmo.tester.examples.tutorial.scenario;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scenario.Scenario;

/** @author Teemu Kanstren */
public class Main3 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    scenario.addSlice("world", 3, 0);
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(1));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}

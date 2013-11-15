package osmo.tester.examples.tutorial.scenario;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scenario.Scenario;

/** @author Teemu Kanstren */
public class Main1 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}

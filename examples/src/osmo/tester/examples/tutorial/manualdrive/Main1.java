package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.manualdrive.ManualAlgorithm;

/** @author Teemu Kanstren */
public class Main1 {
  public static void main(String[] args) {
    OSMOConfiguration.setSeed(52);
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.setAlgorithm(new ManualAlgorithm());
    tester.addTestEndCondition(new Length(100));
    tester.addSuiteEndCondition(new Length(100));
    tester.generate();
  }
}

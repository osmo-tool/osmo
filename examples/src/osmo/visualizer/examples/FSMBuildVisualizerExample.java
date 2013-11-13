package osmo.visualizer.examples;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.visualizer.fsmbuild.FSMBuildVisualizer;

/** @author Teemu Kanstren */
public class FSMBuildVisualizerExample {
  public static void main(String[] args) {
    FSMBuildVisualizer gv = new FSMBuildVisualizer();
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new CalculatorModel());
    osmo.setTestEndCondition(new Length(15));
    osmo.setSuiteEndCondition(new Length(5));
    osmo.addListener(gv);
    osmo.generate(55);
  }
}

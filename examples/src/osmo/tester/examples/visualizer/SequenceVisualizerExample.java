package osmo.tester.examples.visualizer;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.endcondition.Length;
import osmo.visualizer.tests.GraphVisualizer;

/** @author Teemu Kanstren */
public class SequenceVisualizerExample {
  public static void main(String[] args) {
    GraphVisualizer gv = new GraphVisualizer("Test");
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new CalculatorModel());
    osmo.setTestEndCondition(new Length(15));
    osmo.setSuiteEndCondition(new Length(5));
    osmo.addListener(gv);
    osmo.generate(55);
  }
}

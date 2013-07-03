package osmo.visualizer;

import osmo.tester.OSMOTester;
import osmo.visualizer.fsmbuild.FSMBuildVisualizer;

/** @author Teemu Kanstren */
public class BuildVisualizerExample {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new CalculatorModel());
    tester.addListener(new FSMBuildVisualizer());
    tester.generate(333);
  }
}

package osmo.visualizer;

import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.visualizer.fsmbuild.FSMBuildVisualizer;

/** @author Teemu Kanstren */
public class BuildVisualizerExample {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    tester.addListener(new FSMBuildVisualizer());
    tester.generate(333);
  }
}

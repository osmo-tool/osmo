package osmo.tester.examples.visualizer;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.visualizer.generator.TransitionsPieChart;

/** @author Teemu Kanstren */
public class TransitionPieChartExample {
  public static void main(String[] args) {
    TransitionsPieChart transitionsBarChart = new TransitionsPieChart();
    OSMOTester tester = new OSMOTester();
    tester.addListener(transitionsBarChart);
//    tester.addListener(new TransitionsPieChart());
    tester.addModelObject(new CalculatorModel());
    tester.setAlgorithm(new ManualAlgorithm(tester));
    tester.generate(55);
  }
}

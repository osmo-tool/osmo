package osmo.visualizer.examples;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.visualizer.generator.TransitionsPieChart;

/** @author Teemu Kanstren */
public class TransitionPieChartExample {
  public static void main(String[] args) {
    TransitionsPieChart transitionsBarChart = new TransitionsPieChart();
    OSMOTester tester = new OSMOTester();
    tester.addTestEndCondition(new Endless());
    tester.addListener(transitionsBarChart);
//    tester.addListener(new TransitionsPieChart());
    tester.addModelObject(new CalculatorModel());
    tester.setAlgorithm(new ManualAlgorithm());
    tester.generate();
  }
}

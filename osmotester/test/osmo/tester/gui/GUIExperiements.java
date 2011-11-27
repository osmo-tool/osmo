package osmo.tester.gui;

import osmo.tester.OSMOTester;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.tester.gui.manualdrive.ManualEndCondition;
import osmo.tester.testmodels.CalculatorModel;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class GUIExperiements {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    ManualEndCondition mec = new ManualEndCondition();
    tester.addTestEndCondition(mec);
    tester.addSuiteEndCondition(mec);
    tester.addModelObject(new CalculatorModel());
    tester.setAlgorithm(new ManualAlgorithm());
    tester.generate();
  }
}

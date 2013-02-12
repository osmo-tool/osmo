package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.slicing.SlicingGUI;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class Main3 {
  public static void main(String[] args) {
    OSMOConfiguration.setSeed(52);
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    FSM fsm = tester.getFsm();
    SlicingGUI g = new SlicingGUI(fsm);
    g.setVisible(true);
  }
}

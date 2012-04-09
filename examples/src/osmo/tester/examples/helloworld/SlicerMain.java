package osmo.tester.examples.helloworld;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.slicing.SlicingGUI;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class SlicerMain {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.setSeed(345);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    FSM fsm = tester.getFsm();
    SlicingGUI g = new SlicingGUI(fsm);
    g.setVisible(true);
  }
}

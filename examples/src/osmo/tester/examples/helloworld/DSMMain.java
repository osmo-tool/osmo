package osmo.tester.examples.helloworld;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.dsm.DSMGUI;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class DSMMain {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.setSeed(345);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    FSM fsm = tester.getFsm();
    DSMGUI g = new DSMGUI(fsm);
    g.setVisible(true);
  }
}

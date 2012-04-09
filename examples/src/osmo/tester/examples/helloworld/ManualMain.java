package osmo.tester.examples.helloworld;

import osmo.tester.OSMOTester;
import osmo.tester.gui.manualdrive.ManualAlgorithm;

/** @author Teemu Kanstren */
public class ManualMain {
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    osmo.setSeed(345);
    osmo.addModelObject(new HelloModel());
    osmo.setAlgorithm(new ManualAlgorithm());
    osmo.generate();
  }
}

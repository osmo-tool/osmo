package osmo.tester.examples.tutorial.basic;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;

/** @author Teemu Kanstren */
public class Main1 {
  public static void main(String[] args) {
    OSMOConfiguration.setSeed(52);
    OSMOTester tester = new OSMOTester(new HelloModel1());
    tester.generate();
  }
}

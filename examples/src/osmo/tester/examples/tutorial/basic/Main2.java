package osmo.tester.examples.tutorial.basic;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel2());
    tester.generate(52);
  }
}

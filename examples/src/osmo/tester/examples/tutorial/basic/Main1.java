package osmo.tester.examples.tutorial.basic;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;

/** @author Teemu Kanstren */
public class Main1 {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(HelloModel1.class));
    tester.generate(52);
  }
}

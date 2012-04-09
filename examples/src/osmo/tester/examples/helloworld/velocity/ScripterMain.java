package osmo.tester.examples.helloworld.velocity;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class ScripterMain {
  public static void main(String[] args) {
    HelloVelocityScripter scripter = new HelloVelocityScripter();
    HelloModelWithScripter model = new HelloModelWithScripter(scripter);
    OSMOTester tester = new OSMOTester(model);
    tester.setSeed(345);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    tester.generate();
    scripter.write();
  }
}

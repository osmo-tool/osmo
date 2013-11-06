package osmo.tester.examples.tutorial.scripting;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) {
    HelloVelocityScripter scripter = new HelloVelocityScripter();
    HelloModel2 model = new HelloModel2(scripter);
    OSMOTester tester = new OSMOTester();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(model);
    tester.setModelFactory(factory);
    tester.setTestEndCondition(new Length(5));
    tester.setSuiteEndCondition(new Length(2));
    tester.generate(52);
    scripter.write();
  }
}

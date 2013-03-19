
import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
import osmo.tester.annotation.EndCondition;
import osmo.tester.generator.endcondition.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.generator.algorithm.*;

/*
* This is automatically generated OSMO model
* Generated model author: Olli-Pekka Puolitaival
*/
public class TestModel_aa {

	public static void main(String[] args) {
		OSMOTester tester = new OSMOTester();
		tester.addModelObject(new TestModel());
		tester.setAlgorithm(new RandomAlgorithm());
		tester.addTestEndCondition(new Length(10));
		tester.addSuiteEndCondition(new Length(2));
		tester.generate();
	}
}

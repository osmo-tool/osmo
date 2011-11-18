
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
public class EmailApp_Regression {

	public static void main(String[] args) {
		OSMOTester tester = new OSMOTester();
		tester.addModelObject(new EmailApp());
		tester.setAlgorithm(new WeightedRandomAlgorithm());
		tester.addTestEndCondition(new Length(100));
		tester.addSuiteEndCondition(new Length(100));
		tester.generate();
	}
}


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
public class TestModel {
	private enum State{
		State2,
		State1,
		State3,
	}

	//Starting point
	State currentState = State.State1;

	@TestSuiteField
	private TestSuite history = new TestSuite();
	  
	@BeforeSuite
	public void beforeSuite(){
		System.out.println("Test suite starts...");
	}
	
	@BeforeTest
	public void beforeTest(){
		System.out.println("\nTest case "+(history.getAllTestCases().size()+1)+" start");
		currentState = State.State1;
	}

	@AfterTest
	public void afterTest(){
		System.out.println("Test case ends");
	}

	@BeforeSuite
	public void afterSuite(){
		System.out.println("Test suite ends");
	}

	@Guard("3_249")
	public boolean Guard_3_249(){
		return currentState == State.State3;
	}

	@Transition("3_249")
		public void Transition_3_249(){
		System.out.println("");
		currentState = State.State1;
	}

	@Guard("1_to_23_221")
	public boolean Guard_1_to_23_221(){
		return currentState == State.State1;
	}

	@Transition("1_to_23_221")
		public void Transition_1_to_23_221(){
		System.out.println("1_to_2");
		currentState = State.State2;
	}

	@Guard("2_to_33_213")
	public boolean Guard_2_to_33_213(){
		return currentState == State.State2;
	}

	@Transition("2_to_33_213")
		public void Transition_2_to_33_213(){
		System.out.println("2_to_3");
		currentState = State.State3;
	}

	@Guard("3_to_23_290")
	public boolean Guard_3_to_23_290(){
		return currentState == State.State3;
	}

	@Transition("3_to_23_290")
		public void Transition_3_to_23_290(){
		System.out.println("3_to_2");
		currentState = State.State2;
	}

	@EndCondition
	public boolean endCondition(){
		return currentState == State.State3 || false;
	}

	/**
	* Basic generation algorithm with default settings
	*/
	public static void main(String[] args) {		
		OSMOTester tester = new OSMOTester();
		tester.addTestEndCondition(new Endless());
		tester.addModelObject(new TestModel());
		tester.generate();
	}
}

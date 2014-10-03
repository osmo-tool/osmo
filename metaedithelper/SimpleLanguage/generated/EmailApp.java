
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
public class EmailApp {
	private enum State{
		Idle,
		Start_,
		Mail_read_view,
		Reply_view,
		Forward_view,
		Compose_view,
	}

	//Starting point
	State currentState = State.Start_;

	@TestSuiteField
	private TestSuite history = new TestSuite();
	  
	@BeforeSuite
	public void beforeSuite(){
		System.out.println("Test suite starts...");
	}
	
	@BeforeTest
	public void beforeTest(){
		System.out.println("\nTest case "+(history.getAllTestCases().size()+1)+" start");
		currentState = State.Start_;
	}

	@AfterTest
	public void afterTest(){
		System.out.println("Test case ends");
	}

	@BeforeSuite
	public void afterSuite(){
		System.out.println("Test suite ends");
	}

	@Guard("Click_a_mail3_473")
	public boolean Guard_Click_a_mail3_473(){
		return currentState == State.Idle;
	}

	@Transition("Click_a_mail3_473")
		public void Transition_Click_a_mail3_473(){
		System.out.println("Click a mail");
		currentState = State.Mail_read_view;
	}

	@Guard("Click_compose3_582")
	public boolean Guard_Click_compose3_582(){
		return currentState == State.Idle;
	}

	@Transition("Click_compose3_582")
		public void Transition_Click_compose3_582(){
		System.out.println("Click compose");
		currentState = State.Compose_view;
	}

	@Guard("Click_forward3_509")
	public boolean Guard_Click_forward3_509(){
		return currentState == State.Mail_read_view;
	}

	@Transition("Click_forward3_509")
		public void Transition_Click_forward3_509(){
		System.out.println("Click forward");
		currentState = State.Forward_view;
	}

	@Guard("Click_reply3_491")
	public boolean Guard_Click_reply3_491(){
		return currentState == State.Mail_read_view;
	}

	@Transition("Click_reply3_491")
		public void Transition_Click_reply3_491(){
		System.out.println("Click reply");
		currentState = State.Reply_view;
	}

	@Guard("Discard3_542")
	public boolean Guard_Discard3_542(){
		return currentState == State.Reply_view;
	}

	@Transition("Discard3_542")
		public void Transition_Discard3_542(){
		System.out.println("Discard");
		currentState = State.Idle;
	}

	@Guard("Discard3_564")
	public boolean Guard_Discard3_564(){
		return currentState == State.Forward_view;
	}

	@Transition("Discard3_564")
		public void Transition_Discard3_564(){
		System.out.println("Discard");
		currentState = State.Idle;
	}

	@Guard("Discard3_604")
	public boolean Guard_Discard3_604(){
		return currentState == State.Compose_view;
	}

	@Transition("Discard3_604")
		public void Transition_Discard3_604(){
		System.out.println("Discard");
		currentState = State.Idle;
	}

	@Guard("Go_back3_520")
	public boolean Guard_Go_back3_520(){
		return currentState == State.Mail_read_view;
	}

	@Transition("Go_back3_520")
		public void Transition_Go_back3_520(){
		System.out.println("Go back");
		currentState = State.Idle;
	}

	@Guard("Refresh3_455")
	public boolean Guard_Refresh3_455(){
		return currentState == State.Idle;
	}

	@Transition("Refresh3_455")
		public void Transition_Refresh3_455(){
		System.out.println("Refresh");
		currentState = State.Idle;
	}

	@Guard("Send3_531")
	public boolean Guard_Send3_531(){
		return currentState == State.Reply_view;
	}

	@Transition("Send3_531")
		public void Transition_Send3_531(){
		System.out.println("Send");
		currentState = State.Idle;
	}

	@Guard("Send3_553")
	public boolean Guard_Send3_553(){
		return currentState == State.Forward_view;
	}

	@Transition("Send3_553")
		public void Transition_Send3_553(){
		System.out.println("Send");
		currentState = State.Idle;
	}

	@Guard("Send3_593")
	public boolean Guard_Send3_593(){
		return currentState == State.Compose_view;
	}

	@Transition("Send3_593")
		public void Transition_Send3_593(){
		System.out.println("Send");
		currentState = State.Idle;
	}

	@Guard("Start_app3_444")
	public boolean Guard_Start_app3_444(){
		return currentState == State.Start_;
	}

	@Transition("Start_app3_444")
		public void Transition_Start_app3_444(){
		System.out.println("Start app");
		currentState = State.Idle;
	}

	@EndCondition
	public boolean endCondition(){
		return false;
	}

	/**
	* Basic generation algorithm with default settings
	*/
	public static void main(String[] args) {		
		OSMOTester tester = new OSMOTester();
		tester.addModelObject(new EmailApp());
		tester.generate();
	}
}

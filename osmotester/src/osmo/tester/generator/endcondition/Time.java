package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * A simple end condition to stop test case generation when the time is ended in generating test case or test suite
 * 
 * @author Olli-Pekka Puolitaival
 */
public class Time extends AbstractEndCondition {
  private static Logger log = new Logger(Length.class);
  /** The stopping time in seconds. */
  private long treshold = 0;
  private long starttime = 0;

  /**
   * Constructor.
   *
   * @param sec The time in seconds when to stop generation.
   */
  public Time(int seconds) {
    if (seconds < 0) {
      throw new IllegalArgumentException("Time in sec cannot be < 0, was " + seconds + ".");
    }
    this.treshold = (long)(seconds*1000);
    starttime = System.currentTimeMillis();
  }

  /**
   * Constructor.
   *
   * @param sec The time in seconds when to stop generation.
   */
  public Time(double seconds) {
    if (seconds < 0) {
      throw new IllegalArgumentException("Time in sec cannot be < 0, was " + seconds + ".");
    }
    this.treshold = (long)(seconds*1000);
    System.out.println("Treshold: "+treshold);
    starttime = System.currentTimeMillis();
  }
  
  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    log.debug(" es:" + suite.getFinishedTestCases().size() + " c:" + suite.currentSteps());
    return (System.currentTimeMillis()-starttime) >= treshold;    
  }
	
  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.debug(" es:" + suite.getFinishedTestCases().size() + " c:" + suite.currentSteps());
    if((System.currentTimeMillis()-starttime) >= treshold){
        starttime = System.currentTimeMillis();
        return true;
    }
    return false;
  }
	
  @Override
  public void init(FSM fsm) {
  }

}

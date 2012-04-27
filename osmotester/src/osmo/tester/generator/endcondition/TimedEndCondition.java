package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** 
 * An end condition that allows defining the time used for test/suite generation.
 * Uses {@link ScheduledThreadPoolExecutor} to implement timing.
 * 
 * @author Teemu Kanstren 
 */
public class TimedEndCondition extends AbstractEndCondition {
  private boolean shouldEnd = false;
  private final long delay;
  private final TimeUnit timeUnit;

  public TimedEndCondition(long delay, TimeUnit timeUnit) {
    if (delay <= 0) {
      throw new IllegalArgumentException("Time for "+TimedEndCondition.class.getSimpleName()+" should be > 0, was "+delay);
    }
    this.delay = delay;
    this.timeUnit = timeUnit;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return shouldEnd;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return shouldEnd;
  }

  @Override
  public void init(FSM fsm) {
    shouldEnd = false;
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    Runnable end = new Runnable() {
      @Override
      public void run() {
        shouldEnd = true;
      }
    };
    executor.schedule(end, delay, timeUnit);
  }
}

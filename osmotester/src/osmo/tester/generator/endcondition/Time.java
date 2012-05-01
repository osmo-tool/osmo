package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple end condition to stop test case generation when the time is ended in generating test case or test suite
 * 
 * @author Olli-Pekka Puolitaival
 */
public class Time extends AbstractEndCondition {
  private boolean shouldEnd = false;
  private final long delay;
  private final TimeUnit timeUnit;

  public Time(long hours, long minutes, long seconds) {
    this(hours*60*60+minutes*60+seconds);
  }

  public Time(long minutes, long seconds) {
    this(minutes*60+seconds);
  }

  public Time(long seconds) {
    this(seconds, TimeUnit.SECONDS);
  }

  public Time(long delay, TimeUnit timeUnit) {
    if (delay <= 0) {
      throw new IllegalArgumentException("Time for "+Time.class.getSimpleName()+" should be > 0, was "+delay);
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

  @Override
  public String toString() {
    return "Time{" +
            "delay=" + delay +
            ", timeUnit=" + timeUnit +
            '}';
  }
}

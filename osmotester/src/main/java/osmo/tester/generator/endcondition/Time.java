package osmo.tester.generator.endcondition;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Signals generation ending when the given time has elapsed.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class Time implements EndCondition {
  /** Indicates whether time has passed or not, meaning should generation be stopped. */
  private boolean shouldEnd = false;
  /** The number of time units until this should return true. */
  private final long delay;
  /** The time unit used for evaluating time until signalling end. */
  private final TimeUnit timeUnit;

  public Time(long hours, long minutes, long seconds) {
    this(hours * 60 * 60 + minutes * 60 + seconds);
  }

  public Time(long minutes, long seconds) {
    this(minutes * 60 + seconds);
  }

  public Time(long seconds) {
    this(seconds, TimeUnit.SECONDS);
  }

  public Time(long delay, TimeUnit timeUnit) {
    if (delay <= 0) {
      throw new IllegalArgumentException("Time for " + Time.class.getSimpleName() + " should be > 0, was " + delay);
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
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    shouldEnd = false;
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    Runnable task = () -> shouldEnd = true;
    executor.schedule(task, delay, timeUnit);
  }

  public long getDelay() {
    return delay;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public String toString() {
    return "Time{" +
            "delay=" + delay +
            ", timeUnit=" + timeUnit +
            '}';
  }

  @Override
  public EndCondition cloneMe() {
    //we need a copy as running the same instance in parallel would break on the shared timer task and shared boolean end condition
    return new Time(delay, timeUnit);
  }
}

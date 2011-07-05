package osmo.tester.generator;

/**
 * A listener interface that can be implemented to receive notifications about the progress of test generation.
 *
 * @author Teemu Kanstren
 */
public interface GenerationListener {
  /**
   * A guard statement has been invoked.
   *
   * @param name Name of the guard statement as in @Guard("name").
   */
  public void guard(String name);

  /**
   * A transition has been invoked.
   *
   * @param name Name of the transition as in @Transition("name").
   */
  public void transition(String name);

  /**
   * A test oracle has been invoked.
   *
   * @param name Name of the test oracle as in @Oracle("name").
   */
  public void oracle(String name);

  /**
   * The generation of a new test case has started.
   */
  public void testStarted();

  /**
   * The generation of a new test case has ended.
   */
  public void testEnded();

  /**
   * Test suite generation has started.
   */
  public void suiteStarted();

  /**
   * Test suite generation has ended.
   */
  public void suiteEnded();
}

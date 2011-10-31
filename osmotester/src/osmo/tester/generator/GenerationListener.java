package osmo.tester.generator;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * A listener interface that can be implemented to receive notifications about the progress of test generation.
 *
 * @author Teemu Kanstren
 */
public interface GenerationListener {
  /**
   * A guard statement has been invoked for a transition.
   *
   * @param transition The associated transition.
   */
  public void guard(FSMTransition transition);

  /**
   * A transition has been invoked.
   *
   * @param transition The associated transition.
   */
  public void transition(FSMTransition transition);

  /**
   * A pre method has been invoked.
   *
   * @param transition The associated transition.
   */
  public void pre(FSMTransition transition);

  /**
   * A post method has been invoked.
   *
   * @param transition The associated transition.
   */
  public void post(FSMTransition transition);

  /**
   * The generation of a new test case has started.
   *
   * @param test The associated test object.
   */
  public void testStarted(TestCase test);

  /**
   * The generation of a new test case has ended.
   *
   * @param test The associated test object.
   */
  public void testEnded(TestCase test);

  /**
   * Test suite generation has started.
   *
   * @param suite The associated test suite object.
   */
  public void suiteStarted(TestSuite suite);

  /**
   * Test suite generation has ended.
   *
   * @param suite The associated test suite object.
   */
  public void suiteEnded(TestSuite suite);
}

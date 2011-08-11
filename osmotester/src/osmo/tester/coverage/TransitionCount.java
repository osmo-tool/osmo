package osmo.tester.coverage;

import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class TransitionCount {
  private final FSMTransition transition;
  private final int count;

  public TransitionCount(FSMTransition transition, int count) {
    this.transition = transition;
    this.count = count;
  }

  public FSMTransition getTransition() {
    return transition;
  }

  public int getCount() {
    return count;
  }

  public String getName() {
    return transition.getName();
  }
}

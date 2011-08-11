package osmo.tester.coverage;

import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class TransitionPairCount {
  private final FSMTransition from;
  private final FSMTransition to;
  private final int count;

  public TransitionPairCount(FSMTransition from, FSMTransition to, int count) {
    this.from = from;
    this.to = to;
    this.count = count;
  }

  public FSMTransition getFrom() {
    return from;
  }

  public FSMTransition getTo() {
    return to;
  }

  public int getCount() {
    return count;
  }
}

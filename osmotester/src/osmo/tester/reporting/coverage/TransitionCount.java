package osmo.tester.reporting.coverage;

/**
 * Information for how many times a transition has been taken in test generation.
 *
 * @author Teemu Kanstren
 */
public class TransitionCount implements Comparable<TransitionCount> {
  /** The transition taken. */
  private final String transition;
  /** The number of times it was taken. */
  private final int count;

  public TransitionCount(String transition, int count) {
    this.transition = transition;
    this.count = count;
  }

  /** @return The number of times the transition has been taken. */
  public int getCount() {
    return count;
  }

  /** @return Name of the transition. */
  public String getName() {
    return transition;
  }

  @Override
  public int compareTo(TransitionCount o) {
    int countDiff = o.count - count;
    if (countDiff == 0) {
      countDiff = getName().compareTo(o.getName());
    }
    return countDiff;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransitionCount that = (TransitionCount) o;

    if (transition != null ? !transition.equals(that.transition) : that.transition != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return transition != null ? transition.hashCode() : 0;
  }
}

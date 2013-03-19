package osmo.tester.reporting.coverage;

/**
 * Describes a pair of transitions, where the transitions have occurred in this sequence in a test case.
 *
 * @author Teemu Kanstren
 */
public class TransitionPair {
  /** The transition that happened first. */
  private final String from;
  /** The transition that happened after the "from" transition. */
  private final String to;

  public TransitionPair(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransitionPair that = (TransitionPair) o;

    if (from != null ? !from.equals(that.from) : that.from != null) return false;
    if (to != null ? !to.equals(that.to) : that.to != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = from != null ? from.hashCode() : 0;
    result = 31 * result + (to != null ? to.hashCode() : 0);
    return result;
  }
}

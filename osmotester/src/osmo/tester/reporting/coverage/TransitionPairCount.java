package osmo.tester.reporting.coverage;

/**
 * Information for how many times a transition pair has been taken in test generation.
 * A transition pair consists of two transition taken in a sequence in a single test case.
 *
 * @author Teemu Kanstren
 */
public class TransitionPairCount implements Comparable<TransitionPairCount> {
  /** The pair itself. */
  private final TransitionPair pair;
  /** The number of times this pair has been observed in test generation. */
  private final int count;

  public TransitionPairCount(TransitionPair pair, int count) {
    this.pair = pair;
    this.count = count;
  }

  /** @return The first transition of the pair. */
  public String getFrom() {
    return pair.getFrom();
  }

  /** @return The second (latter) transition of the pair. */
  public String getTo() {
    return pair.getTo();
  }

  public int getCount() {
    return count;
  }

  @Override
  public int compareTo(TransitionPairCount o) {
    int countDiff = o.count - count;
    if (countDiff == 0) {
      String name1 = pair.getFrom();
      String name2 = o.getFrom();
      countDiff = name1.compareTo(name2);
      if (countDiff == 0) {
        name1 = pair.getTo();
        name2 = o.getTo();
        countDiff = name1.compareTo(name2);
      }
    }
    return countDiff;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransitionPairCount that = (TransitionPairCount) o;

    if (pair != null ? !pair.equals(that.pair) : that.pair != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return pair != null ? pair.hashCode() : 0;
  }
}

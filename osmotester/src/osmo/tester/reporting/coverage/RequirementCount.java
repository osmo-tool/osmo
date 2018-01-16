package osmo.tester.reporting.coverage;

/**
 * Tracks the requirements count. That is, how many times has the requirement been covered by a test case.
 * Used in report templates through Velocity.
 *
 * @author Teemu Kanstren
 */
public class RequirementCount implements Comparable<RequirementCount> {
  /** Tag name. */
  private final String name;
  /** Times covered. */
  private final int count;

  public RequirementCount(String name, int count) {
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public int getCount() {
    return count;
  }

  @Override
  public int compareTo(RequirementCount o) {
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

    RequirementCount that = (RequirementCount) o;

      return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}

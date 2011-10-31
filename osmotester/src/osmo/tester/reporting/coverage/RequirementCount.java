package osmo.tester.reporting.coverage;

/** @author Teemu Kanstren */
public class RequirementCount implements Comparable<RequirementCount> {
  private final String name;
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

    if (name != null ? !name.equals(that.name) : that.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}

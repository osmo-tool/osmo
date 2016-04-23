package osmo.tester.model;

/** 
 * Internally used to represent names for test steps.
 * Using a specific prefix allows one to re-use the same model class several times as different instances.
 * For example, create a class to represent a user. Instanciate several times as "bob", "alice", "teemu" with prefix.
 * The prefix is what allows the generator to separate these and not consider the names duplicates and as such errors.
 * 
 * @author Teemu Kanstren 
 */
public class TransitionName {
  /** Extra identifier to potentially group the transitions. */
  private final String prefix;
  /** The actual name given in the model class. */
  private final String name;

  public TransitionName(String prefix, String name) {
    this.prefix = prefix;
    this.name = name;
  }

  public int length() {
    return toString().length();
  }

  @Override
  public String toString() {
    return prefix + name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransitionName that = (TransitionName) o;

    //special check to avoid unnamed groups for transitions
    if (name.length() == 0) return false;
    
    if (name != null ? !name.equalsIgnoreCase(that.name) : that.name != null) return false;
    if (prefix != null ? !prefix.equalsIgnoreCase(that.prefix) : that.prefix != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = prefix != null ? prefix.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }

  /**
   * Checks if the given name is different from this name.
   * Also checks for empty group name, which is why this exists.
   * 
   * @param negationName Name we should not match.
   * @return True if our name does not match the given one.
   */
  public boolean shouldNegationApply(TransitionName negationName) {
    if (!prefix.equalsIgnoreCase(negationName.prefix)) {
      //wrong prefix
      return false;
    }
    //special check to avoid unnamed groups for transitions
    if (name.length() == 0) return false;

    return !negationName.name.equalsIgnoreCase(name);
  }

  public String getPrefix() {
    return prefix;
  }
}

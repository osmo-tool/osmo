package osmo.tester.model;

/** @author Teemu Kanstren */
public class TransitionName {
  /**
   * Extra identifier to potentially group the transitions. For example, create a model of a smartphone and
   * use the same model for several phones in test generation. Give them prefixes such as "HTC", "Google", etc.
   * This will cause them to be considered as separate transitions in test generation. This is actually already part
   * of the transition/guard names since parsing but used here to help address "negation" in associations.
   */
  private final String prefix;
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

package osmo.tester.model;

/** 
 * Represents a {@link osmo.tester.annotation.Guard} in the test model.
 * 
 * @author Teemu Kanstren 
 */
public class FSMGuard {
  /** Name of step to which this is associated to. */
  private final TransitionName name;
  /** The actual method to invoke to get the enabled state. */
  private final InvocationTarget target;
  /** How many times was this matched to a step/group in the model? For validity checking. */
  private int count = 0;

  public FSMGuard(TransitionName name, InvocationTarget target) {
    this.name = name;
    this.target = target;
  }

  public TransitionName getName() {
    return name;
  }

  public InvocationTarget getTarget() {
    return target;
  }

  /**
   * This was found to be associated to something, thus likely being valid.
   */
  public void found() {
    count++;
  }

  public int getCount() {
    return count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FSMGuard that = (FSMGuard) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (target != null ? !target.equals(that.target) : that.target != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (target != null ? target.hashCode() : 0);
    return result;
  }
}

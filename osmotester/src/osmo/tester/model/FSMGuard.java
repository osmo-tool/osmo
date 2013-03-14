package osmo.tester.model;

/** @author Teemu Kanstren */
public class FSMGuard {
  private final TransitionName name;
  private final InvocationTarget target;
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

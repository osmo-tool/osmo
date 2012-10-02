package osmo.tester.model;

/** @author Teemu Kanstren */
public class NegatedGuard {
  private final TransitionName name;
  private final InvocationTarget target;

  public NegatedGuard(TransitionName name, InvocationTarget target) {
    this.name = name;
    this.target = target;
  }

  public TransitionName getName() {
    return name;
  }

  public InvocationTarget getTarget() {
    return target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NegatedGuard that = (NegatedGuard) o;

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

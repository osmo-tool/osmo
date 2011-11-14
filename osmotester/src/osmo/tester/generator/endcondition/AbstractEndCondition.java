package osmo.tester.generator.endcondition;

/** @author Teemu Kanstren */
public abstract class AbstractEndCondition implements EndCondition {
  private boolean strict = false;

  @Override
  public boolean isStrict() {
    return strict;
  }

  @Override
  public void setStrict(boolean strict) {
    this.strict = strict;
  }
}

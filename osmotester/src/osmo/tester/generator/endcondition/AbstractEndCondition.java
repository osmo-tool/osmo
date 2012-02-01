package osmo.tester.generator.endcondition;

/**
 * A base class for different end conditions.
 *
 * @author Teemu Kanstren
 */
public abstract class AbstractEndCondition implements EndCondition {
  private boolean strict = false;

  /**
   * If true, the end condition should immediately stop test generation when it returns true.
   * If false, all other end conditions need to be true as well for test generation to stop.
   *
   * @return True if strict.
   */
  @Override
  public boolean isStrict() {
    return strict;
  }

  /** @param strict The new strict value. */
  @Override
  public void setStrict(boolean strict) {
    this.strict = strict;
  }
}

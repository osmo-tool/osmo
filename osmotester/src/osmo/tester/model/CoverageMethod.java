package osmo.tester.model;

import osmo.tester.generator.testsuite.TestCaseStep;

/** 
 * Represents a {@link osmo.tester.annotation.CoverageValue} method in the model. 
 * 
 * @author Teemu Kanstren 
 */
public class CoverageMethod {
  /** Name given to the method, used to track as variable for coverage. */
  private final String variableName;
  /** The actual method to invoke to produce desired effect. */
  private final InvocationTarget invocationTarget;

  public CoverageMethod(String variableName, InvocationTarget invocationTarget) {
    this.variableName = variableName;
    this.invocationTarget = invocationTarget;
  }

  public String getVariableName() {
    return variableName;
  }

  public InvocationTarget getInvocationTarget() {
    return invocationTarget;
  }

  public String invoke(TestCaseStep step) {
    return (String)invocationTarget.invoke(step);
  }

  /**
   * Creates a name for a coverage value pair for this object.
   * 
   * @return Name with "-pair" attached to end.
   */
  public String getPairName() {
    return variableName+"-pair";
  }
}

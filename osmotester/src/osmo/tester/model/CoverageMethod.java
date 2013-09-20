package osmo.tester.model;

import osmo.tester.generator.testsuite.TestCaseStep;

/** @author Teemu Kanstren */
public class CoverageMethod {
  private final String variableName;
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

  public String getPairName() {
    return variableName+"-pair";
  }
}

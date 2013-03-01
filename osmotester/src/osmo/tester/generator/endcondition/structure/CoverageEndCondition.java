package osmo.tester.generator.endcondition.structure;

import osmo.tester.generator.endcondition.AbstractEndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/** 
 * An end condition for test generation that defines a set of requirements related to the model structure.
 * 
 * @author Teemu Kanstren 
 */
public class CoverageEndCondition extends AbstractEndCondition {
  /** Defines what needs to be covered for this to end. */
  private final CoverageRequirement requirement;

  public CoverageEndCondition(CoverageRequirement requirement) {
    this.requirement = requirement;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return requirement.checkCoverage(suite);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return requirement.checkCoverage(suite.getCurrentTest());
  }

  @Override
  public void init(FSM fsm) {
    requirement.init(fsm);
  }
}

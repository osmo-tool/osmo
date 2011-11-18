package osmo.tester.dsm;

import osmo.tester.generator.endcondition.EndCondition;

import java.util.Collection;

/**
 * A factory providing elements for the DSM generator.
 *
 * @author Teemu Kanstren
 */
public interface ModelObjectFactory {
  /**
   * The set of model object for test model. OSMOTester.addModelObject();
   *
   * @return User created model objects.
   */
  public Collection<Object> createModelObjects();

  /**
   * Test end conditions. Added to OSMOTester in addition to the StepCoverage and DataCoverage end conditions.
   *
   * @return User specified additional end conditions for tests.
   */
  public Collection<EndCondition> createTestEndConditions();

  /**
   * Test suite end conditions. By default, DSM generator uses the OSMOTester default suite end conditions.
   *
   * @return Use specific end conditions to replace the default ones.
   */
  public Collection<EndCondition> createSuiteEndConditions();
}

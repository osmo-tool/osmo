package osmo.tester.dsm;

import osmo.tester.generator.endcondition.EndCondition;

import java.util.Collection;

/** @author Teemu Kanstren */
public interface ModelObjectFactory {
  public Collection<Object> createModelObjects();
  public Collection<EndCondition> createTestEndConditions();
  public Collection<EndCondition> createSuiteEndConditions();
}

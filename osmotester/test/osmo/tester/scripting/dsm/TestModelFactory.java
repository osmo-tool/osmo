package osmo.tester.scripting.dsm;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.testmodels.CalculatorModel;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestModelFactory implements ModelObjectFactory {
  @Override
  public Collection<Object> createModelObjects() {
    Object model = new CalculatorModel();
    Collection<Object> objects = new ArrayList<>();
    objects.add(model);
    return objects;
  }

  @Override
  public Collection<EndCondition> createTestEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Probability(0.05));
    return end;
  }

  @Override
  public Collection<EndCondition> createSuiteEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Length(3));
    return end;
  }
}

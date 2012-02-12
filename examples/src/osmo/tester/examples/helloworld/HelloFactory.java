package osmo.tester.examples.helloworld;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scripting.dsm.ModelObjectFactory;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class HelloFactory implements ModelObjectFactory {
  @Override
  public Collection<Object> createModelObjects() {
    Object model = new HelloModel();
    Collection<Object> objects = new ArrayList<>();
    objects.add(model);
    return objects;
  }

  @Override
  public Collection<EndCondition> createTestEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Length(5));
    return end;
  }

  @Override
  public Collection<EndCondition> createSuiteEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Length(3));
    return end;
  }
}

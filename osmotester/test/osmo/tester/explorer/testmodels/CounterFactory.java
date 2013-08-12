package osmo.tester.explorer.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class CounterFactory implements ModelFactory {
  @Override
  public Collection<ModelObject> createModelObjects() {
    Collection<ModelObject> result = new ArrayList<>();
    result.add(new ModelObject(new CounterModel()));
    return result;
  }
}

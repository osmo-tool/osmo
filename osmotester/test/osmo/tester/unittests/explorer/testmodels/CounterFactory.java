package osmo.tester.unittests.explorer.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

/** @author Teemu Kanstren */
public class CounterFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels addHere) {
    addHere.add(new CounterModel());
  }
}

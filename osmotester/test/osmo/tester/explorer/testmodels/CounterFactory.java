package osmo.tester.explorer.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class CounterFactory implements ModelFactory {
  @Override
  public TestModels createModelObjects() {
    TestModels result = new TestModels();
    result.add(new CounterModel());
    return result;
  }
}

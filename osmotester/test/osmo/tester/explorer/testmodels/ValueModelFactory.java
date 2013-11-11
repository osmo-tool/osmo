package osmo.tester.explorer.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class ValueModelFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels addHere) {
    addHere.add(new ValueModel());
  }
}

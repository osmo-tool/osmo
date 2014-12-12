package osmo.tester.unittests.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.TestModels;

/**
 * @author Teemu Kanstren
 */
public class Model10Factory implements ModelFactory {
  private final Requirements req = new Requirements();

  @Override
  public void createModelObjects(TestModels addThemHere) {
    addThemHere.add(new Model10(req));
  }
}

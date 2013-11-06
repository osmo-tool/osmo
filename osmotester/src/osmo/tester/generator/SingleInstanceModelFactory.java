package osmo.tester.generator;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

/** @author Teemu Kanstren */
public class SingleInstanceModelFactory implements ModelFactory {
  private TestModels models = new TestModels();
  
  public void add(Object obj) {
    models.add(obj);
  }
  
  public void add(String prefix, Object obj) {
    models.add(prefix, obj);
  }
  
  @Override
  public TestModels createModelObjects() {
    return models;
  }
}

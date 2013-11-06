package osmo.tester.model;

import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestModels {
  private Collection<ModelObject> models = new ArrayList<>();
  
  public void add(Object model) {
    models.add(new ModelObject(model));
  }

  public void add(String prefix, Object model) {
    models.add(new ModelObject(prefix, model));
  }

  public Collection<ModelObject> getModels() {
    return models;
  }
  
  public int size() {
    return models.size();
  }
}

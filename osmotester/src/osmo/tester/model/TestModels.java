package osmo.tester.model;

import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** 
 * Holds a collection of model objects. Intended to make factory creation easier for the user.
 * 
 * @author Teemu Kanstren 
 */
public class TestModels {
  /** The list of model objects for the model. */
  private Collection<ModelObject> models = new ArrayList<>();

  /**
   * Add new model object, no prefix.
   * 
   * @param model To add.
   */
  public void add(Object model) {
    models.add(new ModelObject(model));
  }

  /**
   * Add new model object, with prefix.
   *
   * @param prefix The model name prefix.
   * @param model To add.
   */
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

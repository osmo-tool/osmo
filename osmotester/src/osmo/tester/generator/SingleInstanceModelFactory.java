package osmo.tester.generator;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class SingleInstanceModelFactory implements ModelFactory {
  private Collection<ModelObject> models = new ArrayList<>();
  
  public void add(Object obj) {
    models.add(new ModelObject(obj));
  }
  
  public void add(String prefix, Object obj) {
    models.add(new ModelObject(prefix, obj));
  }
  
  @Override
  public void createModelObjects(TestModels addHere) {
    for (ModelObject model : models) {
      addHere.add(model.getPrefix(), model.getObject());
    }
  }
}

package osmo.tester.generator;

import osmo.common.log.Logger;
import osmo.tester.model.ModelFactory;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/** 
 * Default factory used for creating simple model objects.
 * These are the ones that have a no-args constructor to use and so can be created via reflection.
 * 
 * @author Teemu Kanstren 
 */
public class SimpleModelFactory implements ModelFactory {
  private static Logger log = new Logger(MainGenerator.class);
  /** List of classes to instantiate as the model objects. */
  private final Collection<Class> classes = new ArrayList<>();

  public SimpleModelFactory() {
  }

  public SimpleModelFactory(Class... classes) {
    Collections.addAll(this.classes, classes);
  }
  
  public void addModelClass(Class modelClass) {
    this.classes.add(modelClass);
  }

  @Override
  public Collection<ModelObject> createModelObjects() {
    Collection<ModelObject> result = new ArrayList<>();
    for (Class aClass : classes) {
      try {
        result.add(new ModelObject(aClass.newInstance()));
      } catch (Exception e) {
        log.error("Failed to create a model class instance. Exiting.", e);
        System.exit(1);
      }
    }
    return result;
  }
}

package osmo.tester.generator;

import osmo.common.Logger;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/** 
 * Default factory used for creating simple model objects.
 * These are the ones that have a no-args constructor to use and so can be created via reflection.
 * 
 * @author Teemu Kanstren 
 */
public class ReflectiveModelFactory implements ModelFactory {
  private static final Logger log = new Logger(MainGenerator.class);
  /** List of classes to instantiate as the model objects. */
  private final Collection<Class> classes = new ArrayList<>();

  public ReflectiveModelFactory() {
  }

  public ReflectiveModelFactory(Class... classes) {
    Collections.addAll(this.classes, classes);
  }
  
  public void addModelClass(Class modelClass) {
    this.classes.add(modelClass);
  }

  @Override
  public void createModelObjects(TestModels models) {
    for (Class aClass : classes) {
      try {
        models.add(aClass.newInstance());
      } catch (Exception e) {
        log.e("Failed to create a model class instance. Exiting.", e);
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }
}

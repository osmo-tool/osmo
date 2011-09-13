package osmo.tester.parser;

import osmo.common.log.Logger;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.EndState;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.model.FSM;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The main parser that takes the given model object and parses it for specific registered annotations,
 * passes these to specific {@link AnnotationParser} implementations to update the {@link FSM} representation
 * according to the information for the specific annotation.
 * 
 * @author Teemu Kanstren
 */
public class MainParser {
  private static Logger log = new Logger(MainParser.class);
  /** Key = Annotation type, Value = The parser object for that annotation. */
  private final Map<Class<? extends Annotation>, AnnotationParser> parsers = new HashMap<Class<? extends Annotation>, AnnotationParser>();

  public MainParser() {
    //we set up the parser objects for the different annotation types
    parsers.put(Transition.class, new TransitionParser());
    parsers.put(Guard.class, new GuardParser());
    parsers.put(AfterTest.class, new AfterTestParser());
    parsers.put(BeforeTest.class, new BeforeTestParser());
    parsers.put(AfterSuite.class, new AfterSuiteParser());
    parsers.put(BeforeSuite.class, new BeforeSuiteParser());
    parsers.put(TestSuiteField.class, new TestSuiteFieldParser());
    parsers.put(RequirementsField.class, new RequirementsFieldParser());
    parsers.put(Pre.class, new PreParser());
    parsers.put(Post.class, new PostParser());
    parsers.put(EndCondition.class, new EndConditionParser());
    parsers.put(EndState.class, new EndStateParser());
    parsers.put(Variable.class, new VariableParser());
  }

  /**
   * This delegates to the other constructor. Mainly used for testing.
   *
   * @param model Model object to be parsed.
   * @return The FSM for the given object.
   */
  public FSM parse(Object model) {
    Collection<Object> models = new ArrayList<Object>();
    models.add(model);
    return parse(models);
  }

  /**
   * Initiates parsing the given model object for the annotation that define the finite state machine (FSM) aspects
   * of the test model.
   *
   * @param modelObjects The set of test model objects to be parsed.
   * @return The FSM object created from the given model object that can be used for test generation.
   */
  public FSM parse(Collection<Object> modelObjects) {
    log.debug("parsing");
    FSM fsm = new FSM();
    String errors = "";
    for (Object obj : modelObjects) {
      //first we check any annotated fields that are relevant
      errors += parseFields(fsm, obj);
      //next we check any annotated methods that are relevant
      errors += parseMethods(fsm, obj);
    }
    //finally we check that the generated FSM itself is valid
    fsm.checkAndUpdateGenericItems(errors);
    return fsm;
  }

  /**
   * Parse the relevant annotated fields and pass these to correct {@link AnnotationParser} objects.
   *
   * @param fsm The test model object to be updated according to the parsed information.
   * @param obj The model object that contains the annotations and fields/executable methods for test generation.
   * @return A string listing all found errors.
   */
  private String parseFields(FSM fsm, Object obj) {
    //first we find all declared fields of any scope and type (private, protected, ...)
    Collection<Field> fields = getAllFields(obj.getClass());
    log.debug("fields "+fields.size());
    //next we create the parameter object and insert the common parameters
    ParserParameters parameters = new ParserParameters();
    parameters.setFsm(fsm);
    parameters.setModel(obj);
    String errors = "";
    //now we loop through all fields defined in the model object
    for (Field field : fields) {
      log.debug("field:"+field);
      //set the field to be accessible from the parser objects
      parameters.setField(field);
      Annotation[] annotations = field.getAnnotations();
      //loop through all defined annotations for each field
      for (Annotation annotation : annotations) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        log.debug("class:"+annotationClass);
        AnnotationParser parser = parsers.get(annotationClass);
        if (parser == null) {
          //unsupported annotation (e.g. for some completely different aspect)
          continue;
        }
        log.debug("parser:"+parser);
        //set the annotation itself as a parameter to the used parser object
        parameters.setAnnotation(annotation);
        //and finally parse it
        errors += parser.parse(parameters);
      }
    }
    return errors;
  }

  private Collection<Field> getAllFields(Class clazz) {
    Class<?> superclass = clazz.getSuperclass();
    Collection<Field> fields = new ArrayList<Field>();
    if (superclass != null) {
      fields.addAll(getAllFields(superclass));
    }
    Collections.addAll(fields, clazz.getDeclaredFields());
    return fields;
  }

  /**
   * Parse the relevant annotated methods and pass these to correct {@link AnnotationParser} objects.
   *
   * @param fsm The test model object to be updated according to the parsed information.
   * @param obj The model object that contains the annotations and fields/executable methods for test generation.
   * @return String representing any errors encountered.
   */
  private String parseMethods(FSM fsm, Object obj) {
    //first we get all methods defined in the test model object (also all scopes -> private, protected, ...)
    Collection<Method> methods = getAllMethods(obj.getClass());
    //there are always some methods inherited from java.lang.Object so we checking them here is pointless. FSM.check will do it
    log.debug("methods "+methods.size());
    //construct and store common parameters first for all method parsers, update the rest each time
    ParserParameters parameters = new ParserParameters();
    parameters.setFsm(fsm);
    parameters.setModel(obj);
    String errors = "";
    //loop through all the methods defined in the given object
    for (Method method : methods) {
      log.debug("method:"+method);
      parameters.setMethod(method);
      Annotation[] annotations = method.getAnnotations();
      //check all annotations for supported ones, use the given object to process them
      for (Annotation annotation : annotations) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        log.debug("class:"+annotationClass);
        AnnotationParser parser = parsers.get(annotationClass);
        if (parser == null) {
          //unsupported annotation (e.g. for some completely different aspect)
          continue;
        }
        log.debug("parser:"+parser);
        //set the annotation itself as a parameter to the used parser object
        parameters.setAnnotation(annotation);
        //and finally parse it
        errors += parser.parse(parameters);
      }
    }
    return errors;
  }

  private Collection<Method> getAllMethods(Class clazz) {
    Class<?> superclass = clazz.getSuperclass();
    Collection<Method> methods = new ArrayList<Method>();
    if (superclass != null) {
      methods.addAll(getAllMethods(superclass));
    }
    Collections.addAll(methods, clazz.getMethods());
    return methods;
  }
}

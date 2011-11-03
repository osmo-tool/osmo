package osmo.tester.parser;

import osmo.tester.model.FSM;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A holder for all the parameters to be passed to different {@link AnnotationParser} implementations.
 * Helps address easier software evolution and understanding by encapsulating the potentially large and
 * changing parameter set inside one object.
 * Also allows for optimizing parameter object creation by reusing the objects and setting only changed fields
 * (e.g. method, annotation).
 *
 * @author Teemu Kanstren
 */
public class ParserParameters {
  /** Describes the test model from test generation perspective. */
  private FSM fsm = null;
  /** The test model itself, providing executable methods for test generation. */
  private Object model = null;
  /** The annotation being currently processed. */
  private Object annotation = null;
  /** The annotated field, if any. When method are parsed, this is null, otherwise must be non-null. */
  private Field field = null;
  /** The annotated method, if any. When fields are parsed this is null, otherwise must be non-null. */
  private Method method = null;
  private String prefix = null;

  public FSM getFsm() {
    return fsm;
  }

  public void setFsm(FSM fsm) {
    this.fsm = fsm;
  }

  public Object getModel() {
    return model;
  }

  public void setModel(Object model) {
    this.model = model;
  }

  public Object getAnnotation() {
    return annotation;
  }

  public void setAnnotation(Object annotation) {
    this.annotation = annotation;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}

package osmo.tester.parser;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.suiteoptimizer.coverage.ScoreConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A holder for all the parameters to be passed to different {@link AnnotationParser} implementations.
 *
 * @author Teemu Kanstren
 */
public class ParserParameters {
  /** The test model itself, providing executable methods for test generation. */
  private Object model = null;
  /** The annotation being currently processed. */
  private Object annotation = null;
  /** All annotations for the represented field, including the one being processed. */
  private Collection<Object> fieldAnnotations = new ArrayList<>();
  /** The annotated field, if any. When method are parsed, this is null, otherwise must be non-null. */
  private Field field = null;
  /** The annotated method, if any. When fields are parsed this is null, otherwise must be non-null. */
  private Method method = null;
  /** The prefix of the model object, to be added to names of all parsed transitions, guards, etc. for that object. */
  private String prefix = null;
  /** Test suite object for the model. */
  private TestSuite suite = null;
  private ScoreConfiguration scoreConfig = null;

  public Object getModel() {
    return model;
  }

  public void setModel(Object model) {
    this.model = model;
  }

  public Object getAnnotation() {
    return annotation;
  }

  public Collection<Object> getFieldAnnotations() {
    return fieldAnnotations;
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

  public TestSuite getSuite() {
    return suite;
  }

  public void setSuite(TestSuite suite) {
    this.suite = suite;
  }

  public void setFieldAnnotations(Annotation[] annotations) {
    Collections.addAll(this.fieldAnnotations, annotations);
  }

  public void setScoreConfig(ScoreConfiguration scoreConfig) {
    this.scoreConfig = scoreConfig;
  }

  public ScoreConfiguration getScoreConfig() {
    return scoreConfig;
  }
}

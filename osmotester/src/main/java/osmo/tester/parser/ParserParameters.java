package osmo.tester.parser;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
  /** All OSMO annotations for the class of the model object. */
  private Map<String, String> classAnnotations = new LinkedHashMap<>();
  /** All OSMO annotations for the represented field, including the one being processed. */
  private Collection<Object> fieldAnnotations = new ArrayList<>();
  /** The annotated field, if any. When fields are parsed, this is non-null, otherwise null. */
  private Field field = null;
  /** The annotated method, if any. When methods are parsed this is non-null, otherwise must null. */
  private Method method = null;
  /** The prefix of the model object, to be added to names of all parsed test steps, guards, etc. for that object. */
  private String prefix = null;
  /** Test suite object for the model. */
  private TestSuite suite = null;
  /** Randomization seed. */
  private Long seed = null;
  /** Generator (and parser configuration). */
  private OSMOConfiguration config;

  public Object getModel() {
    return model;
  }

  public Class getModelClass() {
    return model.getClass();
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

  public Long getSeed() {
    return seed;
  }

  public void setSeed(Long seed) {
    this.seed = seed;
  }

  public void setFieldAnnotations(Annotation[] annotations) {
    fieldAnnotations.clear();
    Collections.addAll(this.fieldAnnotations, annotations);
  }
  
  public void addClassAnnotation(String name, String value) {
    classAnnotations.put(name, value);
  }
  
  public String getClassAnnotation(Class type) {
    String value = classAnnotations.get(type.getSimpleName());
    if (value == null) value = "";
    return value;
  }

  public void reset() {
    classAnnotations.clear();
    fieldAnnotations.clear();
  }

  public void setConfig(OSMOConfiguration config) {
    this.config = config;
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  public boolean isMethodBasedNaming() {
    return config.isMethodBasedNaming();
  }
}

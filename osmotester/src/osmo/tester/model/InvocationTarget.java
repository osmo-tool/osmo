package osmo.tester.model;

import osmo.common.OSMOException;
import osmo.common.log.Logger;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * A class used internally to represent a method to be invoked on a specific object.
 *
 * @author Teemu Kanstren
 */
public class InvocationTarget implements Comparable<InvocationTarget> {
  private static final Logger log = new Logger(InvocationTarget.class);
  /** The model object itself, implementing the actual methods to be invoked etc. */
  private final Object modelObject;
  /** The method to be invoked on the model object. */
  private final Method method;
  /** The annotation name for the invoked method. Used for error reporting. */
  private final String type;

  public InvocationTarget(ParserParameters parameters, Class type) {
    this.modelObject = parameters.getModel();
    this.method = parameters.getMethod();
    this.type = "@" + type.getName();
    log.debug("Found and created " + this.type + " method:" + method.getName());
  }

  /**
   * Call the configured method on the configured object. Expect no parameters to be required.
   * 
   * @return The return value from the invoked method.
   */
  public Object invoke() {
    try {
      return method.invoke(modelObject);
    } catch (Exception e) {
      throw new OSMOException("Failed to invoke " + type + " method on the model object.", e);
    }
  }

  /**
   * Call the configured method on the configured object. Expect a parameters to be required.
   *
   * @param arg The argument to the method to be invoked.
   * @return The return value from the invoked method.
   */
  public Object invoke(Object arg) {
    try {
      return method.invoke(modelObject, arg);
    } catch (Exception e) {
      throw new OSMOException("Failed to invoke " + type + " method on the model object.", e);
    }
  }

  public Object getModelObject() {
    return modelObject;
  }

  public Method getMethod() {
    return method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InvocationTarget that = (InvocationTarget) o;

    if (method != null ? !method.equals(that.method) : that.method != null) return false;
    if (modelObject != null ? !modelObject.equals(that.modelObject) : that.modelObject != null) return false;
    if (type != null ? !type.equals(that.type) : that.type != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = modelObject != null ? modelObject.hashCode() : 0;
    result = 31 * result + (method != null ? method.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }

  @Override
  public int compareTo(InvocationTarget o) {
    return method.getName().compareTo(o.method.getName());
  }
}

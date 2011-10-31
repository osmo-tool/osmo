package osmo.tester.model;

import osmo.common.log.Logger;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * A class used internally to represent a method to be invoked on a specific object.
 *
 * @author Teemu Kanstren
 */
public class InvocationTarget {
  private static Logger log = new Logger(InvocationTarget.class);
  /** The model object itself, implementing the actual transition methods etc. */
  private final Object modelObject;
  /** The method to be invoked on the model object. */
  private final Method method;
  /** The annotation name for the invoked method (from model annotations). Used for error reporting. */
  private final String type;

  public InvocationTarget(ParserParameters parameters, Class type) {
    this.modelObject = parameters.getModel();
    this.method = parameters.getMethod();
    this.type = "@" + type.getName();
    log.debug("Found and created " + this.type + " method:" + method.getName());
  }

  public Object invoke() {
    try {
      return method.invoke(modelObject);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke " + type + " method on the model object.", e);
    }
  }

  public Object invoke(Object arg) {
    //we need to check if it has parameters since it is optional for a pre- or post-method to have parameters
    if (method.getParameterTypes().length == 0) {
      return invoke();
    }
    try {
      return method.invoke(modelObject, arg);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke " + type + " method on the model object.", e);
    }
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
}

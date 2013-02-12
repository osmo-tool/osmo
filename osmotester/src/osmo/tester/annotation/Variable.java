package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that the annotated field should be considered as a model state variable.
 * <p/>
 * Primitive data types (int, float, double, boolean) are stored as such when
 * a step has been taken into the {@link osmo.tester.generator.testsuite.TestStep} object.
 * Any Objects are also stored as such unless they implement the {@link osmo.tester.model.VariableValue}
 * interface. If this interface is implemented the value() method on it is queried for the value to store.
 * <p/>
 * If {@link osmo.tester.model.dataflow.SearchableInput} objects are annotated with this, their latest value
 * will be stored. Otherwise, they will not be stored as part of the model state but only when they are used
 * to provide a new value.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Variable {
  /** The name of the variable. If not specified, uses the variable name from the class definition. */
  String value() default "";
}

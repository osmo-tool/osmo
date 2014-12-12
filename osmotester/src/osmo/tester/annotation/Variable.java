package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Value of annotated field is collected for test coverage calculations.
 * The generator keeps track of such values during generation, which can also be used in reporting.
 * <p>
 * Primitive data types (int, float, double, boolean) are stored as such when a step has been taken.
 * These are added as variable values to into the {@link osmo.tester.generator.testsuite.TestCaseStep} object.
 * 
 * Any Objects are also stored as such unless they implement the {@link osmo.tester.model.VariableValue}
 * interface. If this interface is implemented the value() method on it is queried for the value to store.
 * <p>
 * The data modelling objects extending {@link osmo.tester.model.data.SearchableInput} are special cases in that
 * the last value they provided is taken as the value to store.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Variable {
  /** @return The name of the variable. If not specified, uses the variable name from the class definition. */
  String value() default "";
}

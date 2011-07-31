package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that the annotated field should be considered as a model state variable.
 *
 * If the annotated variable is of type boolean, int, float, double, String, it is stored as such when
 * a step has been taken into the {@link osmo.tester.generator.testsuite.TestStep} object.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Variable {
}

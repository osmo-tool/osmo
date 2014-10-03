package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotated method returns true, test generation for the current test (not suite) is stopped.
 * <p>
 * Each annotated method must have a boolean return value and no parameters.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndCondition {
}

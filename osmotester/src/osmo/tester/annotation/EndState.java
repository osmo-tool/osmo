package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a method annotated with this is found in the model, test generation is only stopped once this
 * returns true (and any end conditions have been satisfied).
 *
 * Note that test cleanup etc. can in most cases be performed with the {@link AfterTest} annotation while more
 * strictly following the test generation algorithm.
 *
 * The annotated method must have no parameters and return a boolean value.
 * 
 * @see AfterTest
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndState {
}

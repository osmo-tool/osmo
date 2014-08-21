package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method is executed after each the specified test step(s).
 * <p>
 * The annotated method must have no parameters.
 *
 * If no name is given, association of "all" is assumed similar to guards. 
 * 
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Post {
  /** @return The set of test steps (or groups) this should be associated with. */
  String[] value() default Guard.DEFAULT;
}

package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method isexecuted after each the specified (or any) test step.
 * <p/>
 * The annotated method must have no parameters or one parameter of type Map<String, Object>.
 * If the parameter is there, OSMO Tester will provide a Map which is the same as the Map that
 * was provided to the any @Pre methods for that test step (allowing, e.g., passing values from @Pre
 * to @Post such as state before test step).
 * All @Pre and @Post executed for a test step thus share the same Map.
 *
 * If no name is given, association of "all" is assumed similar to guards. This can be useful, for example,
 * for logging purposes.
 * 
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Post {
  String[] value() default Guard.DEFAULT;
}

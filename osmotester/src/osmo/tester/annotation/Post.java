package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that the annotated method should be executed after each the specified
 * (or any) transition.
 *
 * The annotated method must have no parameters or one parameter of type Map<String, Object>.
 * If the parameter is there, OSMOTester will provide a Map which is the same as the Map that
 * was provided to the any @Pre methods for that transition (allowing, e.g., passing values from @Pre
 * to @Post such as state before transition).
 * All @Pre and @Post executed for a transition thus share the same Map.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Post {
  String[] value() default "all";
}

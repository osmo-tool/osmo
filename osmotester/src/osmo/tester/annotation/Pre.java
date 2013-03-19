package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that the annotated method should be executed before each the specified
 * (or any) transition.
 * <p/>
 * The annotated method must have no parameters or one parameter of type Map<String, Object>.
 * If the parameter is there, OSMOTester will create a Map that is passed to this method.
 * The same Map instance will then be provided to any @Post methods for the same transition.
 * All @Pre and @Post executed for a transition thus share the same Map.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Pre {
  String[] value() default "all";
}

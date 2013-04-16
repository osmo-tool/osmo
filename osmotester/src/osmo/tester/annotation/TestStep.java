package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the same as {@link Transition} but with a more intuitive name (for some).
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestStep {
  String value() default "";

  String name() default "";
  
  String group() default "";

  int weight() default 10;
  
  boolean strict() default true;
}

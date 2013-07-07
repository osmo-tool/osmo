package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the same as {@link Transition} but with a more academic name (a reminder of the early days).
 * 
 * @author Teemu Kanstren
 * @see Guard
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transition {
  String value() default "";

  String name() default "";

  String group() default "";

  int weight() default 10;

  boolean strict() default true;
}

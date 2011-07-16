package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that the annotated method is a guard defining when a transition is allowed to
 * be taken in test generation. The annotation takes a single parameter which is the name of the transition
 * to which it should be linked. One transition can have several guards associated with it, in which case
 * all these must return "true" for the transition to be considered as enabled and given to the algorithms
 * as an option for execution.
 *
 * The annotated method must have no parameters and return a boolean value.
 *
 * Example:
 * @Transition("bob")
 * public void hello() {
 *   System.out.println("Hello Bob");
 * }
 *
 * @Guard("bob")
 * public boolean allowBob() {
 *   return Math.random() > 0.6;
 * }
 *
 * The above example will allow the transition named "bob" (method hello()) to be taken in the random
 * function from Math.random() returns a value bigger than 0.6.
 *
 * Currently there is a special Guard annotation with the name "all". If such a guard annotation is found,
 * it will be executed before all the transition methods to check if they can be executed (along with their
 * specific guard methods).This will likely change when the special name is move to its own annotation
 * along with other similar annotations.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Guard {
  String[] value() default "all";
}

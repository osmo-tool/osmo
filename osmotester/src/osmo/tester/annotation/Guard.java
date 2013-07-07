package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method is a guard defining when a test step is allowed to
 * be taken in test generation. The annotation takes a single parameter which is the name of the test step
 * to which it should be linked. One test step can have several guards associated with it, in which case
 * all these must return "true" for the step to be considered as enabled and given to the algorithms
 * as an option for execution.
 * <p/>
 * The annotated method must have no parameters and return a boolean value.
 * <p/>
 * Example:
 *
 * @author Teemu Kanstren
 * @Transition("bob") public void hello() {
 * System.out.println("Hello Bob");
 * }
 * @Guard("bob") public boolean allowBob() {
 * return Math.random() > 0.6;
 * }
 * <p/>
 * The above example will allow the transition named "bob" (method hello()) to be taken in the random
 * function from Math.random() returns a value bigger than 0.6.
 * <p/>
 * Currently there is a special Guard annotation with the name "all". If such a guard annotation is found,
 * it will be executed before all the transition methods to check if they can be executed (along with their
 * specific guard methods). This is the same as not having any name at all.
 * A single guard can also be associated to several transitions/test steps by using an array as the identifier.
 * 
 * Finally, guards can be assigned to several test steps via groups. In that case, use the name of the group
 * as the target identifier for the guard condition.
 * 
 * And, of course, that is not all but you can also "negate" the expression. For example, associating a guard
 * to an identifier such as "!login" will associate it to all steps but the one named "login". This example
 * could be used to ensure that no other action is done until a valid login is performed.
 * 
 * In practice, groups and direct assignments with step names/name arrays generally provide better granularity control.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Guard {
  String[] value() default "all";
}

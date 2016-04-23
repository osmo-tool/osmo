package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method is a guard defining when a test step is allowed to be taken in test generation. 
 * The annotation takes as parameter the test step name(s) to which it should be linked. 
 * One test step can have several guards associated with it, in which case all these must return "true" 
 * for the step to be considered as enabled and given to the algorithms as an option for execution.
 * <p>
 * The annotated method must have no parameters and return a boolean value.
 * <p>
 * Example:
 *
 * {@literal @}TestStep("bob") public void hello() {
 * System.out.println("Hello Bob");
 * }
 * {@literal @}Guard("bob") public boolean allowBob() {
 * return Math.random() {@literal >} 0.6;
 * }
 * <p>
 * The above example will allow the test step named "bob" (method hello()) to be taken if the Math.random() call 
 * returns a value bigger than 0.6.
 * <p>
 * Currently there is a special Guard annotation with the name "all". This is the same as not having any name at all.
 * If such a guard annotation is found, it will be associated to all the test steps. 
 * A single guard can be associated to several test steps by using an array as the identifier.
 * 
 * Finally, guards can be assigned to several test steps via groups. In that case, use the name of the group
 * as the target identifier for the guard.
 * 
 * And, of course, that is not all but you can also "negate" the expression. For example, associating a guard
 * to an identifier such as "!login" will associate it to all steps but the one named "login". This example
 * could be used to ensure that no other action is done until a valid login is performed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Guard {
  public static final String DEFAULT = "osmo.tester.annotations.DEFAULT_VALUE";
  /** @return Set of steps (or groups) this guard should match (be attached to). */
  String[] value() default DEFAULT;
}

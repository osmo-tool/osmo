package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Several test steps can be grouped by assigning them to the same group with this annotation.
 * The groups can then be assigned guards that will apply to the whole group at once.
 * Guards are matched by name to groups and test steps, so a group may not have the same name as a test step.
 * 
 * Note that you can also group all methods in a class. If this is done, put this annotation on the class level
 * and it will apply to all test steps in the class. However, this only applies if the test step has no specific
 * group defined for it. If a test step has a specific group defined for it, this specific definition will override
 * the class level definition for that step.
 * 
 * This can be used, for example, to model different types of users in different classes, and associating specific
 * guards to the whole class to only allow them when that type of user is logged in.
 * 
 * @author Teemu Kanstren 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Group {
  /** The group name. Defaults to empty string, which should not be used. */
  String value() default "";
}

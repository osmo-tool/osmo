package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * A method annotated with this is considered to give a descriptive name for a state.
 * While state is practically a combination of all the variables in the model, this can be used to provide
 * more meaningful names for visualization, and also to group coverage values for a state if so desired.
 * You are expected to only have one of these.
 * 
 * @author Teemu Kanstren 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StateName {
}

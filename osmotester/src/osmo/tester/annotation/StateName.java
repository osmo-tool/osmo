package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Annotated method is expected to give a descriptive name for a state.
 * While state is practically a combination of all the variables in the model, this can be used to provide
 * more meaningful names for visualization, and also to group coverage values for a state if so desired.
 * You are expected to only have one of these in your overall model, or the generator will fail.
 * If a null value is returned by the method, it will not be taken into account as a state.
 * 
 * The annotated method must return a String value.
 * 
 * @author Teemu Kanstren 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StateName {
}

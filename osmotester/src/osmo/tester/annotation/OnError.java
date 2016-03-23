package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method is executed when an error/exception is thrown during test generation/execution.
 * <p>
 * The annotated method must have no parameters.
 *
 * If no name is given, association of "all" is assumed similar to guards. 
 * 
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnError {
}

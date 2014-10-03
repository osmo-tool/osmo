package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated will be called to enter the model into generation mode.
 * Only relevant when OSMO Explorer (a specific online algorithm) is used.
 *
 * Annotated Method must have no parameters.
 * 
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GenerationEnabler {
}

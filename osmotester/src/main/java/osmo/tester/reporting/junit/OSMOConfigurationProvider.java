package osmo.tester.reporting.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to identify methods that provide the {@link osmo.tester.OSMOConfiguration}
 * object for JUnit integration.
 * This should identify a method that returns an object of type {@link osmo.tester.OSMOConfiguration}.
 * The object should have the variable value "junitLength" defined for the integration to work.
 *
 * @author Teemu Kanstren
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OSMOConfigurationProvider {
}

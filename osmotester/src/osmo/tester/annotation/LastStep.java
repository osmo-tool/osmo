package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method is executed as last step in test case, before ending the test case but after having defined
 * to end it. Difference to {@link AfterTest} is that this is executed as part of the test case, thus potentially 
 * failing the test case and not the overall generation process.
 * <p>
 * The annotated method must have no parameters.
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface  LastStep {
}

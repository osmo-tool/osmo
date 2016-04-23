package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Annotated method is expected to give a value to collect for coverage.
 * Called after each test step is executed, providing the step as a reference.
 * Null values are ignored.
 *
 * The annotated method must return a String value.
 * The annotated method must take as a parameter a {@link osmo.tester.generator.testsuite.TestCaseStep} object.
 * 
 * @author Teemu Kanstren 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CoverageValue {
  /** 
   * The name of the variable. Must be specified.
   *  
   * @return The name of the coverage variable.
   */
  String value() default "";
}

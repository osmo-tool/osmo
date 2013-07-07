package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation describes a field in the model object for providing access to the test suite being generated.
 * Annotated fields will have their value set by the generator before starting test generation to contain the valid
 * reference to the {@link osmo.tester.generator.testsuite.TestSuite} object with access to real-time generation data.
 * <p/>
 * If the annotated field is not of type {@link osmo.tester.generator.testsuite.TestSuite}, the generation fails.
 * The initial field value must be null of the generation fails.
 * That is, the field should have a null value when the generation starts.
 * If the value is changed later by the user code, the new reference is not used or updated by the generator.
 *
 * @author Teemu Kanstren
 * @see osmo.tester.generator.testsuite.TestSuite
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestSuiteField {
}

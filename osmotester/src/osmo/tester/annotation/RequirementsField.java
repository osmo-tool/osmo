package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation describes the requirements field in the model object passed to the OSMOTester.generate() method.
 * This field must be of type {@link osmo.tester.model.Requirements}.
 * If a correct field is found and has this annotation, the tester will
 * pick up the referenced object and use that to provide a list of expected requirements that the test generator
 * should aim to cover. If the field is null or of wrong type, an exception will be thrown and test generation
 * will not commence. For more information on defining the requirements, see the Requirements class.
 *
 * @author Teemu Kanstren
 * @see osmo.tester.model.Requirements
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequirementsField {
}

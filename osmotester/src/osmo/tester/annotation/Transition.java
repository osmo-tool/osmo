package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines a transition to be taken by the generator when using the test model to generate tests.
 * The transition in practice describes a test step to be taken by a generator. Possible test steps at a given
 * state are identified as transitions that have all their associated guard methods return value "true".
 * The choice of which transition is taken from the set of enabled transitions depends on the algorithm chosen
 * for test generation. This algorithm is passed the set of enabled transitions and test generation history,
 * and is expected to choose one of the given transitions for execution.
 *
 * The methods annotated with this annotation are expected to make the associated test step happen by either
 * invoking a method or service on the system under test itself (in online mode),
 * or by creating a suitable test script element in the format of the tool used to run the tests themselves
 * (in offline mode).
 *
 * @see Guard
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transition {
  String value() default "";
  String name() default "";
  int weight() default 1;
}

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
 * The annotation has three parameters: "value", "name", and "weight". Of these "value" and "name" refer to the
 * same property, the name of the transition. This is what is used to map the transition to the associated guard
 * statements that must have the same name. The two different attributes exist since the default attribute name
 * must be "value" to allow for notation @Transition("myname"). On the other hand, if we want to use more than one
 * attribute (weight in this case) we must provide the name of the attribute. For this reason the "name" is there
 * as well which allows to define weighted transitions using the notation @Transition(name="myname", weight=2)
 * instead of @Transition(value="myname", weight=2) although the latter works as well. A name of "" (empty string)
 * is ignored. If both "value" and "name" are defined, "name" dominates. If neither is defined, an error is given.
 *
 * The weight attribute allows one to give weights to transitions that define how often that transitions should be
 * taken when several are available. Weighted algorithms ({@link osmo.tester.generator.algorithm.WeightedRandomAlgorithm})
 * take available transitions based on their weight. The more weight the transition has, the more often it is taken.
 * If transitions "A" is given weight 1 and transitions B "2", then B is taken twice as often as A. A transition
 * without a weight defined has a default weight of 1.
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

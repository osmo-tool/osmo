package osmo.tester.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines a test step to be taken by the generator.
 * Possible test steps to execute are identified by executing all guards.
 * The steps for which all guards return true are considered enabled.
 * <p/>
 * Methods annotated with this annotation are expected to execute a set of actions on the system under test itself 
 * (in online mode), 
 * or by creating a suitable test script element in the format of the test execution tool (in offline mode).
 * <p/>
 * This annotation has several parameters: "value", "name", "weight", "group", and "strict". 
 * Of these "value" and "name" refer to the same property, the name of the test step. 
 * This is what is used to map the test step to the associated guards.
 * The second alternative to mapping steps to guards is through the {@link Group} annotation.
 * If both "value" and "name" are defined, "name" dominates. If neither is defined, an error is given.
 * <p/>
 * The weight attribute allows one to give weights to test steps. 
 * Weighted algorithms (such as {@link osmo.tester.generator.algorithm.WeightedBalancingAlgorithm}) aim to take
 * steps with higher weights more often. 
 * For example, if step "A" is given weight 1 and step B "2", then B is (over time) taken twice as often as A 
 * (assuming use of a suitable algorithm). If no weight is explicitly defined, a default weight of 10 is used.
 * <p/>
 * The "strict" attribute is a way of saying that if this step fails, test generation should always be stopped.
 * For example, asynchronous updates of non-critical information might not break the rest of the test case even
 * if they are not immediately visible. In such a case once the asynchronous updates go through, the rest of the
 * steps will still function as expected as the model state and SUT state become synchronized shortly after.
 * On the other hand, failing to log in might make it pointless to continue (the overall state is messed up and
 * future steps will never work properly).
 *
 * @author Teemu Kanstren
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestStep {
  String value() default "";

  String name() default "";
  
  String group() default "";

  int weight() default 10;
}

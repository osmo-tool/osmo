package osmo.tester.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Describes a transition in the model object FSM representation.
 * In practice this can be mapped to a method executing a specific test step for test generation when possible.
 * These are identified in the model object from the {@link osmo.tester.annotation.Transition} annotations.
 * This includes the method to execute the test step (and generate scripts, etc.) and also
 * the {@link osmo.tester.annotation.Guard} methods that define when the transition is allowed to be performed.
 * 
 * @author Teemu Kanstren
 */
public class FSMTransition {
  /** Name of the transition, from @Transition("name") or (name="name") or (value="name"). Fails if undefined or empty ("").*/
  private final String name;
  /** Weight of the transitions, from @Transition(weight=x), defaults to 1. */
  private int weight = 1;
  /** The set of guards defining when this transition can be taken. */
  private final Collection<InvocationTarget> guards = new ArrayList<InvocationTarget>();
  /** The method that needs to be invoked when the transition should be actually taken. */
  private InvocationTarget transition = null;
  /** The set of oracles to be evaluated after this transition has been taken. */
  private final Collection<InvocationTarget> oracles = new ArrayList<InvocationTarget>();

  public FSMTransition(String name) {
    this.name = name;
  }

  public void addGuard(InvocationTarget target) {
    guards.add(target);
  }

  public void addOracle(InvocationTarget target) {
    oracles.add(target);
  }

  public String getName() {
    return name;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    //weight is -1 for oracles and guards, which should not impact this as weight is only defined for a transition
    //but guards and oracles are also associated with transitions and can sometimes create them
    if (weight <= 0) {
      return;
    }
    this.weight = weight;
  }

  public Collection<InvocationTarget> getGuards() {
    return guards;
  }

  public InvocationTarget getTransition() {
    return transition;
  }

  public void setTransition(InvocationTarget transition) {
    this.transition = transition;
  }

  public Collection<InvocationTarget> getOracles() {
    return oracles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FSMTransition that = (FSMTransition) o;

    if (guards != null ? !guards.equals(that.guards) : that.guards != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (oracles != null ? !oracles.equals(that.oracles) : that.oracles != null) return false;
    if (transition != null ? !transition.equals(that.transition) : that.transition != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (guards != null ? guards.hashCode() : 0);
    result = 31 * result + (transition != null ? transition.hashCode() : 0);
    result = 31 * result + (oracles != null ? oracles.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FSMTransition{" +
            "name='" + name + '\'' +
            ", weight=" + weight +
            '}';
  }
}

package osmo.tester.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a transition in the model object FSM representation.
 * In practice this can be mapped to a method executing a specific test step for test generation when possible.
 * These are identified in the model object from the {@link osmo.tester.annotation.Transition} annotations.
 * This includes the method to execute the test step (and generate scripts, etc.) and also
 * the {@link osmo.tester.annotation.Guard} methods that define when the transition is allowed to be performed and
 * the {@link osmo.tester.annotation.Post} methods that perform checks after the transitions.
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
  /** The set of pre-methods to be evaluated before this transition has been taken. */
  private final Collection<InvocationTarget> pres = new ArrayList<InvocationTarget>();
  /** The set of post-methods to be evaluated after this transition has been taken. */
  private final Collection<InvocationTarget> posts = new ArrayList<InvocationTarget>();
  /** This is to allow @Pre and @Post to store and share properties over this transition. */
  private Map<String, Object> prePostParameter = new HashMap<String, Object>();

  public FSMTransition(String name) {
    this.name = name;
  }

  public Map<String, Object> getPrePostParameter() {
    return prePostParameter;
  }

  public void addGuard(InvocationTarget target) {
    guards.add(target);
  }

  public void addPre(InvocationTarget target) {
    pres.add(target);
  }

  public void addPost(InvocationTarget target) {
    posts.add(target);
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

  public Collection<InvocationTarget> getPreMethods() {
    return pres;
  }

  public Collection<InvocationTarget> getPostMethods() {
    return posts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FSMTransition that = (FSMTransition) o;

    if (weight != that.weight) return false;
    if (guards != null ? !guards.equals(that.guards) : that.guards != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (posts != null ? !posts.equals(that.posts) : that.posts != null) return false;
    if (pres != null ? !pres.equals(that.pres) : that.pres != null) return false;
    if (transition != null ? !transition.equals(that.transition) : that.transition != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + weight;
    result = 31 * result + (guards != null ? guards.hashCode() : 0);
    result = 31 * result + (transition != null ? transition.hashCode() : 0);
    result = 31 * result + (pres != null ? pres.hashCode() : 0);
    result = 31 * result + (posts != null ? posts.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FSMTransition{" +
            "name='" + name + '\'' +
            ", weight=" + weight +
            '}';
  }

  /**
   * Clears the parameters between @Pre and @Post methods so old ones do not mess with new ones.
   */
  public void reset() {
    prePostParameter.clear();
  }
}

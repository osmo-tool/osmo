package osmo.tester.model;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
public class FSMTransition implements Comparable<FSMTransition> {
  private static Logger log = new Logger(FSMTransition.class);
  /** Name of the transition, from @Transition("name") or (name="name") or (value="name"). Fails if undefined or empty (""). */
  private final TransitionName name;
  /** Weight of the transitions, from @Transition(weight=x), defaults to 10 (see {@link osmo.tester.annotation.Transition}. */
  private int weight = 10; //NOTE: this value here is pointless in practice, the true default is in the annotation class
  /** The set of guards defining when this transition can be taken. */
  private final List<InvocationTarget> guards = new ArrayList<>();
  /** The method that needs to be invoked when the transition should be actually taken. */
  private InvocationTarget transition = null;
  /** The set of pre-methods to be evaluated before this transition has been taken. */
  private final List<InvocationTarget> pres = new ArrayList<>();
  /** The set of post-methods to be evaluated after this transition has been taken. */
  private final List<InvocationTarget> posts = new ArrayList<>();
  /** The group this transition belongs to, null or "" are considered as no group. */
  private TransitionName groupName;
  /** A strict one stops the generator, throwing any exceptions. Non-strict will log the error but continue. */
  private boolean strict = true; //again, the true default value is in the annotation
  /** This index is used by explorer algorithm when picking one of several equals using a fallback algorithm. */
  private int explorerIndex = -1;

  public FSMTransition(String name) {
    this.name = new TransitionName("", name);
  }

  public FSMTransition(TransitionName name) {
    this.name = name;
  }

  public int getExplorerIndex() {
    return explorerIndex;
  }

  public void setExplorerIndex(int explorerIndex) {
    this.explorerIndex = explorerIndex;
  }

  /** Sort all the elements to get more deterministic test generation. */
  public void sort() {
    Collections.sort(pres);
    Collections.sort(posts);
    Collections.sort(guards);
  }

  public void setGroupName(TransitionName groupName) {
    this.groupName = groupName;
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

  public TransitionName getName() {
    return name;
  }
  
  public TransitionName getGroupName() {
    return groupName;
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

  public boolean isStrict() {
    return strict;
  }

  public void setStrict(boolean strict) {
    this.strict = strict;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FSMTransition that = (FSMTransition) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "FSMTransition{" +
            "name='" + name + '\'' +
            ", weight=" + weight +
            '}';
  }

  public String getStringName() {
    return name.toString();
  }

  @Override
  public int compareTo(FSMTransition o) {
    return name.toString().compareTo(o.name.toString());
  }
  
  public String getModelObjectName() {
    return createModelObjectName(name.getPrefix(), transition.getModelObject().getClass());
  }
  
  public static String createModelObjectName(String prefix, Class modelClass) {
    String className = modelClass.getName();
    if (prefix != null && prefix.length() > 0) {
      return prefix +"-"+ className;
    }
    return className;
  }
}

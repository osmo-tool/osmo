package osmo.tester.model;

import osmo.common.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Describes a test step in the test model.
 * In practice this can be mapped to a method executing a specific test step for test generation,
 * including its guards, pre- and post-methods, and other associated information.
 *
 * @author Teemu Kanstren
 */
public class FSMTransition implements Comparable<FSMTransition> {
  private static final Logger log = new Logger(FSMTransition.class);
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

  /**
   * This is only used in testing.
   * 
   * @param name Name to give, forget the prefix..
   */
  public FSMTransition(String name) {
    this.name = new TransitionName("", name);
  }

  public FSMTransition(TransitionName name) {
    this.name = name;
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

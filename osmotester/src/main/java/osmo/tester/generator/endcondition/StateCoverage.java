package osmo.tester.generator.endcondition;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/** 
 * An end condition that requires the given set of {@link osmo.tester.annotation.CoverageValue} values to be covered.
 * Multiples will be merged, meaning you can only request coverage of state, not multiple coverages of it.
 * 
 * @author Teemu Kanstren 
 */
public class StateCoverage implements EndCondition {
  /** Name of variable to cover. */
  private final String name;
  /** List of required values to covered. */
  private Collection<String> required = new LinkedHashSet<>();

  public StateCoverage(String name, String... states) {
    this.name = name;
    Collections.addAll(this.required, states);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    Collection<String> covered = suite.getCoverage().getStates().get(name);
    if (covered == null) covered = new ArrayList<>();
    Collection<String> clone = new ArrayList<>();
    clone.addAll(required);
    clone.removeAll(covered);
    //if all were covered we are done
    return clone.size() == 0;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    Collection<String> covered = suite.getCurrentTest().getCoverage().getStates().get(name);
    if (covered == null) covered = new ArrayList<>();
    Collection<String> clone = new ArrayList<>();
    clone.addAll(required);
    clone.removeAll(covered);
    //if all were covered we are done
    return clone.size() == 0;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public EndCondition cloneMe() {
    //we have no state to initialize so can just return self
    return this;
  }
}

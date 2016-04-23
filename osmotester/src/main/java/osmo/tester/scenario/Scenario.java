package osmo.tester.scenario;

import osmo.common.OSMOException;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.structure.StepCoverage;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Represents a scenario for test generation.
 * Can define a start sequence and a slicing of the model.
 * If a start sequence is defined, that is always executed first in exactly that order for each test.
 * If a slice is defined, it can define a minimum and maximum number of times for specific test steps to be taken.
 * The possible startup sequence is not counter in the slice.
 * It is possible to make the startup sequence "strict" which means nothing but the steps defined in the slices are
 * ever executed (after startup). In non-strict mode, any unsliced steps can be executed as many times as desired.
 * Notice that the steps taken have to be allowed by the model. Thus if some guards forbid a sequence given as a
 * startup sequence, the generator will fail and given an e stating the transition is not available.
 * 
 * NOTE: another option to implement filtering would be to use guards that the scenario would attach to all steps.
 * However, the filtering seems to work well enough for now for generation and for exploration it would be difficult
 * to track everything across all the paths so there it is done specifically in PathExplorer.
 *
 * @author Teemu Kanstren
 */
public class Scenario {
  /** The list of step names for startup sequence for each test case. */
  private List<String> startup = new ArrayList<>();
  /** The slices for the different steps. */
  private List<Slice> slices = new ArrayList<>();
  /** The forbidden steps in this scenario. */
  private Collection<String> forbidden = new LinkedHashSet<>();
  /** Are non-sliced steps allowed? */
  private final boolean strict;
  /** Any errors in parsing the defined scenario. */
  private String errors = "";

  /** @param strict Sets the strict mode flag. */
  public Scenario(boolean strict) {
    this.strict = strict;
  }

  /**
   * Adds the set of given steps to the startup sequence, after any existing steps in it.
   *
   * @param steps To add.
   */
  public void addStartup(String... steps) {
    startup.addAll(Arrays.asList(steps));
  }
  
  public void forbid(String step) {
    forbidden.add(step);
  }

  /**
   * Adds a slicing definition for a test step.
   *
   * @param stepName Name of step to slice.
   * @param min      Minimum number to have in test (after startup).
   * @param max      Maximum number to have in test (after startup).
   */
  public void addSlice(String stepName, int min, int max) {
    slices.add(new Slice(stepName, min, max));
  }

  /**
   * Get the startup sequence.
   *
   * @return The startup sequence.
   */
  public List<String> getStartup() {
    return startup;
  }

  /**
   * Get the defined slices.
   *
   * @return Slices as defined.
   */
  public List<Slice> getSlices() {
    return slices;
  }

  /**
   * Validates the scenario with regards to the parsed test model.
   *
   * @param fsm The test model.
   */
  public void validate(FSM fsm) {
    errors = "";
    Collection<String> names = new LinkedHashSet<>();
    //get all possible names (prefix+name)
    for (FSMTransition t : fsm.getTransitions()) {
      names.add(t.getStringName());
    }

    Collection<String> toClear = new LinkedHashSet<>();
    toClear.addAll(startup);
    toClear.removeAll(names);
    //what remains is the parts of startup script that do not exist
    if (!toClear.isEmpty()) error("Scenario startup script contains steps not found in model:" + toClear);
    toClear.clear();

    List<String> allSliced = new ArrayList<>();
    for (Slice slice : slices) {
      String name = slice.getStepName();
      toClear.add(name);
      if (allSliced.contains(name)) error("Duplicate slices not allowed:" + name);
      allSliced.add(name);
      int min = slice.getMin();
      int max = slice.getMax();
      //zero means it is undefined
      if (min == 0 || max == 0) continue;
      if (min > max) error("Minimum must be lower or equal to maximum in slice:" + name);
    }
    toClear.removeAll(names);
    //what remains are steps in slices that do not exist
    if (!toClear.isEmpty()) error("Scenario slices contains steps not found in model:" + toClear);

    toClear.clear();
    toClear.addAll(forbidden);
    toClear.retainAll(allSliced);
    if (!toClear.isEmpty()) {
      error("Cannot forbid items in slice:"+toClear);
    }
    
    if (strict) {
      List<String> temp = new ArrayList<>();
      temp.addAll(names);
      temp.removeAll(allSliced);
      forbidden.addAll(temp);
    }
    
    if (errors.length() > 0) {
      throw new OSMOException(errors);
    }
  }

  /**
   * Build a single e message with all errors at once, do not report one at a time until fixed.
   *
   * @param msg To add to e string.
   */
  private void error(String msg) {
    if (errors.length() > 0) errors += "\n";
    errors += msg;
  }

  /** @return Whether we are in strict mode. */
  public boolean isStrict() {
    return strict;
  }

  /**
   * Creates an end condition to address minimum slicing requirements.
   *
   * @param testCaseEndCondition The original end condition given by user.
   * @return Possibly modified end condition if minimum slices found.
   */
  public EndCondition createEndCondition(EndCondition testCaseEndCondition) {
    if (slices.isEmpty()) return testCaseEndCondition;

    //first we count how many times different steps exist in the possible startup script
    //this is required to create an end condition where only parts after the startup count for the minimum slice
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (String name : startup) {
      Integer count = counts.get(name);
      if (count == null) {
        count = 0;
      }
      count++;
      counts.put(name, count);
    }

    //define the end condition requirements
    Collection<String> reqs = new ArrayList<>();
    for (Slice slice : slices) {
      //first we have to add everything in the startup sequence or else the slice is not "after" it
      String name = slice.getStepName();
      Integer count = counts.get(name);
      if (count != null) {
        for (int ci = 0 ; ci < count ; ci++) {
          reqs.add(name);
        }
      }
      //then add the count of minimum, together they form the startup+slice requirement for this step
      for (int i = 0 ; i < slice.getMin() ; i++) {
        reqs.add(name);
      }
    }
    //if no requirement exists, there is no need to modify the original end condition
    if (reqs.isEmpty()) return testCaseEndCondition;
    StepCoverage coverage = new StepCoverage(reqs.toArray(new String[reqs.size()]));
    //when exploring, this is the case..
    if (testCaseEndCondition == null) return coverage;
    //we combine the original with the new using the And operator
    return new And(coverage, testCaseEndCondition);
  }

  public Collection<String> getForbidden() {
    return forbidden;
  }
}

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
import java.util.LinkedHashSet;
import java.util.List;

/** @author Teemu Kanstren */
public class Scenario {
  private List<String> startup = new ArrayList<>();
  private List<Slice> slices = new ArrayList<>();
  private final boolean strict;
  private String errors = "";

  public Scenario(boolean strict) {
    this.strict = strict;
  }

  public void addStartup(String... steps) {
    startup.addAll(Arrays.asList(steps));
  }
  
  public void addSlice(String stepName, int min, int max) {
    slices.add(new Slice(stepName, min, max));
  }

  public List<String> getStartup() {
    return startup;
  }

  public List<Slice> getSlices() {
    return slices;
  }
  
  public void validate(FSM fsm) {
    errors = "";
    Collection<String> names = new LinkedHashSet<>();
    for (FSMTransition t : fsm.getTransitions()) {
      names.add(t.getStringName());
    }

    Collection<String> toClear = new LinkedHashSet<>();
    toClear.addAll(startup);
    toClear.removeAll(names);
    if (!toClear.isEmpty()) error("Scenario startup script contains steps not found in model:"+toClear);
    toClear.clear();
    
    List<String> all = new ArrayList<>();
    for (Slice slice : slices) {
      String name = slice.getStepName();
      toClear.add(name);
      if (all.contains(name)) error("Duplicate slices not allowed:" + name);
      all.add(name);
    }
    toClear.removeAll(names);
    if (!toClear.isEmpty()) error("Scenario slices contains steps not found in model:" + toClear);
    if (errors.length() > 0) {
      throw new OSMOException(errors);
    }
  }
  
  private void error(String msg) {
    if (errors.length() > 0) errors += "\n";
    errors += msg;
  }

  public boolean isStrict() {
    return strict;
  }

  public EndCondition createEndCondition(EndCondition testCaseEndCondition) {
    if (slices.isEmpty()) return testCaseEndCondition;
    Collection<String> reqs = new ArrayList<>();
    for (Slice slice : slices) {
      for (int i = 0 ; i < slice.getMin() ; i++) {
        reqs.add(slice.getStepName());
      }
    }
    if (reqs.isEmpty()) return testCaseEndCondition;
    StepCoverage coverage = new StepCoverage(reqs.toArray(new String[reqs.size()])); 
    return new And(coverage, testCaseEndCondition);
  }
}

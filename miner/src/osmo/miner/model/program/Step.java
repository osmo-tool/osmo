package osmo.miner.model.program;

import osmo.miner.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Step {
  private static final Logger log = new Logger(Step.class);
  private final Program parent;
  private final String name;
  private Map<String, String> variables = new HashMap<String, String>();
  private Map<String, Step> subSteps = new HashMap<String, Step>();

  public Step(Program parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  public Program getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public void addVariable(String name, String value) {
    if (!variables.containsKey(name)) {
      variables.put(name, value);
    }
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public void merge(Program with) {
    Map<String, String> map = with.getVariables();
    for (String name : map.keySet()) {
      addVariable(name, map.get(name));
    }

    List<Step> programSteps = with.getSteps();
    for (Step programStep : programSteps) {
      String name = programStep.getName();
      Step step = subSteps.get(name);
      if (step == null) {
        step = new Step(parent, name);
        subSteps.put(name, step);
      }
      for (String vName : programStep.variables.keySet()) {
        step.addVariable(vName, programStep.variables.get(vName));
      }
    }
  }

  public List<Step> getSubSteps() {
    List<Step> steps = new ArrayList<Step>();
    steps.addAll(subSteps.values());
    return steps;
  }

  public Step deepCopy(Program parent) {
    Step copy = new Step(parent, name);
    for (String name : variables.keySet()) {
      copy.addVariable(name, variables.get(name));
    }
    return copy;
  }
}

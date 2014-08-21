package osmo.tester.parser;

import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.Requirements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Represents the results of parsing the given model object(s).
 *
 * @author Teemu Kanstren
 */
public class ParserResult {
  /** The basic structure of the model. */
  private final FSM fsm;
  /** Requirements object for the model. */
  private Requirements requirements = null;
  /** Descriptions for model elements. Name = model element name, value = its description. */
  private Map<String, String> descriptions = new HashMap<>();

  public ParserResult(FSM fsm) {
    this.fsm = fsm;
  }

  public FSM getFsm() {
    return fsm;
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }

  public void addDescription(InvocationTarget target, String description) {
    descriptions.put(idFor(target), description);
  }
  
  private String idFor(InvocationTarget target) {
    String methodName = target.getMethod().getName();
    int hash = System.identityHashCode(target.getModelObject());
    return hash + methodName;
  }
    
  //TODO: add tests for description parsing. 
  //TODO: add tests for errors also
  /**
   * Processes the results by associating descriptions to invocation targets.
   */
  public void postProcess() {
    Collection<InvocationTarget> targets = new HashSet<>();
    targets.addAll(fsm.getBeforeTests());
    targets.addAll(fsm.getBeforeSuites());
    Collection<FSMTransition> steps = fsm.getTransitions();
    for (FSMTransition step : steps) {
      targets.addAll(step.getGuards());
      targets.addAll(step.getPreMethods());
      targets.addAll(step.getPostMethods());
    }
    targets.addAll(fsm.getAfterTests());
    targets.addAll(fsm.getAfterSuites());
    targets.addAll(fsm.getEndConditions());
    targets.addAll(fsm.getExplorationEnablers());
    targets.addAll(fsm.getGenerationEnablers());
    for (InvocationTarget target : targets) {
      String desc = descriptions.get(idFor(target));
      if (desc != null) {
        target.setDescription(desc);
      }
    }
  }
}

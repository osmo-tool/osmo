package osmo.tester.reporting.model;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.CoverageMethod;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.VariableField;

import java.lang.reflect.Method;
import java.util.Collection;

/** 
 * Provides means to print the structure of the test model built from the given model objects.
 * This includes all the model elements, and the test steps having all the guards, pre-, and post- methods
 * associated with them.
 * 
 * @author Teemu Kanstren */
public class ModelPrinter {
  /** Configuration for test generation, used for parsing model for visualization so most attributes are ignored. */
  private OSMOConfiguration config = new OSMOConfiguration();

  /**
   * @param factory For creating model objects.
   * @see osmo.tester.OSMOTester
   */
  public void setModelFactory(ModelFactory factory) {
    config.setFactory(factory);
  }

  public String write() {
    StringBuilder report = new StringBuilder();
    MainGenerator generator = new MainGenerator(0, new TestSuite(), config);
    FSM fsm = generator.getFsm();

    report.append("BeforeSuites: ").append(targetsToString(fsm.getBeforeSuites()));
    report.append("AfterSuites: ").append(targetsToString(fsm.getAfterSuites()));
    report.append("BeforeTests: ").append(targetsToString(fsm.getBeforeTests()));
    report.append("AfterTests: ").append(targetsToString(fsm.getAfterTests()));
    report.append("Last Steps: ").append(targetsToString(fsm.getLastSteps()));
    report.append("Model EndConditions: ").append(targetsToString(fsm.getEndConditions()));
    report.append("On Errors: ").append(targetsToString(fsm.getOnErrors()));
    report.append("Exploration Enablers: ").append(targetsToString(fsm.getExplorationEnablers()));
    report.append("Generation Enablers: ").append(targetsToString(fsm.getGenerationEnablers()));

    report.append("Coverage Value Methods: ");
    Collection<CoverageMethod> coverageMethods = fsm.getCoverageMethods();
    for (CoverageMethod cm : coverageMethods) {
      report.append(cm.getVariableName());
      Method method = cm.getInvocationTarget().getMethod();
      String[] split = method.toGenericString().split(" ");
      String target = split[2];
      report.append("[").append(target).append("], ");
    }
    report.append("\n");

    Requirements requirements = fsm.getRequirements();
    report.append("Requirements: ").append(requirements);
    if (requirements != null) {
      report.append(" (").append(requirements.getRequirements()).append(")");
    }
    report.append("\n");

    Collection<VariableField> variables = fsm.getModelVariables();
    report.append("Variables: ");
    for (VariableField variable : variables) {
      report.append(variable.getName()).append(", ");
    }
    report.append("\n");

    report.append("\n");

    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition t : transitions) {
      report.append("STEP: ").append(t.getStringName()).append(", WEIGHT=").append(t.getWeight()).append("\n");
      report.append("GUARDS: ").append(targetsToString(t.getGuards()));
      report.append("GROUP: ").append(t.getGroupName()).append("\n");
      report.append("POST: ").append(targetsToString(t.getPostMethods()));
      report.append("PRE: ").append(targetsToString(t.getPreMethods()));
      report.append("\n");
    }
    return report.toString();
  }

  /**
   * Build a string list of the given target methods.
   * 
   * @param targets The methods to build a list for.
   * @return String list with ", " in the end.
   */
  private String targetsToString(Collection<InvocationTarget> targets) {
    StringBuilder result = new StringBuilder();
    for (InvocationTarget target : targets) {
      Method method = target.getMethod();
      String[] split = method.toGenericString().split(" ");
      String name = split[2];
      result.append(name).append(", ");
    }
    result.append("\n");
    return result.toString();
  }
}

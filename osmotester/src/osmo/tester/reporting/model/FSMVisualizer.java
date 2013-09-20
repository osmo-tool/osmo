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
public class FSMVisualizer {
  /** Configuration for test generation, used for parsing model for visualization so most attributes are ignored. */
  private OSMOConfiguration config = new OSMOConfiguration();

  /**
   * @see osmo.tester.OSMOTester
   */
  public void addModelObject(Object modelObject) {
    config.addModelObject(modelObject);
  }

  /**
   * @see osmo.tester.OSMOTester
   */
  public void setModelFactory(ModelFactory factory) {
    config.setFactory(factory);
  }

  /**
   * @see osmo.tester.OSMOTester
   */
  public void addModelObject(String prefix, Object modelObject) {
    config.addModelObject(prefix, modelObject);
  }

  //step+guards+pre+post+group+weight
  //state names
  //before suite, after suite, before test, after test
  //coverage variables
  //end conditions
  //exploration enablers
  //generation enablers
  //requirements
  public String write() {
    String report = "";
    MainGenerator generator = new MainGenerator(0, new TestSuite(), config);
    FSM fsm = generator.getFsm();

    report += "BeforeSuites: "+targetsToString(fsm.getBeforeSuites());
    report += "AfterSuites: "+targetsToString(fsm.getAfterSuites());
    report += "BeforeTests: "+targetsToString(fsm.getBeforeTests());
    report += "AfterTests: "+targetsToString(fsm.getAfterTests());
    report += "Model EndConditions: "+targetsToString(fsm.getEndConditions());
    report += "Exploration Enablers: "+targetsToString(fsm.getExplorationEnablers());
    report += "Generation Enablers: "+targetsToString(fsm.getGenerationEnablers());

    report += "Coverage Value Methods: ";
    Collection<CoverageMethod> coverageMethods = fsm.getCoverageMethods();
    for (CoverageMethod cm : coverageMethods) {
      report += cm.getVariableName();
      Method method = cm.getInvocationTarget().getMethod();
      String[] split = method.toGenericString().split(" ");
      String target = split[2];
      report+="["+target+"], ";
    }
    report += "\n";

    Requirements requirements = fsm.getRequirements();
    report += "Requirements: "+ requirements;
    if (requirements != null) {
      report += " ("+requirements.getRequirements()+")";
    }
    report += "\n";

    Collection<VariableField> variables = fsm.getStateVariables();
    report += "Variables: ";
    for (VariableField variable : variables) {
      report += variable.getName()+", ";
    }
    report += "\n";

    report += "\n";

    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition t : transitions) {
      report += "STEP: "+t.getStringName()+", WEIGHT="+t.getWeight()+"\n";
      report += "GUARDS: "+targetsToString(t.getGuards());
      report += "GROUP: "+t.getGroupName()+"\n";
      report += "POST: "+targetsToString(t.getPostMethods());
      report += "PRE: "+targetsToString(t.getPreMethods());
      report += "\n";
    }
    return report;
  }

  /**
   * Build a string list of the given target methods.
   * 
   * @param targets The methods to build a list for.
   * @return String list with ", " in the end.
   */
  private String targetsToString(Collection<InvocationTarget> targets) {
    String result = "";
    for (InvocationTarget target : targets) {
      Method method = target.getMethod();
      String[] split = method.toGenericString().split(" ");
      String name = split[2];
      result += name +", ";
    }
    result += "\n";
    return result;
  }
}

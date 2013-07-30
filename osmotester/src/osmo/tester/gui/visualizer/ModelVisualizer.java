package osmo.tester.gui.visualizer;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.ModelFactory;

import java.util.Collection;

/** @author Teemu Kanstren */
public class ModelVisualizer {
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
  public void show() {
    MainGenerator generator = new MainGenerator(0, new TestSuite(), config);
    FSM fsm = generator.getFsm();
    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition transition : transitions) {
//      transition.ge
    }
  }
}

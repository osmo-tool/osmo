package osmo.tester.scripting.manual;

import osmo.tester.OSMOTester;
import osmo.tester.model.dataflow.ScriptedValueProvider;

import java.util.Collection;
import java.util.List;

/**
 * The main class for executing manually defined scripts.
 *
 * @author Teemu Kanstren
 */
public class ScripterMain {
  public void run(Collection<Object> modelObjects, List<TestScript> scripts) {
    OSMOTester osmo = new OSMOTester();
    for (Object mo : modelObjects) {
      osmo.addModelObject(mo);
    }
    ScriptAlgorithm algorithm = new ScriptAlgorithm();
    ScriptedValueProvider valueProvider = new ScriptedValueProvider();
    for (TestScript script : scripts) {
      algorithm.addScript(script);
      List<ScriptStep> steps = script.getSteps();
      for (ScriptStep step : steps) {
        List<ScriptValue> values = step.getValues();
        for (ScriptValue value : values) {
          valueProvider.addValue(value.getVariable(), value.getValue());
        }
      }
    }
    osmo.setValueScripter(valueProvider);
    osmo.setAlgorithm(algorithm);
    ScriptEndCondition sec = new ScriptEndCondition(algorithm);
    osmo.addTestEndCondition(sec);
    osmo.addSuiteEndCondition(sec);
    osmo.generate();
  }
}

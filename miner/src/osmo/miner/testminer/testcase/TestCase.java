package osmo.miner.testminer.testcase;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class TestCase {
  private static final Logger log = new Logger(TestCase.class);
  private final String name;
  private Map<String, String> variables = new HashMap<>();
  private Map<String, Step> steps = new LinkedHashMap<>();

  public TestCase(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public void addVariable(String name, String value) {
    variables.put(name, value);
  }

  public List<Step> getSteps() {
    List<Step> result = new ArrayList<>();
    result.addAll(steps.values());
    return result;
  }

  public Map<String, Step> getStepMap() {
    return steps;
  }

  public Step createStep(String name) {
    Step step = steps.get(name);
    if (step == null) {
      step = new Step(this, name);
      steps.put(name, step);
    }
    return step;
  }

  @Override
  public String toString() {
    return "Program{" +
            "name='" + name + '\'' +
            ", steps=" + steps +
            '}';
  }
}

package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.LengthProbability;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class LengthProbabilityConfiguration implements EndConditionConfiguration {
  @Override
  public EndCondition createEndCondition() {
    return null;
  }

  @Override
  public String getType() {
    return LengthProbability.class.getName();
  }

  @Override
  public void fillFrom(EndCondition endCondition) {
  }

  @Override
  public void showGUI(Project project) {
  }

  @Override
  public Map<String, String> getMap() {
    return new HashMap<>();
  }
}

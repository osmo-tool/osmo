package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class LengthConfiguration implements EndConditionConfiguration {
  @Override
  public EndCondition createEndCondition() {
    return null;
  }

  @Override
  public String getType() {
    return Length.class.getName();
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

package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Time;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class TimeConfiguration implements EndConditionConfiguration {
  @Override
  public EndCondition createEndCondition() {
    return null;
  }

  @Override
  public String getType() {
    return Time.class.getName();
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

  @Override
  public void setMap(Map<String, String> map) {
  }
}

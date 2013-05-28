package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class DefaultConfiguration implements EndConditionConfiguration {
  private String type = "";
  
  @Override
  public EndCondition createEndCondition() {
    return null;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void fillFrom(EndCondition endCondition) {
    this.type = endCondition.getClass().getName();
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

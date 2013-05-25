package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;

import java.util.Map;

/** @author Teemu Kanstren */
public interface EndConditionConfiguration {
  public String getType();
  
  public EndCondition createEndCondition();
  
  public void fillFrom(EndCondition endCondition);
  
  public void showGUI(Project project);

  public Map<String,String> getMap();
}

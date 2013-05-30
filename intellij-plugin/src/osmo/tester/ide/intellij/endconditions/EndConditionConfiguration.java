package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;

import java.util.Collection;
import java.util.Map;

/** @author Teemu Kanstren */
public interface EndConditionConfiguration {
  public void showGUI(Project project);
  
  public String getType();
  
  public String getCreateString();
}

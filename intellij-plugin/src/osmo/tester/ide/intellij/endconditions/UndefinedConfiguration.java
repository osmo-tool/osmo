package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class UndefinedConfiguration implements EndConditionConfiguration {
  private String type;

  public UndefinedConfiguration(String type) {
    this.type = type;
  }

  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getCreateString() {
    return "new "+type+"();\n";
  }
}

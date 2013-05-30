package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.Endless;

/** @author Teemu Kanstren */
public class EndlessConfiguration implements EndConditionConfiguration {
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return Endless.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new Endless();\n";
  }
}

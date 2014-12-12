package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.StateCoverage;

/** @author Teemu Kanstren */
public class StateCoverageConfiguration implements EndConditionConfiguration {
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return StateCoverage.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new StateCoverage();\n";
  }
}

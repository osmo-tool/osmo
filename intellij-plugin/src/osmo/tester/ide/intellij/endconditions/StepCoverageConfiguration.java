package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.structure.StepCoverage;

/** @author Teemu Kanstren */
public class StepCoverageConfiguration implements EndConditionConfiguration {
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return StepCoverage.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new StepCoverage();\n";
  }
}

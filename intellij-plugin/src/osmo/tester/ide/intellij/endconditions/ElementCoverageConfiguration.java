package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.structure.ElementCoverage;

/** @author Teemu Kanstren */
public class ElementCoverageConfiguration implements EndConditionConfiguration {
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return ElementCoverage.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new ElementCoverage();\n";
  }
}

package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class LengthConfiguration implements EndConditionConfiguration {
  private int length = 1;
  
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return Length.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new Length("+length+"d);\n";
  }
}

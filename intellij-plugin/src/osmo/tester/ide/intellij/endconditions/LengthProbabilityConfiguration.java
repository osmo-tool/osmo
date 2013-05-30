package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.LengthProbability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class LengthProbabilityConfiguration implements EndConditionConfiguration {
  private int length = 1;
  private double threshold = 0.2d;
  
  @Override
  public void showGUI(Project project) {
  }

  @Override
  public String getType() {
    return LengthProbability.class.getName();
  }

  @Override
  public String getCreateString() {
    return "new LengthProbability("+length+", "+threshold+"d);\n";
  }
}

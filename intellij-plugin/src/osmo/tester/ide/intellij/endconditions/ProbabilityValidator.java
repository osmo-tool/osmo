package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.ui.InputValidator;

/** @author Teemu Kanstren */
public class ProbabilityValidator implements InputValidator {
  @Override
  public boolean checkInput(String inputString) {
    double value = 0;
    try {
      value = Double.parseDouble(inputString);
    } catch (NumberFormatException e) {
      return false;
    }
    return value >= 0 && value <= 1;
  }

  @Override
  public boolean canClose(String inputString) {
    return checkInput(inputString);
  }
}

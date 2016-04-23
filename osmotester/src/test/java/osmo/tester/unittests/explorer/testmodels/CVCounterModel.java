package osmo.tester.unittests.explorer.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.Requirements;

/**
 * Same as the other calculator but without explicit state enumeration. Instead this will keep the counter > 0 and
 * use the counter itself to define the state. This also illustrates how you can name your methods and elements in
 * any way you like, since only the annotations are used.
 *
 * @author Teemu Kanstren
 */
public class CVCounterModel {
  private Requirements req = new Requirements();
  private int counter = 0;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  public static int decreases = 0;
  public static int increases = 0;

  public CVCounterModel() {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
  }

  @BeforeTest
  public void start() {
    counter = 0;
  }

  @Guard("start")
  public boolean checkStart() {
    return counter == 0;
  }

  @TestStep("start")
  public void startState() {
    counter++;
  }

  @Guard("decrease")
  public boolean toDecreaseOrNot() {
    return counter > 1;
  }

  @TestStep("decrease")
  public void decreaseState() {
    req.covered(REQ_DECREASE);
    counter--;
    decreases++;
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter > 0;
  }

  @TestStep("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    increases++;
  }
  
  @CoverageValue
  public String counter(TestCaseStep step) {
    return ""+counter;
  }

  @CoverageValue
  public String requirements(TestCaseStep step) {
    return ""+req.getRequirements().size();
  }
}

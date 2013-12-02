package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.generation.TestVariable;

/** @author Teemu Kanstren */
public class VariableModel1 {
  @Variable
  private int i1 = 1;
  @Variable
  private float f1 = 1.1f;
  @Variable
  private double d1 = 1.2d;
  @Variable
  private boolean b1 = false;
  @Variable
  private Integer i2 = 1;
  @Variable
  private Float f2 = 1.1f;
  @Variable
  private Double d2 = 1.2d;
  @Variable
  private Boolean b2 = false;
  @Variable
  private TestVariable testVariable = new TestVariable("VariableModel1 test variable");
  @Variable
  private String stringVariable = "String variable";
  private TestSuite suite = null;

  @Pre("first")
  public void pre() {
  }

  @Post("first")
  public void post() {
  }

  @Transition("first")
  public void one() {
    i1++;
    f1++;
    d1++;
    b1 = !b1;
  }

  @Guard("first")
  public boolean check1() {
    return true;
  }

  public TestSuite getSuite() {
    return suite;
  }
}

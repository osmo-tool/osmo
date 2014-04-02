package osmo.tester.unittests.issues.models;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.model.VariableValue;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class Issue32 {
  private boolean step1 = false;
  private boolean step2 = false;
  private final PrintStream out;
  @Variable
  private TestUser user = null;

  public Issue32(PrintStream out) {
    this.out = out;
  }

  @BeforeTest
  public void reset() {
    out.println("RESET");
    step1 = false;
    step2 = false;
    user = null;
  }

  @TestStep("Step1")
  public void step1() {
    out.println("Step1");
    step1 = true;
    step2 = false;
    user = new TestUser("guest");
  }
  
  @Guard("Step2")
  public boolean step2Guard() {
    return step1;
  }
  
  @TestStep("Step2")
  public void step2() {
    out.println("Step2:"+step1);
    user = new TestUser("bob");
  }

  @Guard({"Step3", "Step4"})
  public boolean step3and4Guard() {
    return step2;
  }
  
  @TestStep(name="Step3", weight=2)
  public void step3() {
    out.println("Step3");
  }
  
  @TestStep(name="Step4", weight=2)
  public void step4() {
    out.println("Step4");
  }

  private static class TestUser implements VariableValue {
    private final String name;

    private TestUser(String name) {
      this.name = name;
    }

    @Override
    public Object value() {
      return null;
    }

    @Override
    public String toString() {
      return name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TestUser testUser = (TestUser) o;

      if (name != null ? !name.equals(testUser.name) : testUser.name != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
  }
}

package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generation.TestVariable;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Teemu Kanstren
 */
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
  @TestSuiteField
  private TestSuite suite = new TestSuite();

  @Pre("first")
  public void pre(Map<String, Object> state) {
    String msg = "@" + Variable.class.getSimpleName() + " should be visible in @" + Pre.class.getSimpleName() + " methods.";
    System.out.println("pre-state:"+state);
    assertNotNull(msg, state.get("i1"));
    assertNotNull(msg, state.get("f1"));
    assertNotNull(msg, state.get("d1"));
    assertNotNull(msg, state.get("b1"));
    state.put("old-i1", state.get("i1"));
    state.put("old-f1", state.get("f1"));
    state.put("old-d1", state.get("d1"));
    state.put("old-b1", state.get("b1"));
  }

  @Post("first")
  public void post(Map<String, Object> state) {
    String msg = "@" + Variable.class.getSimpleName() + " should be visible in @" + Post.class.getSimpleName() + " methods.";
    System.out.println("post-state:"+state);
    Integer newI1 = (Integer) state.get("i1");
    Float newF1 = (Float) state.get("f1");
    Double newD1 = (Double) state.get("d1");
    Boolean newB1 = (Boolean) state.get("b1");
    assertNotNull(msg, newI1);
    assertNotNull(msg, newF1);
    assertNotNull(msg, newD1);
    assertNotNull(msg, newB1);

    String msg2 = "@"+Post.class.getSimpleName()+" state should be updated and different from @"+Pre.class.getSimpleName()+" (assuming a @"+Transition.class.getName()+" changed state).";
    Integer oldI1 = (Integer) state.get("old-i1");
    Float oldF1 = (Float) state.get("old-f1");
    Double oldD1 = (Double) state.get("old-d1");
    Boolean oldB1 = (Boolean) state.get("old-b1");
    assertEquals(msg2, oldI1+1, newI1.intValue());
    assertEquals(msg2, oldF1+1, newF1);
    assertEquals(msg2, oldD1+1, newD1);
    assertEquals(msg2, !oldB1, newB1.booleanValue());
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

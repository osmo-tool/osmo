package osmo.tester.scripting.manual;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.testmodels.CalculatorModel;
import osmo.tester.testmodels.VariableModel2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class GenerationTests {
  @Test
  public void calculatorScript() {
    CalculatorModel model = new CalculatorModel(System.out);
    Collection<Object> modelObjects = new ArrayList<>();
    modelObjects.add(model);
    ScripterMain main = new ScripterMain();
    List<TestScript> scripts = new ArrayList<>();
    TestScript script = new TestScript();
    script.addStep("start");
    script.addStep("increase");
    scripts.add(script);
    main.run(modelObjects, scripts);
  }

  @Test
  public void invalidTransitionsAndVariables() {
    VariableModel2 model = new VariableModel2();
    Collection<Object> modelObjects = new ArrayList<>();
    modelObjects.add(model);
    ScripterMain main = new ScripterMain();
    List<TestScript> scripts = new ArrayList<>();
    TestScript script = new TestScript();
    script.addStep("start");
    script.addStep("increase");
    scripts.add(script);
    try {
      main.run(modelObjects, scripts);
      fail("Invalid transitions should fail script validation");
    } catch (IllegalStateException e) {
      String expected = "Validating given script against given model objects failed:Transitions [start, increase] not found in model.";
      assertEquals("Expected validation error", expected, e.getMessage());
    }
  }

  @Test
  public void variableScript() {
    TestUtils.setSeed(2421);
    VariableModel2 model = new VariableModel2(System.out);
    Collection<Object> modelObjects = new ArrayList<>();
    modelObjects.add(model);
    ScripterMain main = new ScripterMain();
    List<TestScript> scripts = new ArrayList<>();
    TestScript script = new TestScript();
    script.addStep("first");
    script.addStep("second");
    script.addStep("third");
    script.addValue("set", "v3");
    scripts.add(script);
    main.run(modelObjects, scripts);
  }
}

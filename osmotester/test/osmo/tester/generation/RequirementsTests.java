package osmo.tester.generation;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel2;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class RequirementsTests {
  @Test
  public void fullCoverage() {
    Requirements req = new Requirements();
    req.add(ValidTestModel2.REQ_EPIX);
    req.add(ValidTestModel2.REQ_HELLO);
    req.add(ValidTestModel2.REQ_WORLD);
    OSMOTester osmo = new OSMOTester(new ValidTestModel2(req));
    osmo.addTestEndCondition(new Length(3));
    osmo.addSuiteEndCondition(new Length(1));
    osmo.generate();
    assertEquals(3, req.getUniqueCoverage().size());
    assertEquals(3, req.getRequirements().size());
    assertEquals(0, req.getExcess().size());
  }

  @Test
  public void excessCoverage() {
    Requirements req = new Requirements();
    req.add(ValidTestModel2.REQ_EPIX);
    OSMOTester osmo = new OSMOTester(new ValidTestModel2(req));
    osmo.addTestEndCondition(new Length(3));
    osmo.addSuiteEndCondition(new Length(1));
    osmo.generate();
    assertEquals(3, req.getUniqueCoverage().size());
    assertEquals(1, req.getRequirements().size());
    assertEquals(2, req.getExcess().size());
  }

  @Test
  public void fullExcessCoverage() {
    Requirements req = new Requirements();
    OSMOTester osmo = new OSMOTester(new ValidTestModel2(req));
    osmo.addTestEndCondition(new Length(3));
    osmo.addSuiteEndCondition(new Length(1));
    osmo.generate();
    assertEquals(3, req.getUniqueCoverage().size());
    assertEquals(0, req.getRequirements().size());
    assertEquals(3, req.getExcess().size());
  }

  @Test
  public void lackingCoverage() {
    Requirements req = new Requirements();
    req.add(ValidTestModel2.REQ_EPIX);
    req.add(ValidTestModel2.REQ_HELLO);
    req.add(ValidTestModel2.REQ_WORLD);
    req.add("undefined");
    OSMOTester osmo = new OSMOTester(new ValidTestModel2(req));
    osmo.addTestEndCondition(new Length(3));
    osmo.addSuiteEndCondition(new Length(1));
    osmo.generate();
    assertEquals(3, req.getUniqueCoverage().size());
    assertEquals(4, req.getRequirements().size());
    assertEquals(0, req.getExcess().size());
  }
}

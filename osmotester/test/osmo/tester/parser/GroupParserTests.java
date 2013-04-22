package osmo.tester.parser;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.testmodels.GroupModel1;
import osmo.tester.testmodels.GroupModelInvalid;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class GroupParserTests {
  private MainParser parser = null;

  @Before
  public void setup() {
    parser = new MainParser();
  }

  private OSMOConfiguration conf(Object... modelObjects) {
    OSMOConfiguration config = new OSMOConfiguration();
    for (Object mo : modelObjects) {
      config.addModelObject(mo);
    }
    return config;
  }

  @Test
  public void testValidModel() throws Exception {
    GroupModel1 model = new GroupModel1();
    ParserResult result = parser.parse(conf(model), new TestSuite());
    FSM fsm = result.getFsm();
    assertEquals("Number of @Before methods", 0, fsm.getBefores().size());
    assertEquals("Number of @BeforeSuite methods", 0, fsm.getBeforeSuites().size());
    assertEquals("Number of @After methods", 0, fsm.getAfters().size());
    assertEquals("Number of @AfterSuite methods", 0, fsm.getAfterSuites().size());
    assertEquals("Number of end conditions", 0, fsm.getEndConditions().size());
    assertEquals("Number of exploration enablers", 0, fsm.getExplorationEnablers().size());
    FSMTransition step1 = fsm.getTransition("step1");
    assertEquals("Number of guards", 2, step1.getGuards().size());
    FSMTransition step2 = fsm.getTransition("step2");
    assertEquals("Number of guards", 2, step2.getGuards().size());
    FSMTransition step3 = fsm.getTransition("step3");
    assertEquals("Number of guards", 3, step3.getGuards().size());
    FSMTransition step4 = fsm.getTransition("step4");
    assertEquals("Number of guards", 3, step4.getGuards().size());

    assertEquals("Group name", "group1", step1.getGroupName().toString());
    assertEquals("Group name", "group1", step2.getGroupName().toString());
    assertEquals("Group name", "group1", step3.getGroupName().toString());
    assertEquals("Group name", "big-group", step4.getGroupName().toString());
  }

  @Test
  public void testInvalidModel() throws Exception {
    GroupModelInvalid model = new GroupModelInvalid();
    try {
      ParserResult result = parser.parse(conf(model), new TestSuite());
    } catch (Exception e) {
      String expected = "Invalid FSM:\n" +
              "@Group must have name.\n" +
              "Guard without matching transition:group1.\n" +
              "Pre without matching transition:group1.\n" +
              "Post without matching transition:group1.\n" +
              "Groupname same as a step name (step3). Must be different.\n";
      assertEquals("Errors for parsing invalid group model", expected, e.getMessage());
    }
  }
}

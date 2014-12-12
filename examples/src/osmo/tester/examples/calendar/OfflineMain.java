package osmo.tester.examples.calendar;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

/**
 * The class used to generate tests from the calendar example.
 *
 * @author Teemu Kanstren
 */
public class OfflineMain {
  /**
   * This is used to execute the calendar example with an offline scripter.
   *
   * @param args command line arguments, ignored.
   */
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    OSMOConfiguration config = new OSMOConfiguration();
    config.setUnwrapExceptions(true);
    osmo.setConfig(config);
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(5));
    ModelState state = new ModelState();
    state.setUserCount(3);
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    osmo.setModelFactory(factory);
    factory.add(state);
    factory.add(new CalendarMeetingModel(state, scripter));
    factory.add(new CalendarOracleModel(state, scripter));
    factory.add(new CalendarTaskModel(state, scripter));
    factory.add(new CalendarOverlappingModel(state, scripter));
    factory.add(new CalendarParticipantModel(state, scripter));
    factory.add(new CalendarErrorHandlingModel(state, scripter));
    osmo.generate(55);
    TestSuite suite = osmo.getSuite();
    FSM fsm = osmo.getFsm();
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String report = html.getTraceabilityMatrix();
    TestUtils.write(report, "coverage.html");
  }

  //time limit = 10 years
  //add task, random time (DONE)
  //add event, random time (DONE)
  //add task, overlapping task (DONE)
  //add event, overlapping event (DONE)
  //add event, overlapping task (DONE)
  //remove chosen event (DONE)
  //remove events in timeframe
  //remove chosen task (DONE)
  //remove tasks in timeframe (IGNORE)
  //check tasks are always correct (post) (DONE)
  //check events are always correct (post) (DONE)
  //remove task that does not exist (DONE)
  //remove event that does not exist (DONE)
  //remove events in timeframe where none exist (IGNORE)
  //remove tasks in timeframe where none exist (IGNORE)
  //link task to several users (IGNORE)
  //link event to several users (DONE)
  //remove task from a single user while linked to others (DONE)
  //check tasks for all users (DONE)
  //check events for all users (DONE)
  //check geteventforday in post, also gettaskforday (IGNORE)
  //user boundary values for task remove and add (IGNORE)
  //create specific model object for each boundary (NO BOUNDARY PRESENT, IGNORE)
  //create more osmo.visualizer.examples of using dataflow objects (IF WE CAN THINK OF SOME)
  //create example of failing script (DONE)
  //create example of oracle in transitions (DONE)
}

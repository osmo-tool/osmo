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
    osmo.setConfig(config);
    config.setTestEndCondition(new Length(5));
    config.setSuiteEndCondition(new Length(5));
    ModelState state = new ModelState();
    state.setUserCount(3);
    OfflineScripter scripter = new OfflineScripter(state, "tests.html");
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    config.setFactory(factory);
    factory.add(state);
    factory.add(new CalendarMeetingModel(state, scripter));
    factory.add(new CalendarOracleModel(state, scripter));
    factory.add(new CalendarTaskModel(state, scripter));
    factory.add(new CalendarOverlappingModel(state, scripter));
    factory.add(new CalendarParticipantModel(state, scripter));
    factory.add(new CalendarErrorHandlingModel(state, scripter));
    osmo.generate(55);
    scripter.write();
    TestSuite suite = osmo.getSuite();
    FSM fsm = osmo.getFsm();
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String report = html.getTraceabilityMatrix();
    TestUtils.write(report, "coverage.html");
  }
}

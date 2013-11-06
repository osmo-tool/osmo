package osmo.tester.examples.report;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.reporting.jenkins.JenkinsReportGenerator;

/**
 * Example for running as a Java Ant task in Jenkins to generate JUnit test reports for Jenkins.
 * The build.xml for this example module contains a task "calendar-jenkins" showing how to run this.
 *
 * @author Teemu Kanstren
 */
public class CalendarJenkins {
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    OSMOConfiguration config = new OSMOConfiguration();
    JenkinsReportGenerator reporter = new JenkinsReportGenerator("calendar-steps.xml", true);
    config.addListener(reporter);
    osmo.setConfig(config);
    osmo.setSuiteEndCondition(new Length(20));
    ModelState state = new ModelState();
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
    factory.add(new CalendarFailureModel(state, scripter));
    osmo.generate(2324);
  }
}

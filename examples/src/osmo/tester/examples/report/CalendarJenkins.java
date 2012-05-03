package osmo.tester.examples.report;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.*;
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
    JenkinsReportGenerator reporter = new JenkinsReportGenerator();
    config.addListener(reporter);
    osmo.setConfig(config);
    osmo.addSuiteEndCondition(new Length(20));
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter("tests.html");
    osmo.addModelObject(state);
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    osmo.generate();
    reporter.writeStepReport("calendar-steps.xml");
    reporter.writeTestReport("calendar-tests.xml");
  }
}

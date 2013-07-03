package osmo.tester.examples.calendar;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.visualizer.fsmbuild.FSMBuildVisualizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The class used to generate tests from the calendar example.
 *
 * @author Teemu Kanstren
 */
public class ManualDriveMain {
  /**
   * This is used to execute the calendar example.
   *
   * @param args command line arguments, ignored.
   */
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.setAlgorithm(new ManualAlgorithm(osmo));
//    osmo.setSuiteEndCondition(new Length(2));
    ModelState state = new ModelState();
//    CalendarScripter scripter = new OnlineScripter();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    osmo.addModelObject(new CalendarMeetingModel(state, scripter));
//    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
//    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
//    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(state);
    osmo.setTestEndCondition(new Endless());
    osmo.setSuiteEndCondition(new Endless());
    osmo.addListener(new FSMBuildVisualizer());
//    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    //osmo.setValueScripter(new ScriptedValueProvider());
    osmo.generate(111);
  }
}

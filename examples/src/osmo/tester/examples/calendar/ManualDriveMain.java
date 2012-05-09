package osmo.tester.examples.calendar;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.*;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.tester.scripting.manual.AsciiParser;
import osmo.tester.scripting.manual.ScripterMain;
import osmo.tester.scripting.manual.TestScript;
import osmo.visualizer.model.FSMBuildVisualizer;

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
//    osmo.addSuiteEndCondition(new Length(2));
    ModelState state = new ModelState();
//    CalendarScripter scripter = new OnlineScripter();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(state);
    osmo.addTestEndCondition(new Endless());
    osmo.addSuiteEndCondition(new Endless());
    osmo.addListener(new FSMBuildVisualizer());
//    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    osmo.setSeed(111);
    osmo.setAlgorithm(new ManualAlgorithm());
    //osmo.setValueScripter(new ScriptedValueProvider());
    osmo.generate();
  }

  public static void main2(String[] args) throws Exception {
    Collection<Object> modelObjects = new ArrayList<>();
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    modelObjects.add(new CalendarBaseModel(state, scripter));
    modelObjects.add(new CalendarOracleModel(state, scripter));
    modelObjects.add(new CalendarTaskModel(state, scripter));
    modelObjects.add(new CalendarOverlappingModel(state, scripter));
    modelObjects.add(new CalendarParticipantModel(state, scripter));
    modelObjects.add(new CalendarErrorHandlingModel(state, scripter));
//    modelObjects.add();
    ScripterMain main = new ScripterMain();
    AsciiParser parser = new AsciiParser();
    List<TestScript> scripts = parser.loadAndParse("osmo-tests.txt");
    main.run(modelObjects, scripts);
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

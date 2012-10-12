package osmo.tester.examples.calendar;

import osmo.tester.OSMOConfiguration;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.scripting.OSMOConfigurationFactory;

import java.util.Random;

/** @author Teemu Kanstren */
public class CalendarModelFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    config.addSuiteEndCondition(new Length(1));
    config.addModelObject(state);
//    config.addTestEndCondition(new Probability(0.11));
    config.addModelObject(new CalendarMeetingModel(state, scripter));
//    config.addModelObject(new CalendarOracleModel(state, scripter));
    config.addModelObject(new CalendarTaskModel(state, scripter));
//    config.addModelObject(new CalendarOverlappingModel(state, scripter));
//    config.addModelObject(new CalendarParticipantModel(state, scripter));
//    config.addModelObject(new CalendarErrorHandlingModel(state, scripter));
//    config.addModelObject(new CalendarFailureModel(state, scripter));
    return config;
  }
}

package osmo.tester.examples.calendar;

import osmo.tester.OSMOConfiguration;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.*;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scripting.OSMOConfigurationFactory;

/** @author Teemu Kanstren */
public class CalendarModelFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    config.addSuiteEndCondition(new Length(5));
    config.addModelObject(state);
    config.addModelObject(new CalendarBaseModel(state, scripter));
    config.addModelObject(new CalendarOracleModel(state, scripter));
    config.addModelObject(new CalendarTaskModel(state, scripter));
    config.addModelObject(new CalendarOverlappingModel(state, scripter));
    config.addModelObject(new CalendarParticipantModel(state, scripter));
    config.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    config.addModelObject(new CalendarFailureModel(state, scripter));
    return config;
  }
}

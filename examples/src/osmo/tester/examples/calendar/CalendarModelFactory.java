package osmo.tester.examples.calendar;

import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.scripting.dsm.ModelObjectFactory;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class CalendarModelFactory implements ModelObjectFactory {
  @Override
  public Collection<Object> createModelObjects() {
    ModelState state = new ModelState();
//    CalendarScripter scripter = new OnlineScripter();
    CalendarScripter scripter = new OfflineScripter("tests.html");
    Collection<Object> models = new ArrayList<>();
    models.add(state);
    models.add(new CalendarBaseModel(state, scripter));
    models.add(new CalendarOracleModel(state, scripter));
    models.add(new CalendarTaskModel(state, scripter));
    models.add(new CalendarOverlappingModel(state, scripter));
    models.add(new CalendarParticipantModel(state, scripter));
    models.add(new CalendarErrorHandlingModel(state, scripter));
    models.add(new CalendarFailureModel(state, scripter));
    return models;
  }

  @Override
  public Collection<EndCondition> createTestEndConditions() {
    return new ArrayList<>();
  }

  @Override
  public Collection<EndCondition> createSuiteEndConditions() {
    return new ArrayList<>();
  }
}

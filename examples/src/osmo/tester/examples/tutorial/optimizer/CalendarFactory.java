package osmo.tester.examples.tutorial.optimizer;

import osmo.common.NullPrintStream;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

/**
 * @author Teemu Kanstren
 */
public class CalendarFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels here) {
    ModelState state = new ModelState();
    OfflineScripter scripter = new OfflineScripter(state, null);
    here.add(new ScriptMob(scripter));
    here.add(state);
    here.add(new ExtraState(state));
    here.add(new CalendarMeetingModel(state, scripter, NullPrintStream.stream));
    here.add(new CalendarFailureModel(state, scripter, NullPrintStream.stream));
    here.add(new CalendarOracleModel(state, scripter, NullPrintStream.stream));
    here.add(new CalendarParticipantModel(state, scripter, NullPrintStream.stream));
    here.add(new CalendarTaskModel(state, scripter, NullPrintStream.stream));
    here.add(new CalendarErrorHandlingModel(state, scripter, NullPrintStream.stream));
  }
}

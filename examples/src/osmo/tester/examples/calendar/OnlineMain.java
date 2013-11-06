package osmo.tester.examples.calendar;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.online.OnlineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class OnlineMain {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.setSuiteEndCondition(new Length(5));
    osmo.setTestEndCondition(new Length(5));
    ModelState state = new ModelState();
    CalendarScripter scripter = new OnlineScripter();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    osmo.setModelFactory(factory);
    factory.add(new CalendarMeetingModel(state, scripter));
    OSMOConfiguration config = osmo.getConfig();
    config.setFailWhenError(false);
    osmo.generate(111);
  }

}

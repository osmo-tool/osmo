package osmo.tester.examples.calendar;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.online.OnlineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.listener.AbstractListener;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;

/** @author Teemu Kanstren */
public class OnlineMain {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.setSuiteEndCondition(new Length(5));
    osmo.setTestEndCondition(new Length(5));
    ModelState state = new ModelState();
    CalendarScripter scripter = new OnlineScripter();
    osmo.addModelObject(new CalendarMeetingModel(state, scripter));
    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    osmo.addModelObject(state);
    OSMOConfiguration config = osmo.getConfig();
    config.setStopTestOnError(false);
    config.setStopGenerationOnError(false);
    config.setUnwrapExceptions(true);
    osmo.addListener(new ErrorListener());
    osmo.generate(111);
  }
  
  private static class ErrorListener extends AbstractListener {
    @Override
    public void testError(TestCase test, Throwable error) {
      System.out.println("ERROR in step '"+test.getCurrentStep()+"':"+error);
    }
  }

}

package osmo.tester.generator;

import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Observer that is called from all {@link osmo.tester.model.data.SearchableInput} instances to
 * track generated data in the generated test suite.
 *
 * @author Teemu Kanstren
 */
public class Observer {
  /** The suite where the observed values are stored. */
  private static TestSuite suite;
  private static Collection<Object> observations = new ArrayList<>();

  //to keep this static
  private Observer() {
    
  }

  public static void setSuite(TestSuite suite) {
    Observer.suite = suite;
  }

  public static void observe(String variable, Object value) {
    //this is not always set in testing
    if (suite != null) {
      suite.getCurrentTest().addVariableValue(variable, value);
    }
    observations.add(value);
  }

  public static Collection<Object> getObservations() {
    return observations;
  }

  public static void reset() {
    suite = null;
    observations.clear();
  }
}

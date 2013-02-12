package osmo.tester.parser.field;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;

import java.lang.reflect.Field;

/**
 * Represents a test suite field in the model.
 *
 * @author Teemu Kanstren
 */
public class TestSuiteModelField {
  private static Logger log = new Logger(TestSuiteModelField.class);
  /** The model object itself, implementing the actual transition methods etc. */
  private final Object modelObject;
  /** The field itself. */
  private final Field field;

  public TestSuiteModelField(Object modelObject, Field field) {
    this.modelObject = modelObject;
    this.field = field;
  }

  /**
   * Sets the contents of the field.
   *
   * @param suite The new contents.
   */
  public void set(TestSuite suite) {
    try {
      field.set(modelObject, suite);
    } catch (IllegalAccessException e) {
      log.error("Failed to set TestSuite on model object", e);
      throw new RuntimeException(e);
    }
  }
}

package osmo.tester.parser;

import osmo.common.log.Logger;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.generator.testsuite.TestSuite;

import java.lang.reflect.Field;

/**
 * Parses {@link TestSuiteField} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TestSuiteFieldParser implements AnnotationParser {
  private static Logger log = new Logger(TestSuiteFieldParser.class);
  private TestSuite suite = null;

  @Override
  public String parse(ParserParameters parameters) {
    TestSuiteField annotation = (TestSuiteField) parameters.getAnnotation();
    log.debug("TestLogField processing");
    String errors = "";
    String name = "@" + TestSuiteField.class.getSimpleName();
    try {
      Field field = parameters.getField();
      Class<?> type = field.getType();
      if (type != TestSuite.class) {
        errors += name + " class must be of type " + TestSuite.class.getName() + ". Was " + type.getName() + ".\n";
        return errors;
      }
      //we bypass the private etc. modifiers to access it
      field.setAccessible(true);
      Object model = parameters.getModel();

      TestSuite suite = (TestSuite) field.get(model);
      if (suite == null) {
        errors += name + " value was null, which is not allowed.\n";
        return errors;
      }
      if (this.suite != null && this.suite != suite) {
        errors += "Only one " + name + " allowed in the model.\n";
      }
      this.suite = suite;
      parameters.getFsm().setSuite(suite);

      log.debug("Value is now set to: " + suite);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set " + name, e);
    }
    return errors;
  }
}

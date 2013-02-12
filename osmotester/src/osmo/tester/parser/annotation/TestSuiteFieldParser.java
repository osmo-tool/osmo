package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;

/**
 * Parses {@link osmo.tester.annotation.TestSuiteField} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TestSuiteFieldParser implements AnnotationParser {
  private static Logger log = new Logger(TestSuiteFieldParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    TestSuiteField annotation = (TestSuiteField) parameters.getAnnotation();
    log.debug("TestSuiteField processing");
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
      if (suite != null) {
        errors += name + " value was not null, which is not allowed.\n";
        return errors;
      }
      suite = parameters.getSuite();
      field.set(model, suite);

      log.debug("Value is now set to: " + suite);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set " + name, e);
    }
    return errors;
  }
}

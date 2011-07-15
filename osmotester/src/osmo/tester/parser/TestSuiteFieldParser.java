package osmo.tester.parser;

import osmo.tester.annotation.TestSuiteField;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;

import java.lang.reflect.Field;

/**
 * Parses {@link TestSuiteField} annotations from the given model object.
 * 
 * @author Teemu Kanstren
 */
public class TestSuiteFieldParser implements AnnotationParser {
  private static Logger log = new Logger(TestSuiteFieldParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    TestSuiteField annotation = (TestSuiteField) parameters.getAnnotation();
    log.debug("TestLogField processing");
    String errors = "";
    String name = "@"+TestSuiteField.class.getSimpleName();
    try {
      Field field = parameters.getField();
      Class<?> type = field.getType();
      if (type != TestSuite.class) {
        errors += name+" class must be of type "+TestSuite.class.getName()+". Was "+type.getName()+".\n";
        return errors;
      }
      //we bypass the private etc. modifiers to access it
      field.setAccessible(true);
      Object model = parameters.getModel();
      if (field.get(model) != null) {
        errors += name +" value was pre-initialized in the model, which is not allowed.\n";
        return errors;
      }
      TestSuite suite = parameters.getFsm().getTestSuite();
      field.set(model, suite);
      log.debug("Value is now set to: "+suite);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set "+name, e);
    }
    return errors;
  }
}

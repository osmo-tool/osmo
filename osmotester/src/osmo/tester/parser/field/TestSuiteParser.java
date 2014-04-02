package osmo.tester.parser.field;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;

/**
 * Parses {@link osmo.tester.generator.testsuite.TestSuite} objects from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TestSuiteParser implements AnnotationParser {
  private static final Logger log = new Logger(TestSuiteParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    log.debug("TestSuite parser processing");
    String errors = "";
    Field field = parameters.getField();
    //to enable access to private fields
    field.setAccessible(true);
    String name = SearchableInput.class.getSimpleName();
    Object model = parameters.getModel();
    try {
      TestSuite suite = (TestSuite) field.get(model);
      if (suite != null) {
        errors += name+" value was not null, which is not allowed.\n";
        return errors;
      }
      suite = parameters.getSuite();
      field.set(model, suite);

      log.debug("Value is now set to: " + suite);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set " + field.getName(), e);
    }
    return errors;
  }
}

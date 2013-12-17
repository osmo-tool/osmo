package osmo.tester.parser.field;

import osmo.common.Randomizer;
import osmo.tester.annotation.Variable;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Parses fields of the {@link osmo.common.Randomizer} type and stores them to the test model FSM.
 *
 * @author Teemu Kanstren
 */
public class RandomizerParser implements AnnotationParser {
  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    String errors = "";
    Field field = parameters.getField();
    field.setAccessible(true);
    Object model = parameters.getModel();
    String name = Randomizer.class.getSimpleName();
    Randomizer rand = null;
    try {
      rand = (Randomizer) field.get(model);
      if (rand == null) {
        return name+" must be initialized when defined:" + field.getName() + ".\n";
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse "+name+" object " + field.getName(), e);
    }
    Long seed = parameters.getSeed();
    rand.setSeed(seed);
    return errors;
  }
}

package osmo.tester.parser;

/**
 * The base interface for parsing annotations. 
 * The {@link osmo.tester.parser.MainParser} class maps annotations by their type to specific parsers.
 * When an annotation is found in a model object by the parser, the specific parser object for it is invoked.
 * Each of these stores the data to the {@link osmo.tester.model.FSM} test model representation.
 *
 * @author Teemu Kanstren
 */
public interface AnnotationParser {
  /**
   * Called to parse the given information for this annotation.
   *
   * @param result For collecting parsing results.
   * @param parameters The information for the annotation parser.
   * @param errors For collecting all parser errors.
   */
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors);
}

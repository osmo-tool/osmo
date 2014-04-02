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
   * @param parameters The information for the annotation parser.
   * @return Error messages from the parser.
   */
  public String parse(ParserResult result, ParserParameters parameters);
}

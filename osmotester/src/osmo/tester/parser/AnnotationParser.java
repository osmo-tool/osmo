package osmo.tester.parser;

/**
 * The base interface for parsing annotations. The {@link MainParser} class maps annotations by their type to
 * specific parsers and calls a matching parser that implements this interface. This specific parser object is then
 * expected to update the underlying {@link osmo.tester.model.FSM} representation according to the information
 * encoded in the annotation.
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
  public String parse(ParserParameters parameters);
}

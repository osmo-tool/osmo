package osmo.tester.scripting;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for slicer and manual ascii parser.
 *
 * @author Teemu Kanstren
 */
public class AbstractAsciiParser {
  private final Logger log;

  public AbstractAsciiParser(Logger log) {
    this.log = log;
  }

  /**
   * Get a list of rows in given input, as separated by the different potential line separators (unix/windows/mac).
   *
   * @param input The text to be parsed.
   * @return The list of rows separated by linefeeds.
   */
  protected String[] parseLines(String input) {
    String[] split = input.split("\n|\r\n|\r");
    for (int i = 0 ; i < split.length ; i++) {
      String s = split[i];
      split[i] = s.trim();
    }
    return split;
  }

  /**
   * Generic parse for all the tables. Supports only 2 column tables, where each column must have content.
   *
   * @param lines   The lines (rows) of the table.
   * @param headers The table headers used to find the table (expected to be separated by , in the input)
   * @return The cells of the table, first row as cells[0], cells[1], second row as cells[2], cells[3], etc.
   */
  public String[] parseTable(String[] lines, String... headers) {
    //table name here is for error reporting
    String tableName = "\"" + headers[0];
    for (int h = 1 ; h < headers.length ; h++) {
      tableName += ", " + headers[h];
    }
    tableName += "\"";
    List<String> temp = new ArrayList<>();
    //this "i" holds the index of overall parsing
    int i = 0;
    int cols = headers.length;
    boolean found = true;
    //first we proceed until we find the header
    for ( ; i < lines.length ; i++) {
      String line = lines[i];
      log.debug("parsing line:" + line);
      String[] cells = line.split(",");
      if (cells.length < cols) {
        continue;
      }
      //TODO: test with two variable tables, test with more and less of columns than headers
      found = true;
      for (int s = 0 ; s < cols ; s++) {
        cells[s] = cells[s].trim();
        if (!cells[s].equalsIgnoreCase(headers[s])) {
          found = false;
          break;
        }
      }
      if (found) {
        log.debug("found table header");
        break;
      }
    }
    //if we find no header, we return an empty set of cells
    if (!found) {
      log.debug("table [" + tableName + "] not found");
      return new String[0];
    }
    //now we parse all cells
    for (i += 1; i < lines.length ; i++) {
      log.debug("parsing line:" + lines[i]);
      //the table cells must be separated with a comma
      String[] cells = lines[i].split(",", -1);
      String error = "Table rows must have " + cols + " cells. " + tableName + " had a row with " + cells.length + " cell(s).";
      //the table must have exactly correct number of cells on each row
      if (cells.length > cols) {
        throw new IllegalArgumentException(error);
      }
      if (cells.length == 1) {
        //empty line ends the table
        if (cells[0].trim().length() == 0) {
          log.debug("table end");
          //table ends
          break;
        }
        //the table must have exactly 2 cells on each row
        throw new IllegalArgumentException(error);
      }
      //trim any excess whitespace from the cell data
      for (String cell : cells) {
        temp.add(cell.trim());
      }
    }
    log.debug("temp:" + temp);
    if (temp.size() == 0) {
      //empty tables are not supported
      throw new IllegalArgumentException("Table " + tableName + " has no content.");
    }
    String[] result = new String[temp.size()];
    return temp.toArray(result);
  }
}

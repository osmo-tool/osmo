package osmo.tester.tools.web.oracle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 
 * Defines expected rows for a table.
 * 
 * @author Teemu Kanstren 
 */
public class ExpectedTable {
  /** Expected rows, each with a list of columns. */
  private List<List<String>> rows = new ArrayList<>();

  /**
   * Add a new row to the set of expected.
   * 
   * @param cells The cells of the row.
   */
  public void addRow(String... cells) {
    List<String> row = new ArrayList<>();
    Collections.addAll(row, cells);
    rows.add(row);
  }

  public List<List<String>> getRows() {
    return rows;
  }
}

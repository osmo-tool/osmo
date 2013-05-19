package osmo.tester.tools.web.oracle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Teemu Kanstren */
public class ExpectedTable {
  private List<List<String>> rows = new ArrayList<>();
  
  public void addRow(String... cells) {
    List<String> row = new ArrayList<>();
    Collections.addAll(row, cells);
    rows.add(row);
  }

  public List<List<String>> getRows() {
    return rows;
  }
}

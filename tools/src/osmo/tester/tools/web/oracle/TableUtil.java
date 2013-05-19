package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** 
 * 
 * 
 * @author Teemu Kanstren 
 */
public class TableUtil {
  /**
   * Find a row with the given set of cells in the given table.
   * 
   * @param expected The set of expected cells to find (defines the row to find).
   * @param rows The set of rows where to find the row in.
   * @param strict If cell data must be exactly the given or just contained in the cell.
   * @return Index to the found row in the given rows list.
   */
  public static int findRow(List<String> expected, List<WebElement> rows, boolean strict) {
    int index = 0;
    for (WebElement row : rows) {
      List<WebElement> tableCells = row.findElements(By.tagName("td"));
      int found = 0;
      for (String expectedCell : expected) {
        for (WebElement tableCell : tableCells) {
          String actualCell = tableCell.getText();
          if (strict) {
            if (actualCell.equals(expectedCell)) {
              found++;
              break;
            }
          } else {
            if (actualCell.contains(expectedCell)) {
              found++;
              break;
            }
          }
        }
      }
      if (found == expected.size()) {
        return index;
      }
      index++;
    }
    throw new AssertionError("Row not found:" + expected);
  }
}

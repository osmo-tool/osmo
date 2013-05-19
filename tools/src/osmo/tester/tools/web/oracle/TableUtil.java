package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** @author Teemu Kanstren */
public class TableUtil {
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

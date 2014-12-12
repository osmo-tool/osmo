package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/** 
 * A test oracle for a HTML table.
 * 
 * A strict oracle means that the actual observed table must match exactly the data in the order stored in the
 * given {@link ExpectedTable} instance. That is, expected table row 1 must be exactly the same as actual table 1.
 * Also, row 1 must have the cells in the exact order in strict mode.
 * 
 * In 'loose' mode (strict is false), the ordering is irrelevant. What matters is that there is an
 * equal number of rows in the expected and actual table (after headers and footers are removed).
 * Also, the data is checked so that for each row in the expected table, there exists a row in the actual table
 * that has the same set of cells (regardless of order in the row). The row can have more or less data just that
 * one of the cells matches.
 * If a match is found, it is removed so a single cell or row cannot match several expected cells or rows even if
 * the expected rows or cells are equal.
 * 
 * @author Teemu Kanstren 
 */
public class TableOracle {
  /** How big is your table header? We skip this many first rows in the table. */
  private final int headerSize;
  /** How big is your table footer? We remove this many from expected number of rows. */
  private final int footerSize;
  /** Name of the table, used to give meaningful error reports. */
  private final String tableName;
  /** Is the check strict, that is should all rows be in added order or is it enough the data exists? */
  private boolean strict;

  public TableOracle(int headerSize, String tableName) {
    this(headerSize, 0, tableName);
  }

  public TableOracle(int headerSize, int footerSize, String tableName) {
    this.headerSize = headerSize;
    this.footerSize = footerSize;
    this.tableName = tableName;
  }

  /**
   * Call this to perform the check of expected vs actual.
   * 
   * @param expectedTable An object filled with the expected set of rows/cells.
   * @param table The actual table, the root "table" element of the HTML table.
   */
  public void check(ExpectedTable expectedTable, WebElement table) {
    //get all rows in table
    List<WebElement> rows = table.findElements(By.tagName("tr"));
    List<List<String>> expectedRows = expectedTable.getRows();
    //count hte number of actual rows to compare against number of expected rows
    int bodyCount = rows.size() - headerSize - footerSize;
    assertEquals(tableName + " table size", expectedRows.size(), bodyCount);
    if (strict) {
      checkStrict(expectedTable, rows);
    }
    else {
      checkLoose(expectedTable, rows);
    }
  }

  /**
   * Performs the strict mode check, where everything should be in the same order.
   * 
   * @param expectedTable The expected data.
   * @param rows The actual data.
   */
  private void checkStrict(ExpectedTable expectedTable, List<WebElement> rows) {
    List<List<String>> expectedRows = expectedTable.getRows();

    int rowIndex = headerSize;
    for (List<String> eCells : expectedRows) {
      List<WebElement> aCells = rows.get(rowIndex).findElements(By.tagName("td"));
      for (int cellIndex = 0 ; cellIndex < eCells.size() ; cellIndex++) {
        String actual = aCells.get(cellIndex).getText();
        String expected = eCells.get(cellIndex);
        assertEquals("Table "+tableName+" at row "+rowIndex+" cell "+cellIndex, expected, actual);
      }
      rowIndex++;
    }
  }

  /**
   * Performs the 'loose' mode check, where order is irrelevant.
   * 
   * @param expectedTable The expected data.
   * @param rows The actual data.
   */
  private void checkLoose(ExpectedTable expectedTable, List<WebElement> rows) {
    List<List<String>> expectedRows = expectedTable.getRows();
    
    for (WebElement row : rows) {
      String rowText = row.getText();
      for (Iterator<List<String>> i = expectedRows.iterator() ; i.hasNext() ; ) {
        List<String> cells = i.next();
        int found = 0;
        for (String cell : cells) {
          //TODO: change this to require the text to be in a cell in the row
          if (!rowText.contains(cell)) {
            break;
          }
          found++;
        }
        if (found == cells.size()) {
          i.remove();
          break;
        }
      }
    }
    assertEquals("Too many items left in table ("+tableName+"):"+expectedRows, 0, expectedRows.size());
  }

  public TableOracle setStrict(boolean strict) {
    this.strict = strict;
    return this;
  }

  public boolean isStrict() {
    return strict;
  }
}

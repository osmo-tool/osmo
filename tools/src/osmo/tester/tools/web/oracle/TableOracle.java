package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class TableOracle {
  private final int headerSize;
  private final int footerSize;
  private final String tableName;
  private boolean strict;

  public TableOracle(int headerSize, String tableName) {
    this(headerSize, 0, tableName);
  }

  public TableOracle(int headerSize, int footerSize, String tableName) {
    this.headerSize = headerSize;
    this.footerSize = footerSize;
    this.tableName = tableName;
  }

  public void check(ExpectedTable expectedTable, WebElement table) {
    List<WebElement> rows = table.findElements(By.tagName("tr"));
    List<List<String>> expectedRows = expectedTable.getRows();
    int bodyCount = rows.size() - headerSize - footerSize;
    assertEquals(tableName + " table size", expectedRows.size(), bodyCount);
    if (strict) {
      checkStrict(expectedTable, rows);
    }
    else {
      checkLoose(expectedTable, rows);
    }
  }

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

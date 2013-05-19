package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class ListOracle {
  private final int tolerance;
  private final String listName;
  private boolean strict = false;

  public ListOracle(int tolerance, String listName) {
    this.tolerance = tolerance;
    this.listName = listName;
  }

  public void check(List<String> expectedList, WebElement actualList) {
    List<WebElement> actual = actualList.findElements(By.tagName("li"));
    int min = expectedList.size() - tolerance;
    int max = expectedList.size();
    int actualSize = actual.size();
    String errorMsg = listName + " list size should be between " + min + " - " + max + ", was " + actualSize;
    try {
      assertTrue(errorMsg, actualSize >= min && actualSize <= max);
    } catch (Error e) {
      e.printStackTrace();
    }
    if (strict) {
      checkStrict(expectedList, actual);
    }
    else {
      checkLoose(expectedList, actual);
    }
  }

  private void checkStrict(List<String> expectedList, List<WebElement> actualList) {
    int index = 0;
    for (String expectedRow : expectedList) {
      String actualRow = actualList.get(index).getText();
      assertEquals("List "+listName+" at row "+index, expectedRow, actualRow);
      index++;
    }
  }

  private void checkLoose(List<String> expectedList, List<WebElement> actualList) {
    for (WebElement actualRow : actualList) {
      String actualRowText = actualRow.getText();
      for (Iterator<String> i = expectedList.iterator() ; i.hasNext() ; ) {
        String expectedRow = i.next();
        if (actualRowText.contains(expectedRow)) {
          i.remove();
          break;
        }
      }
    }
    assertTrue("Too many items left in list ("+listName+"):"+expectedList+" max="+tolerance, expectedList.size() <= tolerance);
  }

  public ListOracle setStrict(boolean strict) {
    this.strict = strict;
    return this;
  }

  public boolean isStrict() {
    return strict;
  }
}

package osmo.tester.tools.web.oracle;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import osmo.common.log.Logger;

import java.util.Iterator;
import java.util.List;

/** 
 * A test oracle for a HTML list.
 * 
 * @author Teemu Kanstren 
 */
public class ListOracle {
  private static final Logger log = new Logger(ListOracle.class);
  /** Tolerance for how many items the list should have, e.g. expect 5 but tolerance of 2 = 3-7 expected size. */
  private final int tolerance;
  /** List name, used for readable error reporting. */
  private final String listName;
  /** If true, checking will be strick and row content must be exact match. Otherwise just the text has to be part of the content. */
  private boolean strict = false;
  /** If set, we do not take the text of the row but an attribute value for comparison. */
  private String attribute = null;
  /** How many times do we retry if the oracle fails? */
  private int retryCount = 0;
  /** What is the time to wait between retries? */
  private int waitTime = 2;

  public ListOracle(int tolerance, String listName) {
    this.tolerance = tolerance;
    this.listName = listName;
  }
  
  public ListOracle setWaitTime(int waitTime) {
    this.waitTime = waitTime;
    return this;
  }

  public ListOracle setRetryCount(int retryCount) {
    this.retryCount = retryCount;
    return this;
  }

  public ListOracle setAttribute(String attribute) {
    this.attribute = attribute;
    return this;
  }

  /**
   * Check if actual list matched expected.
   * 
   * @param expectedList The expectations.
   * @param actualList The actual HTML table (root element).
   */
  public void check(List<String> expectedList, WebElement actualList) {
    int retries = 0;
    while (true) {
      try {
        List<WebElement> actual = actualList.findElements(By.tagName("li"));
        int min = expectedList.size() - tolerance;
        int max = expectedList.size();
        int actualSize = actual.size();
        String errorMsg = listName + " list size should be between " + min + " - " + max + ", was " + actualSize;
        if (!(actualSize >= min && actualSize <= max)) {
          throw new AssertionError(errorMsg);
        }
        if (strict) {
          checkStrict(expectedList, actual);
        }
        else {
          checkLoose(expectedList, actual);
        }
        return;
      } catch (AssertionError ae) {
        log.debug("fail "+retries);
        if (retries >= retryCount) {
          throw ae;
        }
        retries++;
        try {
          Thread.sleep(1000*waitTime);
        } catch (InterruptedException e) {
        }
      }
    }
  }

  /**
   * Check in strict mode, expecting all rows to be in same order for expected vs actual.
   * 
   * @param expectedList The expected rows.
   * @param actualList The actual HTML rows (root element).
   */
  private void checkStrict(List<String> expectedList, List<WebElement> actualList) {
    int index = 0;
    for (String expectedRow : expectedList) {
      String actualRow = actualList.get(index).getText();
      if (attribute != null) actualRow = actualList.get(index).getAttribute(attribute);
      if (!expectedRow.equals(actualRow)) {
        throw new AssertionError("List "+listName+" at row "+index);
      }
      index++;
    }
  }

  /**
   * Check if the gives list of items is in the list, regardless of the ordering.
   * 
   * @param expectedList The expected rows.
   * @param actualList The actual rows (HTML list root element).
   */
  private void checkLoose(List<String> expectedList, List<WebElement> actualList) {
    log.debug("List of actual rows:"+actualList);
    for (WebElement actualRow : actualList) {
      String actualRowText = actualRow.getText();
      log.debug("Found item:"+actualRowText);
      if (attribute != null) actualRowText = actualRow.getAttribute(attribute);
      for (Iterator<String> i = expectedList.iterator() ; i.hasNext() ; ) {
        String expectedRow = i.next();
        if (actualRowText.contains(expectedRow)) {
          i.remove();
          break;
        }
      }
    }
    if (expectedList.size() > tolerance) {
      throw new AssertionError("Too many items left in list ("+listName+"):"+expectedList+" max="+tolerance);
    }
  }

  public ListOracle setStrict(boolean strict) {
    this.strict = strict;
    return this;
  }

  public boolean isStrict() {
    return strict;
  }
}

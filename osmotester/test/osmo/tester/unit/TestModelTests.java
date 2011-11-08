package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class TestModelTests {
  @Test
  public void customAttributes() {
    TestCase test = new TestCase();
    test.setAttribute("script", "whooppee");
    assertEquals("Stored attribute in test case", "whooppee", test.getAttribute("script"));
  }
}

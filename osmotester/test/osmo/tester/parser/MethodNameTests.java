package osmo.tester.parser;

import org.junit.Test;
import osmo.tester.parser.annotation.GuardParser;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class MethodNameTests {
  @Test
  public void okNameParsing() {
    String input = "guardHelloWorld";
    String output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "HelloWorld", output);

    input = "allowStep";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "Step", output);

    input = "g_MethodHere";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "MethodHere", output);

    input = "BadButOKParse";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "BadButOKParse", output);
  }

  @Test
  public void nokNameParsing() {
    String input = "alllowercase";
    String output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "", output);
  }
}

package osmo.tester.unittests.parser;

import org.junit.Test;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.annotation.GuardParser;
import osmo.tester.parser.annotation.TestStepParser;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class MethodNameTests {
  @Test
  public void okNameParsingGuard() {
    String input = "guardHelloWorld";
    String output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "HelloWorld", output);

    input = "allowStep";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "Step", output);
    input = "g_MethodHere";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "MethodHere", output);

    input = "gMethodHere";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "MethodHere", output);

    input = "BadButOKParse";
    output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "BadButOKParse", output);
  }

  @Test
  public void nokNameParsingGuard() {
    String input = "alllowercase";
    String output = GuardParser.findNameFrom(input);
    assertEquals("Parsed guard name", "", output);
  }

  @Test
  public void okNameParsingStep() {
    String input = "helloWorld";
    ParserParameters parameters = new ParserParameters();
    String output = TestStepParser.parseName(input);
    assertEquals("Parsed guard name", "HelloWorld", output);

    input = "hilloS";
    output = TestStepParser.parseName(input);
    assertEquals("Parsed guard name", "HilloS", output);

    input = "_MethodHere";
    output = TestStepParser.parseName(input);
    assertEquals("Parsed guard name", "_MethodHere", output);

    input = "a";
    output = TestStepParser.parseName(input);
    assertEquals("Parsed guard name", "A", output);
  }
}

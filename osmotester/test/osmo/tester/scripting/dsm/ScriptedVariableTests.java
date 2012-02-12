package osmo.tester.scripting.dsm;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.model.dataflow.InputObserver;
import osmo.tester.model.dataflow.ReadableWords;
import osmo.tester.model.dataflow.ScriptedValueProvider;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueRangeSet;
import osmo.tester.model.dataflow.ValueSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/** @author Teemu Kanstren */
public class ScriptedVariableTests {
  private ValueSet<Integer> set;
  private ValueRange<Integer> range;
  private ValueRangeSet<Double> rangeSet;
  private ReadableWords words;
  private ScriptedValueProvider scripter;

  @Before
  public void start() {
    set = new ValueSet<>(1, 4, 9);
    range = new ValueRange<>(1, 5);
    rangeSet = new ValueRangeSet<>();
    words = new ReadableWords(8, 13);

    TestUtils.setSeed(111);
    scripter = new ScriptedValueProvider();
    set.setName("bob");
    set.setObserver(new MockObserver());
    range.setName("alice");
    range.setObserver(new MockObserver());
    rangeSet.addPartition(1, 3);
    rangeSet.addPartition(7, 9);
    rangeSet.addPartition(55, 111);
    rangeSet.setName("john");
    rangeSet.setObserver(new MockObserver());
    words.setName("zerowing");
    words.setObserver(new MockObserver());
  }

  @Test
  public void valueSetNoScript() {
    String expected = "4,9,4,9,9,4,1,9,4,1,9,1,9,9,4,9,1,9,9,1,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += set.next() + ",";
    }
    assertEquals("Value from scripted set", expected, actual);
  }

  @Test
  public void valueSetValid() {
    scripter.addValue("bob", "4");
    scripter.addValue("bob", "9");
    set.setScripter(scripter);
    String expected = "4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += set.next() + ",";
    }
    assertEquals("Value from scripted set", expected, actual);
  }

  @Test
  public void valueSetInvalid() {
    scripter.addValue("bob", "4");
    scripter.addValue("bob", "0");
    set.setScripter(scripter);
    set.next();
    try {
      set.next();
      fail("Scripting values not in set should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error message", "Requested scripted value for variable 'bob' not found: 0", e.getMessage());
    }
  }

  @Test
  public void valueRangeNoScript() {
    String expected = "4,1,3,3,5,1,5,3,3,1,1,5,5,5,4,4,1,2,1,1,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += range.next() + ",";
    }
    assertEquals("Value from scripted range", expected, actual);
  }

  @Test
  public void valueRangeValid() {
    scripter.addValue("alice", "2");
    scripter.addValue("alice", "4");
    range.setScripter(scripter);
    String expected = "2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += range.next() + ",";
    }
    assertEquals("Value from scripted range", expected, actual);
  }

  @Test
  public void valueRangeInvalid() {
    scripter.addValue("alice", "2");
    scripter.addValue("alice", "0");
    range.setScripter(scripter);
    range.next();
    try {
      range.next();
      fail("Scripting values not in range should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error message", "Requested invalid scripted value for variable 'alice': 0", e.getMessage());
    }
  }

  @Test
  public void valueRangeSetNoScript() {
    String expected = "9,9,104,3,7,73,75,9,3,91,9,2,2,88,7,76,74,97,8,82,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += rangeSet.next() + ",";
    }
    assertEquals("Value from scripted ValueRangeSet", expected, actual);
  }

  @Test
  public void valueRangeSetValid() {
    scripter.addValue("john", "2");
    scripter.addValue("john", "69");
    scripter.addValue("john", "7");
    rangeSet.setScripter(scripter);
    String expected = "2,69,7,2,69,7,2,69,7,2,69,7,2,69,7,2,69,7,2,69,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += rangeSet.next() + ",";
    }
    assertEquals("Value from scripted ValueRangeSet", expected, actual);
  }

  @Test
  public void valueRangeSetInvalid() {
    scripter.addValue("john", "2");
    scripter.addValue("john", "69");
    scripter.addValue("john", "11");
    rangeSet.setScripter(scripter);
    rangeSet.next();
    rangeSet.next();
    try {
      rangeSet.next();
      fail("Scripting values not in ValueRangeSet should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error message", "Requested invalid scripted value for variable 'john': 11", e.getMessage());
    }
  }

  @Test
  public void readableWordNoScript() {
    String expected = "8/bjL',.f,4EöF30]eE-,ZAÄÅVÅ4/X\"0uM,=Y'ka'CQ,\tm0%DhDPJ>X<,Nwt[´!+h=|T8,+<;[PiellPn:\\,Z9^rUOaÅUC.M,=k=.iw91 ,Åiä<DyYR¨{QzH,MyB;6Z*S%Q,6mu8`::G,zBög6ov4zx2,LG.A6Z(~,-v_/1ÄyEi7\"a,{´l&>gö\\Q%P/,.P´5qwåÖW\t,zÅ`1ä:n4,rV'´#\"4sq,F|\"]I?^#H,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += words.next() + ",";
    }
    assertEquals("Value from scripted ReadableWords", expected, actual);

  }

  @Test
  public void readableWordValid() {
    scripter.addValue("zerowing", "all your");
    scripter.addValue("zerowing", "base are");
    scripter.addValue("zerowing", "belong to us!");
    words.setScripter(scripter);
    String expected = "all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,";
    String actual = "";
    for (int i = 0; i < 20; i++) {
      actual += words.next() + ",";
    }
    assertEquals("Value from scripted ReadableWords", expected, actual);
  }

  @Test
  public void readableWordInvalid() {
    scripter.addValue("zerowing", "all your");
    scripter.addValue("zerowing", "base");
    scripter.addValue("zerowing", "belong to us!");
    words.setScripter(scripter);
    words.next();
    try {
      words.next();
      fail("Scripting values not in ReadableWords should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error message", "Requested invalid scripted value for variable 'zerowing' (must be valid set of chars and length in defined bounds of length 8-13): base", e.getMessage());
    }
  }

  private static class MockObserver implements InputObserver {
    @Override
    public void value(String variable, Object value) {
    }
  }
}

package osmo.tester.scripting.manual;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.data.Text;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueRangeSet;
import osmo.tester.model.data.ValueSet;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ScriptedVariableTests {
  private ValueSet<Integer> set;
  private ValueRange<Integer> range;
  private ValueRangeSet<Double> rangeSet;
  private Text text;
  private ScriptedValueProvider scripter;

  @Before
  public void start() {
    TestSuite suite = new TestSuite();
    TestCase test = suite.startTest();
    test.addStep(new FSMTransition("test"));

    OSMOConfiguration.setSeed(111);
    scripter = new ScriptedValueProvider();
    OSMOConfiguration.setScripter(scripter);
    set = new ValueSet<>(1, 4, 9);
    set.setSuite(suite);
    range = new ValueRange<>(1, 5);
    range.setSuite(suite);
    rangeSet = new ValueRangeSet<>();
    rangeSet.setSuite(suite);
    text = new Text(8, 13);
    text.setSuite(suite);

    set.setName("bob");
    range.setName("alice");
    rangeSet.addPartition(1, 3);
    rangeSet.addPartition(7, 9);
    rangeSet.addPartition(55, 111);
    rangeSet.setName("john");
    text.setName("zerowing");
  }

  @Test
  public void valueSetNoScript() {
    String expected = "1,9,4,1,9,1,9,9,4,9,1,9,9,1,4,9,1,4,1,4,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += set.next() + ",";
    }
    assertEquals("Value from scripted set", expected, actual);
  }

  @Test
  public void valueSetValid() {
    scripter.addValue("bob", "4");
    scripter.addValue("bob", "9");
    String expected = "4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,4,9,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += set.next() + ",";
    }
    assertEquals("Value from scripted set", expected, actual);
  }

  @Test
  public void valueSetInvalid() {
    scripter.addValue("bob", "4");
    scripter.addValue("bob", "0");
    assertEquals("Scripted defined value", 4, (int) set.next());
    try {
      set.next();
      fail("Scripting values not in set should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error message", "ValueSet does not support scripting undefined values: Requested scripted value for variable 'bob' not found: 0", e.getMessage());
    }
  }

  @Test
  public void valueRangeNoScript() {
    String expected = "5,3,3,1,1,5,5,5,4,4,1,2,1,1,1,1,5,1,3,3,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += range.next() + ",";
    }
    assertEquals("Value from scripted range", expected, actual);
  }

  @Test
  public void valueRangeValid() {
    scripter.addValue("alice", "2");
    scripter.addValue("alice", "4");
    String expected = "2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,2,4,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += range.next() + ",";
    }
    assertEquals("Value from scripted range", expected, actual);
  }

  @Test
  public void valueRangeInvalid() {
    scripter.addValue("alice", "2");
    scripter.addValue("alice", "0");
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
    String expected = "1,82,7,3,90,2,62,73,9,81,1,73,66,3,8,75,1,7,3,9,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += rangeSet.next() + ",";
    }
    assertEquals("Value from scripted ValueRangeSet", expected, actual);
  }

  @Test
  public void valueRangeSetValid() {
    scripter.addValue("john", "2");
    scripter.addValue("john", "69");
    scripter.addValue("john", "7");
    String expected = "2,69,7,2,69,7,2,69,7,2,69,7,2,69,7,2,69,7,2,69,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += rangeSet.next() + ",";
    }
    assertEquals("Value from scripted ValueRangeSet", expected, actual);
  }

  @Test
  public void valueRangeSetInvalid() {
    scripter.addValue("john", "2");
    scripter.addValue("john", "69");
    scripter.addValue("john", "11");
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
  public void textNoScript() {
    String expected = "}j(_Å%O),+e5+JÅ]cNL,\"lGHa9Ö~\",ö=.xZ<:Ä2=~,)+_&r{8 4^,0w2'h<#V,]C*?TOÅW-RvPw,vh[.+0,;C[,G3uDHä\"DL+.^,H9*RWÄ9(Ef~_[,_h%..s´H,V\"gQ2zh?2ö,wdTAVÖhJr),tSz´w_6\\X4#,,pÅ+ö='Y=,-Lk+i] l]'=mä,8¨´[ö=b&,vI;}]0ÖI9,=JQTxä^b,\"idgQX1Me,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += text.next() + ",";
    }
    assertEquals("Value from scripted Text", expected, actual);

  }

  @Test
  public void readableWordValid() {
    scripter.addValue("zerowing", "all your");
    scripter.addValue("zerowing", "base are");
    scripter.addValue("zerowing", "belong to us!");
    String expected = "all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,belong to us!,all your,base are,";
    String actual = "";
    for (int i = 0 ; i < 20 ; i++) {
      actual += text.next() + ",";
    }
    assertEquals("Value from scripted Text", expected, actual);
  }

  @Test
  public void scriptedWordsOutOfBounds() {
    scripter.addValue("zerowing", "all your");
    scripter.addValue("zerowing", "base");
    scripter.addValue("zerowing", "belong to us!");
    String word = text.next();
    assertEquals("Generated word", "all your", word);
    word = text.next();
    //this is less than minimum size
    assertEquals("Generated word", "base", word);
  }
}

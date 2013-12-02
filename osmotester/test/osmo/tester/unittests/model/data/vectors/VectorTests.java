package osmo.tester.unittests.model.data.vectors;

import org.junit.Test;
import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.vectors.VectorSet;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class VectorTests {

  /** Loads the test vector and checks various types of input lines. */
  @Test
  public void testVector() {
    VectorSet set = new VectorSet(getClass(), "test_vector");
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded test fuzz vector options", 4, options.size());
    assertEquals("line 1", set.next());
    assertEquals("line 2", set.next());
    assertEquals(" line 3 ", set.next());
    assertEquals("line 4", set.next());
  }

  @Test
  public void testLDAP() {
    VectorSet set = new VectorSet(VectorSet.LDAP);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded LDAP fuzz vector options", 20, options.size());
    assertEquals("|", set.next());
  }

  @Test
  public void testSQLActive() {
    VectorSet set = new VectorSet(VectorSet.SQL_ACTIVE);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded active SQL fuzz vector options", 10, options.size());
    assertEquals("'; exec master..xp_cmdshell 'ping 10.10.1.2'--", set.next());
  }

  @Test
  public void testSQLPassive() {
    VectorSet set = new VectorSet(VectorSet.SQL_PASSIVE);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded passive SQL fuzz vector options", 55, options.size());
    assertEquals("'||(elt(-3+5,bin(15),ord(10),hex(char(45))))", set.next());
  }

  @Test
  public void testXML() {
    VectorSet set = new VectorSet(VectorSet.XML);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded XML fuzz vector options", 7, options.size());
    assertEquals("<![CDATA[<script>var n=0;while(true){n++;}</script>]]>", set.next());
  }

  @Test
  public void testXPATH() {
    VectorSet set = new VectorSet(VectorSet.XPATH);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded XPath fuzz vector options", 10, options.size());
    assertEquals("'+or+'1'='1", set.next());
  }

  @Test
  public void testXSS() {
    VectorSet set = new VectorSet(VectorSet.XSS);
    List<String> options = set.getOptions();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.setSeed(111);
    assertEquals("Loaded XSS fuzz vector options", 22, options.size());
    assertEquals(">\"><script>alert(\"XSS\")</script>&", set.next());
  }
}

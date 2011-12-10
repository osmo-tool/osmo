package osmo.miner.flowminer;

import org.junit.Test;
import osmo.miner.flowminer.dataflow.ValueRangeMiner;
import osmo.miner.flowminer.dataflow.ValueSetMiner;
import osmo.miner.flowminer.dataflow.VariableCountMiner;
import osmo.miner.flowminer.model.*;

import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class DataFlowMinerTests {
  @Test
  public void rangeMinMaxOne() {
    ValueRangeMiner miner = new ValueRangeMiner();
    miner.process("bob", "1");
    miner.process("bob", "2");
    miner.process("bob", "3");
    Map<String,ValueRange> ranges = miner.getRanges();
    assertEquals("Number of ranges", 1, ranges.size());
    ValueRange bob = ranges.get("bob");
    assertEquals("Bob min", 1, bob.getMin());
    assertEquals("Bob max", 3, bob.getMax());
  }

  @Test
  public void rangeMinMaxTwo() {
    ValueRangeMiner miner = new ValueRangeMiner();
    miner.process("bob", "-1");
    miner.process("joe", "-2");
    miner.process("bob", "3");
    miner.process("bob", "1");
    miner.process("joe", "6");
    miner.process("bob", "9");
    Map<String, ValueRange> ranges = miner.getRanges();
    assertEquals("Number of ranges", 2, ranges.size());
    ValueRange bob = ranges.get("bob");
    assertEquals("Bob min", -1, bob.getMin());
    assertEquals("Bob max", 9, bob.getMax());
    ValueRange joe = ranges.get("joe");
    assertEquals("Joe min", -2, joe.getMin());
    assertEquals("Joe max", 6, joe.getMax());
  }

  @Test
  public void setTests() {
    ValueSetMiner miner = new ValueSetMiner();
    miner.process("bob", "-1");
    miner.process("joe", "-2");
    miner.process("bob", "AA");
    miner.process("alice", "1");
    miner.process("alice", "2");
    miner.process("bob", "AA");
    miner.process("joe", "cc");
    miner.process("bob", "9");
    miner.process("alice", "1");
    miner.process("joe", "cc");
    Map<String, ValueSet> sets = miner.getSets();
    assertEquals("Number of sets", 3, sets.size());
    ValueSet bob = sets.get("bob");
    Map<String, Integer> bobValues = bob.getValues();
    assertEquals("Number of bobs", 3, bobValues.size());
    assertEquals(1, bobValues.get("-1").intValue());
    assertEquals(2, bobValues.get("AA").intValue());
    assertEquals(1, bobValues.get("9").intValue());
    ValueSet joe = sets.get("joe");
    Map<String, Integer> joeValues = joe.getValues();
    assertEquals("Number of joes", 2, joeValues.size());
    assertEquals(1, joeValues.get("-2").intValue());
    assertEquals(2, joeValues.get("cc").intValue());
    ValueSet alice = sets.get("alice");
    Map<String, Integer> aliceValues = alice.getValues();
    assertEquals("Number of alices", 2, aliceValues.size());
    assertEquals(2, aliceValues.get("1").intValue());
    assertEquals(1, aliceValues.get("2").intValue());
  }

  @Test
  public void countForTwo() {
    VariableCountMiner miner = new VariableCountMiner();
    miner.process("bob", "-1");
    miner.process("joe", "-2");
    miner.process("bob", "AA");
    miner.process("alice", "1");
    miner.process("alice", "2");
    miner.process("bob", "AA");
    miner.process("joe", "cc");
    miner.process("bob", "9");
    miner.process("alice", "1");
    miner.process("joe", "cc");
    Map<String, Integer> counts = miner.getCounts();
    assertEquals("Number of variable counts", 3, counts.size());
    assertEquals("Number of bobs", 4, counts.get("bob").intValue());
    assertEquals("Number of joes", 3, counts.get("joe").intValue());
    assertEquals("Number of alices", 3, counts.get("alice").intValue());
  }
}

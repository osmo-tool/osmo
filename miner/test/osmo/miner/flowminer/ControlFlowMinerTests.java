package osmo.miner.flowminer;

import org.junit.Test;
import osmo.miner.flowminer.controlflow.PrecedenceMiner;

import java.util.Collection;

/** @author Teemu Kanstren */
public class ControlFlowMinerTests {
  @Test
  public void precedence() {
    PrecedenceMiner miner = new PrecedenceMiner();
    miner.process("a");
    miner.process("b");
    miner.process("a");
    Collection<String> patterns = miner.getPatterns();
    System.out.println("patterns:"+patterns);
  }

  @Test
  public void precedence2() {
    PrecedenceMiner miner = new PrecedenceMiner();
    miner.process("a");
    miner.process("a");
    miner.process("b");
    miner.process("a");
    miner.process("c");
    miner.process("c");
    Collection<String> patterns = miner.getPatterns();
    System.out.println("patterns:" + patterns);
  }

  @Test
  public void precedence3() {
    PrecedenceMiner miner = new PrecedenceMiner();
    miner.process("a");
    miner.process("a");
    miner.process("b");
    miner.process("c");
    miner.process("a");
    miner.process("c");
    Collection<String> patterns = miner.getPatterns();
    System.out.println("patterns:" + patterns);
  }
}

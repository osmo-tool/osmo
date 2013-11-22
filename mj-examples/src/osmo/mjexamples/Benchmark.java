package osmo.mjexamples;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.GreedyOptimizer;
import osmo.tester.optimizer.MultiGreedy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/** @author Teemu Kanstren */
public class Benchmark {
  public static void main(String[] args) {
    Logger.consoleLevel = Level.INFO;
    Collection<Long> times = new ArrayList<>();
    long sum = 0;
    int RUNS = 10;
    for (int i = 0 ; i < RUNS ; i++) {
      long start = System.currentTimeMillis();

      generate();

      long end = System.currentTimeMillis();
      long diff = end-start;
      times.add(diff);
      sum += diff;
    }
    System.out.println("times:"+times);
    System.out.println("avg:"+(sum/RUNS));
  }
  //27s,23s,23s,23s,24s,23s,23s,23s,24s,23s
  //21s,18s,17s,17s,17s,18s,18s,19s,18s,18s
  
  private static void generate() {
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setTestEndCondition(new LengthProbability(50, 0.2d));
    oc.setFailWhenError(false);
    oc.setFactory(new GSMModelFactory(NullPrintStream.stream));
    ScoreConfiguration sc = new ScoreConfiguration();
    sc.setLengthWeight(-1);
    GreedyOptimizer optimizer = new GreedyOptimizer(oc, sc);
    optimizer.setTimeout(1);
    GenerationResults results = optimizer.search(111);
    TestCoverage tc = results.getCoverage();
    System.out.println(tc.coverageString(optimizer.getFsm(), null, null, null, null, false));
  }
}

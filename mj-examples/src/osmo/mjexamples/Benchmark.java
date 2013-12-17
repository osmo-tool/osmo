package osmo.mjexamples;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.greedy.GreedyOptimizer;

import java.util.ArrayList;
import java.util.Collection;
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
    oc.setStopTestOnError(false);
    oc.setFactory(new GSMModelFactory(NullPrintStream.stream));
    ScoreConfiguration sc = new ScoreConfiguration();
    sc.setLengthWeight(-1);
    GreedyOptimizer optimizer = new GreedyOptimizer(oc, sc);
    optimizer.setTimeout(100);
    GenerationResults results = optimizer.search(111);
    TestCoverage tc = results.getCoverage();
    System.out.println(tc.coverageString(optimizer.getFsm(), null, null, null, null, false));
  }

  //22.11.2013, before removing inner loops
//  15:06:53 o.t.o.GreedyOptimizer - greedy 1 starting up
//  15:06:53 o.t.o.GreedyOptimizer - 1:starting iteration 0
//          15:06:58 o.t.o.GreedyOptimizer - 1:sorting and pruning iteration results
//  15:07:22 o.t.o.GreedyOptimizer - loops in sort:354430
//          15:07:22 o.t.o.GreedyOptimizer - 1:iteration time:(1)28476 gain:248563
//          15:07:22 o.t.o.GreedyOptimizer - Generation timed out
//  15:07:22 o.t.o.GreedyOptimizer - GreedyOptimizer 1 generated 1000 tests.
//  15:07:22 o.t.o.GreedyOptimizer - Resulting suite has 459 tests. Generation time 28483 millis
//  Covered elements:
//  Total steps: 24697
//  Unique steps: 15 (of 15)
//  Unique step-pairs: 240
//  Unique requirements: 30
//  Variable values: 0
//  Unique coverage-values: 1030
//  Unique coverage-value-pairs: 5349

  //22.11.2013, after removing inner loops
//  15:39:21 o.t.o.GreedyOptimizer - greedy 1 starting up
//  15:39:21 o.t.o.GreedyOptimizer - 1:starting iteration 0
//          15:39:25 o.t.o.GreedyOptimizer - 1:sorting and pruning iteration results
//  15:39:27 o.t.o.GreedyOptimizer - loops in sort:354430
//          15:39:27 o.t.o.GreedyOptimizer - 1:iteration time:(1)6648 gain:248563
//          15:39:27 o.t.o.GreedyOptimizer - Generation timed out
//  15:39:27 o.t.o.GreedyOptimizer - GreedyOptimizer 1 generated 1000 tests.
//  15:39:27 o.t.o.GreedyOptimizer - Resulting suite has 459 tests. Generation time 6654 millis
//  Covered elements:
//  Total steps: 24697
//  Unique steps: 15 (of 15)
//  Unique step-pairs: 240
//  Unique requirements: 30
//  Variable values: 0
//  Unique coverage-values: 1030
//  Unique coverage-value-pairs: 5349
}

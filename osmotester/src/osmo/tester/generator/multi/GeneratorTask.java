package osmo.tester.generator.multi;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.testsuite.TestSuite;

/** 
 * Runs a single generator in a thread of its own as executed in a thread pool by {@link MultiOSMO}.
 * 
 * @author Teemu Kanstren 
 */
public class GeneratorTask implements Runnable {
  /** The generator configuration. */
  private final OSMOConfiguration config;
  /** Base seed for the generators running in this task. */
  private long seed;
  /** The minimum time the iterations need to run. */
  private final Time time;
  /** A unique ID for this task, for writing traces. */
  private final int id;
  /** Next ID for next parallel task.. */
  private static volatile int nextId = 1;

  public GeneratorTask(OSMOConfiguration config, Time time, long seed) {
    this.config = config;
    this.seed = seed;
    this.time = time;
    this.id = nextId++;
  }

  @Override
  public void run() {
    time.init(0, null);
    int i = 1;
    while (!time.endTest(null, null)) {
      seed += 100;
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.generate(seed);
      TestSuite suite = tester.getSuite();
      OSMOTester.writeTrace("osmo-output/mosmo-task-"+id+"-i-"+i, suite.getAllTestCases(), seed, config);
      i++;
    }
  }
}

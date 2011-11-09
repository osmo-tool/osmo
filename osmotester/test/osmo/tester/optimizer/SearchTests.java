package osmo.tester.optimizer;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOTester;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.VariableModel2;

import java.util.Random;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class SearchTests {
  @Test
  public void transitionsVariablesValuesPairsAndTransitions() {
//    Logger.debug = true;
    TestUtils.setRandom(new Random(111));
    VariableModel2 model = new VariableModel2();
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(model);
    SearchConfiguration config = new SearchConfiguration(tester);
    SearchingOptimizer optimizer = new SearchingOptimizer(config);
//    long start = System.currentTimeMillis();
    Candidate solution = optimizer.search();
    assertEquals("optimized fitness", 8290, solution.getFitness());
//    long end = System.currentTimeMillis();
//    long diff = end - start;
//    int seconds = Math.round(diff / 1000);
//    int minutes = Math.round(seconds / 60);
//    seconds = seconds % 60;
//    System.out.println("time:"+minutes+"min, "+seconds+"s");
//    System.out.println("fitness:"+solution.getFitness());
  }

  @Test
  public void transitionsVariablesValuesPairsAndTransitions2() {
//    Logger.debug = true;
    TestUtils.setRandom(new Random(121));
    VariableModel2 model = new VariableModel2();
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(model);
    SearchConfiguration config = new SearchConfiguration(tester);
    config.setLengthWeight(3);
    config.setPairsWeight(4);
    config.setTransitionWeight(2);
    config.setValueWeight(7);
    config.setVariableWeight(9);
    SearchingOptimizer optimizer = new SearchingOptimizer(config);
//    long start = System.currentTimeMillis();
    Candidate solution = optimizer.search();
    assertEquals("optimized fitness", 2636, solution.getFitness());
//    long end = System.currentTimeMillis();
//    long diff = end - start;
//    int seconds = Math.round(diff / 1000);
//    int minutes = Math.round(seconds / 60);
//    seconds = seconds % 60;
//    System.out.println("time:"+minutes+"min, "+seconds+"s");
//    System.out.println("fitness:"+solution.getFitness());
  }

  @Test
  public void transitionsOnly() {
//    Logger.debug = true;
    TestUtils.setRandom(new Random(111));
    VariableModel2 model = new VariableModel2();
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(model);
    SearchConfiguration config = new SearchConfiguration(tester);
    config.setLengthWeight(0);
    config.setPairsWeight(0);
    config.setTransitionWeight(10);
    config.setValueWeight(0);
    config.setVariableWeight(0);
    SearchingOptimizer optimizer = new SearchingOptimizer(config);
//    long start = System.currentTimeMillis();
    Candidate solution = optimizer.search();
    assertEquals("optimized fitness", 30, solution.getFitness());
//    long end = System.currentTimeMillis();
//    long diff = end - start;
//    int seconds = Math.round(diff / 1000);
//    int minutes = Math.round(seconds / 60);
//    seconds = seconds % 60;
//    System.out.println("time:"+minutes+"min, "+seconds+"s");
//    System.out.println("fitness:"+solution.getFitness());
  }

  @Test
  public void shortestPathAndRequirements() {
    //TODO: this requires full source space exploration, a good example of that ..
    TestUtils.setRandom(new Random(111));
    ValidTestModel2 model = new ValidTestModel2(new Requirements());
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(model);
    SearchConfiguration config = new SearchConfiguration(tester);
    config.setPopulationSize(3);
    config.setLengthWeight(-1);
    config.setPairsWeight(0);
    config.setTransitionWeight(0);
    config.setValueWeight(0);
    config.setVariableWeight(0);
    config.setRequirementWeight(10);
    SearchingOptimizer optimizer = new SearchingOptimizer(config);
    Candidate solution = optimizer.search();
    System.out.println("t1:"+solution.get(0).getSteps().size()+" reqs:"+solution.get(0).getCoveredRequirements());
    System.out.println("t2:"+solution.get(1).getSteps().size()+" reqs:"+solution.get(1).getCoveredRequirements());
    System.out.println("t3:"+solution.get(2).getSteps().size()+" reqs:"+solution.get(2).getCoveredRequirements());
    assertEquals("optimized fitness", 30, solution.getFitness());
  }
}

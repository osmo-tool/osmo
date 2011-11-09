package osmo.tester.optimizer;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class FitnessTests {
  @Test
  public void transitionsOnly() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(0);
    sc.setPairsWeight(0);
    sc.setValueWeight(0);
    sc.setVariableWeight(0);
    sc.setTransitionWeight(10);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for 5 transitions with transitions only enabled", 50, fitness);
  }

  @Test
  public void variablesOnly() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(0);
    sc.setPairsWeight(0);
    sc.setValueWeight(0);
    sc.setVariableWeight(10);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for 6 values with values only enabled", 60, fitness);
  }

  @Test
  public void valuesOnly() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(0);
    sc.setPairsWeight(0);
    sc.setValueWeight(10);
    sc.setVariableWeight(0);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    //we have 10 unique values for 6 variables. that is v1=2 unique values, v2=3, v3=2, v4=1, v5=1, v6=1
    assertEquals("Fitness for 10 values with values only enabled", 100, fitness);
  }

  @Test
  public void lengthOnly() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(10);
    sc.setPairsWeight(0);
    sc.setValueWeight(0);
    sc.setVariableWeight(0);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for 12 transitions with length only enabled", 120, fitness);
  }

  @Test
  public void pairsOnly() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(0);
    sc.setPairsWeight(10);
    sc.setValueWeight(0);
    sc.setVariableWeight(0);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    //[init,t1], [t1,t2], [t2,t3], [t3,t1], [t2,t4], [init,t3], [t3,t2], [t2,t4], [t5,t2], [t2,t1]
    assertEquals("Fitness for 10 transition pairs with pairs only enabled", 100, fitness);
  }

  @Test
  public void lengthAndPairs() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(10);
    sc.setPairsWeight(10);
    sc.setValueWeight(0);
    sc.setVariableWeight(0);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for pairs and length", 220, fitness);
  }

  @Test
  public void lengthPairsAndVariables() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(10);
    sc.setPairsWeight(10);
    sc.setValueWeight(0);
    sc.setVariableWeight(10);
    sc.setTransitionWeight(0);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for pairs and length", 280, fitness);
  }

  @Test
  public void lengthPairsVariablesValuesAndTransitions() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(10);
    sc.setPairsWeight(10);
    sc.setValueWeight(10);
    sc.setVariableWeight(10);
    sc.setTransitionWeight(10);
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for pairs and length", 430, fitness);
  }

  @Test
  public void lengthPairsVariablesValuesAndTransitionsVaryingWeights() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setLengthWeight(2); //2*12=24
    sc.setPairsWeight(3); //3*10=30
    sc.setValueWeight(4); //4*10=40
    sc.setVariableWeight(5); //5*6=30
    sc.setTransitionWeight(6); //6*5=30
    Candidate candidate = createCandidate(sc);
    int fitness = candidate.calculateFitness();
    assertEquals("Fitness for pairs and length", 154, fitness);
  }

  private Candidate createCandidate(SearchConfiguration sc) {
    TestCase tc1 = new TestCase();
    tc1.addStep(new FSMTransition("t1"));
    tc1.addVariableValue("v1", 1);
    tc1.addStep(new FSMTransition("t2"));
    tc1.addVariableValue("v2", 1);
    tc1.addStep(new FSMTransition("t3"));
    tc1.addVariableValue("v3", 1);
    tc1.addStep(new FSMTransition("t1"));
    tc1.addVariableValue("v2", 2);
    tc1.addStep(new FSMTransition("t2"));
    tc1.addVariableValue("v2", 1);
    tc1.addStep(new FSMTransition("t4"));
    tc1.addVariableValue("v1", 3);

    TestCase tc2 = new TestCase();
    tc2.addStep(new FSMTransition("t3"));
    tc2.addVariableValue("v3", 3);
    tc2.addStep(new FSMTransition("t2"));
    tc2.addVariableValue("v2", 5);
    tc2.addStep(new FSMTransition("t4"));
    tc2.addVariableValue("v4", 7);
    tc2.addStep(new FSMTransition("t5"));
    tc2.addVariableValue("v5", 4);
    tc2.addStep(new FSMTransition("t2"));
    tc2.addVariableValue("v1", 1);
    tc2.addStep(new FSMTransition("t1"));
    tc2.addVariableValue("v6", 3);

    List<TestCase> tests = new ArrayList<TestCase>();
    tests.add(tc1);
    tests.add(tc2);
    return new Candidate(sc, tests);
  }
}

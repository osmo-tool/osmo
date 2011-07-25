package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static osmo.tester.TestUtils.*;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but not preferring to take
 * a the least covered of all available transitions (randomly). The difference is that a single
 * transition is not taken many times until other available ones have been taken equally many times.
 * This also try to avoid same order of the transitions.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class OptimizedRandomAlgorithm implements FSMTraversalAlgorithm {
  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    Map<FSMTransition, Integer> coverage = countCoverage(history);
    Map<FSMTransition, Integer> coverageTP = countTransitionPairCoverage(history);
    if(coverageTP != null){
      return optimizedRandomChoice(coverage,coverageTP, transitions);
    }else{
      return optimizedRandomChoice(coverage, transitions);
    }
  }
  
  public static <T> T optimizedRandomChoice(Map<T, Integer> coverage, Map<T, Integer> coverageTP, Collection<T> choices) {
    Collection<T> options = new ArrayList<T>();
    options.addAll(choices);
    options.removeAll(coverage.keySet());
    //options now contains all previously uncovered transitions
    
    Collection<T> optionsTP = new ArrayList<T>();
    optionsTP.addAll(choices);
    optionsTP.removeAll(coverageTP.keySet());
    //options now contains all previously uncovered transition pairs

    //Make union
    options.addAll(optionsTP);
    
    if (options.size() == 0) {
      //we have covered everything at least once so lets count the coverage instead
      int smallest = minOf(coverage.values());
      int smallestTP = minOf(coverageTP.values());

      for (T t : choices) {
        if (coverage.get(t) == smallest) {
          options.add(t);
        }
        if(coverageTP.get(t) == smallestTP){
          options.add(t);
        }
      }
      if (options.size() == 0) {
        options = choices;
      }
    }
    return oneOf(options);
  }
  
  public static <T> T optimizedRandomChoice(Map<T, Integer> coverage, Collection<T> choices) {
    Collection<T> options = new ArrayList<T>();
    options.addAll(choices);
    options.removeAll(coverage.keySet());
    //options now contains all previously uncovered transitions
    if (options.size() == 0) {
      //we have covered everything at least once so lets count the coverage instead
      int smallest = minOf(coverage.values());
      for (T t : choices) {
        if (coverage.get(t) == smallest) {
          options.add(t);
        }
      }
      if (options.size() == 0) {
        options = choices;
      }
    }
    T t = oneOf(options);
    return t;
  }
  
  /**
   * 
   * @param history
   * @return A list of transitions with a numbers which 
   * tells the count of that transition and current transition
   */
  private Map<FSMTransition, Integer> countTransitionPairCoverage(TestSuite history) {
    
    TestStep ts = history.getCurrent().getCurrentStep();
    if(ts == null || ts.getTransition() == null)
      return null;
    FSMTransition currentTransition = ts.getTransition();
    
    Map<FSMTransition, Integer> coverage = new HashMap<FSMTransition, Integer>();
    List<TestCase> tests = history.getAllTestCases();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      FSMTransition previousTransition = null;
      for (TestStep step : steps) {
        //We are interested only pairs where current transition is starting point
        if(previousTransition != null && previousTransition == currentTransition){
          FSMTransition transition = step.getTransition();
          //Increase counter
          Integer count = coverage.get(transition);
          if (count == null) {
            count = 0;
          }
          coverage.put(transition, count+1);
        }
        previousTransition = step.getTransition();
      }  
    }
    return coverage;
  }

  
  private Map<FSMTransition, Integer> countCoverage(TestSuite history) {
    Map<FSMTransition, Integer> coverage = new HashMap<FSMTransition, Integer>();
    List<TestCase> tests = history.getAllTestCases();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        FSMTransition transition = step.getTransition();
        Integer count = coverage.get(transition);
        if (count == null) {
          count = 0;
        }
        coverage.put(transition, count+1);
      }
    }
    return coverage;
  }
  
}

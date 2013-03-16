package osmo.tester.optimizer;

import osmo.tester.generator.testsuite.TestCase;

import java.util.Comparator;

/** 
 * Sorts given test cases to make a test suite deterministic.
 * For example, if we generate several test sets in parallel and merge them the resulting ordering may vary
 * depending on what order did the sets get finished and added to the set. If we then prune this set to pick
 * a smaller set according to some coverage criteria, the resulting set may be different on different runs.
 * By sorting the merged set before pruning it, we get deterministic results.
 * 
 * @author Teemu Kanstren 
 */
public class TestSorter implements Comparator<TestCase> {
  @Override
  public int compare(TestCase t1, TestCase t2) {
    return t1.toString().compareTo(t2.toString());
  }
}

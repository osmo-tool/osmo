package osmo.tester.optimizer.reducer.debug.invariants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Any common sequences of steps in all given test cases.
 * A sequence is list of steps one following another.
 * For example two tests 1:ABCD 2:ACDE we would have shared sequence of CD but not ACD since B is missing from between. 
 * Only reports biggest shared sequences so if we have ABCD as shared sequence, ABC will not be reported.
 * 
 * @author Teemu Kanstren
 */
public class SharedSequence {
  /** Observed sequences. */
  private Collection<String> sequences = new HashSet<>();
  /** Tells if we already processed a test or not, as we initialize only from the first one. */
  private boolean initialized = false;

  /**
   * Initialize by collecting sequences from given test.
   * Following iterations can only remove ones not present in new tests, not add new.
   * This is because the first test did not have them so all tests will not have them. 
   * Some may but we are looking for ones that are found in all. And first one is part of all.
   * 
   * @param steps For the test to check.
   */
  public void init(List<String> steps) {
    if (initialized) return;
    initialized = true;
    sequences.addAll(sequencesFor(steps));
  }

  /**
   * Find all possible sequences in given list of steps.
   * 
   * @param steps To find sequences from.
   * @return Found sequences.
   */
  public Collection<String> sequencesFor(List<String> steps) {
    Collection<String> result = new HashSet<>();
    for (int i = 0 ; i <= steps.size() ; i++) {
      for (int ii = i+1 ; ii <= steps.size() ; ii++) {
        result.add(steps.subList(i, ii).toString());
      }
    }
    return result;
  }

  /**
   * Process given test and remove any patterns not found in it that were in previous ones.
   * 
   * @param steps Of the test to process.
   */
  public void process(List<String> steps) {
    Collection<String> seqs = sequencesFor(steps);
    sequences.retainAll(seqs);
  }

  /**
   * Returns the given sequences.
   * Used for reporting in template.
   * 
   * @return Found sequences.
   */
  public Collection<String> getPatterns() {
    List<String> duplicates = new ArrayList<>();
    //first we remove all duplicates, that is one that are actually embedded in others
    for (String seq1 : sequences) {
      //first and last characters are [] which need to be removed to check for partial match
      String check = seq1.substring(1, seq1.length()-1);
      for (String seq2 : sequences) {
        //if seq2 contains seq1 we do not report seq1
        if (seq2.contains(check) && !seq1.equals(seq2)) {
          duplicates.add(seq1);
          break;
        }
      }
    }
    List<String> result = new ArrayList<>();
    result.addAll(sequences);
    result.removeAll(duplicates);
    Collections.sort(result);
    return result;
  }
}

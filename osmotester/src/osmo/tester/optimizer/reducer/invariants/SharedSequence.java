package osmo.tester.optimizer.reducer.invariants;

import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class SharedSequence {
  private Collection<String> sequences = new HashSet<>();
  private boolean initialized = false;
  
  public void init(List<String> steps) {
    if (initialized) return;
    initialized = true;
    sequences.addAll(sequencesFor(steps));
  }

  public Collection<String> sequencesFor(List<String> steps) {
    Collection<String> result = new HashSet<>();
    for (int i = 0 ; i <= steps.size() ; i++) {
      for (int ii = i+1 ; ii <= steps.size() ; ii++) {
        result.add(steps.subList(i, ii).toString());
      }
    }
    return result;
  }

  public void process(List<String> steps) {
    Collection<String> seqs = sequencesFor(steps);
    sequences.retainAll(seqs);
  }

  public Collection<String> getPatterns() {
    List<String> duplicates = new ArrayList<>();
    for (String seq1 : sequences) {
      String check = seq1.substring(1, seq1.length()-1);
      for (String seq2 : sequences) {
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

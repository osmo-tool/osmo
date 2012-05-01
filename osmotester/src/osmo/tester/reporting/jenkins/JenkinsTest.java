package osmo.tester.reporting.jenkins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/** @author Teemu Kanstren */
public class JenkinsTest {
  private List<JenkinsStep> steps = new ArrayList<>();
  private AtomicInteger nextId = new AtomicInteger(0);
  private final int id;

  public JenkinsTest() {
    id = nextId.incrementAndGet();
  }

  public void add(JenkinsStep step) {
    steps.add(step);
  }
  
  public String getName() {
    return "Test"+id;
  }
  
  public String getClassName() {
    Collection<JenkinsStep> mySteps = new LinkedHashSet<>();
    mySteps.addAll(steps);
    Collection<String> name = new LinkedHashSet<>();
    for (JenkinsStep step : mySteps) {
      name.add(step.getClassName());
    }
    return name.toString();
  }
  
  public String getDuration() {
    if (steps.size() == 0) {
      return "!test has no steps!";
    }
    JenkinsStep first = steps.get(0);
    JenkinsStep last = steps.get(steps.size()-1);
    long startTime = first.getStartTime();
    long endTime = last.getEndTime();
    long duration = endTime - startTime;
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  public List<JenkinsStep> getSteps() {
    return steps;
  }
}

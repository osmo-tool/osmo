package osmo.tester.examples.tutorial.optimizer;

import osmo.tester.annotation.CoverageValue;
import osmo.tester.coverage.NumberCategoryPartitioner;
import osmo.tester.coverage.RangeCategory;
import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.examples.calendar.testmodel.ModelTask;
import osmo.tester.generator.testsuite.TestCaseStep;

import java.util.Collection;

/**
 * @author Teemu Kanstren.
 */
public class ExtraState {
  private final ModelState state;

  public ExtraState(ModelState state) {
    this.state = state;
  }

  @CoverageValue
  public String eventCategories(TestCaseStep step) {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.zeroOneMany();
    Collection<ModelEvent> events = state.getEvents();
    return ncp.categoryFor(events.size());
  }

  @CoverageValue
  public String taskCategories(TestCaseStep step) {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.zeroOneMany();
    Collection<ModelTask> tasks = state.getTasks();
    return ncp.categoryFor(tasks.size());
  }

  @CoverageValue
  public String eventAndTaskCategories(TestCaseStep step) {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.zeroOneMany();
    Collection<ModelEvent> events = state.getEvents();
    Collection<ModelTask> tasks = state.getTasks();
    return ncp.categoryFor(events.size())+"&&"+ncp.categoryFor(tasks.size());
  }

  @CoverageValue
  public String eventCountInSteps(TestCaseStep step) {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.zeroOneMany();
    Collection<ModelEvent> events = state.getEvents();
    return ncp.categoryFor(events.size())+"&&"+step.getName();
  }
}

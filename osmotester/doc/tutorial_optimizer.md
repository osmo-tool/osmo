Tutorial: Using Optimizer in OSMO Tester
========================================

Teemu Kanstrén

Introduction
------------
This tutorial describes the offline optimizer for OSMO Tester.
This optimizer generates a number of tests and picks a subset that best fulfills the given coverage criteria.
Tests that do not contribute anything unique to this coverage criteria are discarded.

Example Model
-------------
The model used in this tutorial is for a Calendar application.
The code for this model can be found in the examples directory in the OSMO source tree.
At the time of this writing it is located [here](https://github.com/mukatee/osmo/tree/master/examples/src/osmo/tester/examples/calendar).
The optimizer setup for this tutorial is similarly located [here](https://github.com/mukatee/osmo/tree/master/examples/src/osmo/tester/examples/tutorial/optimizer).

The Calendar model consists of several parts. The ones used in this case:
* CalendarMeetingModel: Adds and removes meetings in the calendar.
* CalendarOracleModel: Adds checks to the test cases, performing several checks of model vs SUT state after each step.
* CalendarParticipantModel: Adds/Removes participants in the meetings (in addition to the organizer).
* CalendarTaskModel: Adds and removes tasks in the calendar.
* CalendarErrorHandlingModel: Tries to exercise the calendar with invalid input to see it is not corrupted.
* ModelState: Contains the shared test model state for all above model objects.

The following shows the CalendarMeetingModel as an example:

```java
public class CalendarMeetingModel {
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarMeetingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Description("General test setup")
  @BeforeTest
  public void setup() {
    state.init();
    scripter.reset();
    state.reset();
  }

  @TestStep("Add Meeting")
  public void addEvent() {
    User user = state.randomUser();
    Date start = state.randomStartTime();
    Date end = state.randomEndTime(start);
    ModelEvent event = state.createEvent(user, start, end);
    scripter.addEvent(event);
  }

  @Description("Someone has an existing meeting in calendar")
  @Guard("Remove Meeting")
  public boolean guardRemoveOrganizerEvent() {
    return state.hasEvents();
  }

  @TestStep("Remove Meeting")
  public void removeOrganizerEvent() {
    ModelEvent event = state.getAndRemoveOwnerEvent();
    scripter.removeEvent(event.getUser().getId(), event.getEventId());
  }
}
```

There are examples of generating tests for the Calendar in both online and offline mode in the examples directory.
For this reason, there is a base CalendarScripter class that has two variants: OnlineScripter and OfflineScripter.
These can be used either to generate test scripts for execution at a later time (offline) or to execute the tests
while they are generated (online).
OSMO Tester contains both an online test optimizer and an offline test optimizer.
However, in this case we describe the offline optimizer.

Running the basic offline generation without the optimizer can be done with something similar to:

```java
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    OSMOConfiguration config = new OSMOConfiguration();
    osmo.setConfig(config);
    config.setTestEndCondition(new Length(5));
    config.setSuiteEndCondition(new Length(5));
    ModelState state = new ModelState();
    state.setUserCount(3);
    OfflineScripter scripter = new OfflineScripter(state, "tests.html");
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    config.setFactory(factory);
    factory.add(state);
    factory.add(new CalendarMeetingModel(state, scripter));
    factory.add(new CalendarOracleModel(state, scripter));
    factory.add(new CalendarTaskModel(state, scripter));
    factory.add(new CalendarOverlappingModel(state, scripter));
    factory.add(new CalendarParticipantModel(state, scripter));
    factory.add(new CalendarErrorHandlingModel(state, scripter));
    osmo.generate(55);
    scripter.write();
    TestSuite suite = osmo.getSuite();
    FSM fsm = osmo.getFsm();
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), fsm);
    String report = html.getTraceabilityMatrix();
    TestUtils.write(report, "coverage.html");
  }
```

The line "scripter.write()" writes the tests generated into a file named "tests.html".
The HTMLCoverageReporter class is used similarly to write a coverage report into a file named "coverage.html".
In this case, the generator produces a set of randomly generated tests, which can have any coverage and typically
can have significant overlaps.
Such test generation is good for fast generation, to get quick results.
Especially in cases where the tests are fast to execute, the models evolve continously, and in cases where
the model can dynamically respond to SUT output this type of test generation and execution is very useful.
However, this is generally most useful in online testing.

The factory object in the above example is what is used by the test generator to re-create the model objects
between different test runs.
In a basic generator scenario such as the one above, this does not need anything special and thus the above
version just uses the SingleInstanceModelFactory.
This factory always gives the same object instances for every test case.
It is the same one that OSMO uses by default if none is specified and the objects are simply added to the generator.

For offline testing it is often more interesting to optimize the produced test set in some way.
An example of performing this with OSMO Tester for the calendar example is shown below:

```java
  public static void main(String[] args) throws Exception {
    long seed = Long.parseLong(args[0]);
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new CalendarFactory());
    EndCondition ec1 = new LengthProbability(1, 0.5);
    EndCondition ec2 = new LengthProbability(10, 0.2);
    EndCondition ec3 = new LengthProbability(50, 0.5);
    WeightedEndConditionSet set = new WeightedEndConditionSet(10, ec1, 100, ec2);
    set.addEndCondition(20, ec3);
    oc.setTestEndCondition(set);
    ScoreConfiguration sc = new ScoreConfiguration();
    MultiGreedy greedy = new MultiGreedy(oc, sc, seed);
    greedy.setTimeout(10);
    List<TestCase> tests = greedy.search();
    int i = 1;
    for (TestCase test : tests) {
      TestUtils.write((String)test.getAttribute("script"), "calendar-example/test"+i+".html");
      i++;
    }
    HTMLCoverageReporter html = new HTMLCoverageReporter(greedy.getFinalCoverage(), tests, greedy.getFsm());
    String report = html.getTraceabilityMatrix();
    TestUtils.write(report, "calendar-example/coverage.html");
  }
```

In this case, a specific factory object is used, called CalendarFactory. This is shown below:

```java
public class CalendarFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels here) {
    ModelState state = new ModelState();
    OfflineScripter scripter = new OfflineScripter(state, null);
    here.add(new ScriptMob(scripter));
    here.add(state);
    here.add(new ExtraState(state));
    here.add(new CalendarMeetingModel(state, scripter));
    here.add(new CalendarOracleModel(state, scripter));
    here.add(new CalendarParticipantModel(state, scripter));
    here.add(new CalendarTaskModel(state, scripter));
    here.add(new CalendarErrorHandlingModel(state, scripter));
  }
}
```

This is needed to build different instances of the model for different test cases.
The generator cannot do this by itself as it has no way of knowing how to wire all these together.
The actual optimizer running here is the MultiGreedy class shown in the main() method above.
This runs several instances of the generators in parallel, producing large sets of tests to optimize from.
It requires each generator to have separate model instances so the parallel execution does not corrupt state in the model.
Additionally, creating different instances simplifies model state management.

As shown in the initial basic offline test generation, a basic way to create the actual test scripts is to create
and collect them in the scripter, and write them to a file or set of files in the end, or to a file after each test.
However, we cannot do this in the optimizer case as it would write all the non-unique tests into the file(s) as well.
Instead, we store them in the generated test object which the optimizer preserves for the final set.
In this case, a specific model object called ScriptMob is used:

```java
public class ScriptMob {
  private final OfflineScripter scripter;
  private TestSuite suite;

  public ScriptMob(OfflineScripter scripter) {
    this.scripter = scripter;
  }

  @AfterTest
  public void storeScript() {
    suite.getCurrentTest().setAttribute("script", scripter.getScript());
  }
}
```

We can then use this when the optimizer finishes. This is shown in the main() method shown above.

The generator can be configured with various options. Some of the main ones listed below:

* Coverage weights: The ScoreConfiguration can be used to define how much weight each model element gets when
optimizing for more coverage. For example, one can set variable values to score 0 meaning unique values will not
add extra score. For example, in the calendar each start time is recorded as a variable value (it is annotated
in the model as @Variable).
* Timeouts: The time in seconds how long the optimizer runs. After it finishes an iteration it stops if this
timeout has been exceeded. Note that this is usually not the exact timeout as the iteration can go over by quite a bit.
* Population size: Defines how many tests the optimizer produces in a single iteration.
* Threshold: The minimum added coverage score to achieve in an iteration. If not achieved, the optimizer stops.
For example, one can use this with score configuration by setting variable values to score 1 and set the
threshold to 20, meaning it requires at least 20 values (or a single other highly weighted model element) to
go for another iteration.
* Listeners: It is possible to attach custom listeners to be notified about iteration status etc.

It is also possible to add custom state values for coverage (can also be weighted in score configuration).
As an example for the Calendar:

```java
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
```

This defines four extra values for custom "state" coverage.
The first one defines a set of category-partitions for the number of events processed.
The second one the same for tasks, and the third one for tasks and events at the same time.
The fourth is an example of counting event category counts separately for each executed test step.
For example, running "Remove Event" with N events in the calendar is scored as a unique hit,
 and so is "Remove Event" with 1 event in the calendar.
The same applies for all steps and their combinations of event categories in the calendar.
These are found by the generator based on the @CoverageValue annotation and the method signature.
The name for the variable taken for coverage is the method name.

Other points of interest to cover some day would be the automated reducer and pattern analyzer for
failed tests, and the online test optimizer for slowly executing tests. Someday...

The end.
--------



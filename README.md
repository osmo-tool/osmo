OSMO MBT Tool
=============

Tool for generating and executing test cases.

Test models are expressed as Java programs, which the test generator executes based on annotations defined in the model.
The test generator can be configured with different algorithms to generate tests in different ways.

The main point with this approach is to provide:
- Systematic approach to go through the specification and define it more formally in the form of a test model from
  which the tool can then be used to automatically generate tests.
- Automated generation of large number of tests for level of coverage not realistic for manual test creation.
  The set of coverage elements supported:
 - Variable values (all or category-partitions)
 - Covered steps
 - Covered step-pairs
 - Covered state values (defined by annotated methods with return values)
 - Covered state-pairs
 - Requirements (defined in model as illustrated in the example model below).

Example test model:

```java
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.listener.TracePrinter;
import osmo.tester.model.Requirements;

public class ExampleModel {
  private final Requirements req = new Requirements();
  @Variable
  private int counter = 0;

  private static final String REQ_INCREASE = "REQ_INCREASE";
  private static final String REQ_DECREASE = "REQ_DECREASE";

  public ExampleModel() {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
  }

  @BeforeTest
  public void start() {
    counter = 0;
  }

  @Guard("decrease")
  public boolean toDecreaseOrNot() {
    return counter > 1;
  }

  @TestStep("decrease")
  public void decreaseState() {
    req.covered(REQ_DECREASE);
    counter--;
    System.out.println("- " + counter);
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter >= 0;
  }

  @TestStep("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    System.out.println("+ " + counter);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    OSMOConfiguration oc = tester.getConfig();
    oc.addListener(new TracePrinter());
    oc.setTestEndCondition(new LengthProbability(10, 20, 0.2d));
    oc.setSuiteEndCondition(new Length(10));
    oc.setFactory(models -> models.add(new ExampleModel()));
    ScoreConfiguration config = new ScoreConfiguration();
    tester.generate(112);
  }
}
```

The tests can then be executed by configuring the generator such as:

```java
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new ExampleModel());
    tester.setAlgorithm(new RandomAlgorithm());
    tester.setTestEndCondition(new Length(100));
    tester.setSuiteEndCondition(new Length(100));
    tester.generate(333);
  }
```

This generates 100 tests, each with 100 steps. Each step is chosen randomly based on the set of enabled steps.

Installing
----------

Currently the suggested method is to use the Maven repository. Either as a dependency or download the jar file from the repository.
Dependency:

```xml
<dependency>
	<groupId>net.kanstren.osmo</groupId>
	<artifactId>osmotester</artifactId>
	<version>3.7.1</version>
</dependency>
```

or direct [link](http://central.maven.org/maven2/net/kanstren/osmo/osmotester/3.7.1/osmotester-3.7.1.jar)

The required core dependencies should be only Log4J2 (api and core jars).
Some of the reports use Velocity, and some of the JUnit integration uses JUnit. But those should not be strict requirements if you do not use those features.

Documentation
-------------

Some documentation exists here:

 - [User guide](https://github.com/mukatee/osmo/blob/master/osmotester/doc/osmo_guide.md)
 - [Basics tutorial] (https://github.com/mukatee/osmo/blob/master/osmotester/doc/tutorial_basics.md)
 - [Data tutorial] (https://github.com/mukatee/osmo/blob/master/osmotester/doc/tutorial_data.md)
 - [Scenarios tutorial] (https://github.com/mukatee/osmo/blob/master/osmotester/doc/tutorial_scenarios.md)
 - [Optimizer tutorial] (https://github.com/mukatee/osmo/blob/master/osmotester/doc/tutorial_optimizer.md)

License
-------

LGPL License


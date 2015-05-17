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
public class ExampleModel {
  private final Requirements req = new Requirements();
  @Variable
  private int counter = 0;

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
    return counter > 0;
  }

  @TestStep("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    out.println("+ " + counter);
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

I would recommend to download the source, and using the Ant build script under "osmotester" directory to build it.
There is also some version in the maven repo.

<dependency>
	<groupId>net.kanstren.osmo</groupId>
	<artifactId>osmotester</artifactId>
	<version>3.4.0</version>
</dependency>

or direct [link](http://central.maven.org/maven2/net/kanstren/osmo/osmotester/3.4.0/osmotester-3.4.0.jar)

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


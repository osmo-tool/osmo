OSMO MBT Tool
=============

Tool for generating and executing test cases.

Test models are expressed as Java programs, which the test generator executes based on annotations defined in the model.
The test generator can be configured with different algorithms to generate tests in different ways.

Example test model:

```java
public class ExampleModel {
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

License
-------

LGPL License


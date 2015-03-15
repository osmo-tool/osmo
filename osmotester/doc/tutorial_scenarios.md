Tutorial: Writing Scenarios
===========================

Teemu Kanstrén

Introduction
------------

This tutorial describes using scenarios with OSMO Tester using simple examples.

Startup sequence
----------------

Previously in the basic tutorial we created a model that prints “HELLO” and “WORLD” in that order.
In the data tutorial we extended this to add string text names and numerical data values to these items.
Here is a simple model with those attributes:

```java
public class HelloModel {
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");

  @BeforeTest
  public void startTest() {
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO "+names.next());
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD "+worlds.next());
  }
}
```

This is what was used to run it:

```java
public class Main {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new HelloModel());
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(2));
    tester.generate(52);
  }
}
```

And the output:

```
TEST START
HELLO
WORLD
HELLO
WORLD
HELLO
TEST END
TEST START
HELLO
WORLD
HELLO
WORLD
HELLO
TEST END
generated 2 tests.
```

There are two main parts to a scenario.
A scenario can have a startup script, and a scenario may have one or more slices defined.
If a startup script is defined, it will be execute at the beginning of each test case.
Here is one with a startup script:

```java
public class Main1 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}
```

This gives the output:

```
TEST START
HELLO teemu
WORLD mars
HELLO teemu
HELLO bob
HELLO teemu
TEST END
TEST START
HELLO teemu
WORLD mars
HELLO teemu
HELLO teemu
WORLD venus
TEST END
generated 2 tests.
```

Here all the generated tests start with the given startup sequence “hello”, “world”, “hello”.
After that, the generator is free to pick whatever it likes as there are no more constraints given.
The startup script is always executed as given.
Unless of course the rules (guards) in the model forbid the given ordering, at which point the generation fails.

After executing the startup sequence,
the scenario can define a set of slices to control what steps can be taken and how many times.
The parameter to the scenario constructor defines if the scenario should be "strict".
If the “strict” parameter is set to true, only the steps defined in the slices are allowed after the startup script.
In the example above no slices are defined, which means there would be no step to take after the initial script if
"strict" was true.

Slicing steps
-------------

Here is another scenario to demonstrate slicing:

```java
public class Main2 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    scenario.addSlice("world", 0, 1);
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(8));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}
```

This adds a slice for the “world” step, defining
“world” should appear minimum 0 times and maximum 1 time after the startup sequence is finished.
Output:

```
TEST START
HELLO teemu
WORLD mars
HELLO teemu
HELLO bob
HELLO teemu
HELLO teemu
HELLO teemu
WORLD mars
TEST END
TEST START
HELLO teemu
WORLD venus
HELLO bob
WORLD mars
HELLO teemu
HELLO bob
HELLO teemu
HELLO bob
TEST END
generated 2 tests.
```

Notice that in both tests, the startup sequence is there and there is one “world” step after startup.
We can also define the minimum number of times a step has to appear:

```java
public class Main3 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.addStartup("hello", "world", "hello");
    scenario.addSlice("world", 3, 0);
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(1));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}
```

Notice that we set the end condition for a test case to length of 1.
This means it should stop after taking a single step.
A startup sequence definition given alone would still stop after the first step (all tests would be just one “hello”).
However, since we have a scenario minimum length defined for a step (3 for “world”),
it goes on even after the end condition is met until the scenario is satisfied.
Running this then produces the following:

```
TEST START
HELLO teemu
WORLD mars
HELLO teemu
HELLO bob
HELLO teemu
HELLO teemu
HELLO teemu
WORLD mars
HELLO teemu
WORLD venus
HELLO bob
WORLD mars
TEST END
TEST START
HELLO teemu
WORLD mars
HELLO bob
WORLD mars
HELLO teemu
HELLO bob
WORLD mars
HELLO bob
HELLO teemu
HELLO bob
HELLO bob
HELLO teemu
HELLO teemu
WORLD venus
TEST END
generated 2 tests.
```

Notice how every test case again starts with the given startup sequence.
After that, the choice is up to the test generation algorithm.
However, each test case only ends after the “world” step has been taken three times as defined in the scenario.

Forbidden steps
---------------

Defining a slice with minimum and maximum of 0 is the same as not defining a slice at all.
This is because only values greater than zero are taken into account to allow to define only one of them if desired.
To completely forbid a step do the following:

```java
public class Main4 {
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new HelloModel());
    Scenario scenario = new Scenario(false);
    scenario.forbid("world");
    osmo.getConfig().setScenario(scenario);
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(55);
  }
}
```

Running this will now produce the following:

```
TEST START
HELLO teemu
HELLO teemu
HELLO bob
HELLO teemu
HELLO teemu
TEST END
TEST START
HELLO teemu
HELLO teemu
HELLO bob
HELLO teemu
HELLO bob
TEST END
generated 2 tests.
```

Here we have five steps as requested and none of them “world” since we forbid it.
That is pretty much all there is to scenarios..

The end.
--------

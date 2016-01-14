Tutorial: Basic features in OSMO Tester
=======================================

Teemu Kanstrén

Introduction
------------
This tutorial describes the basic modeling concepts of OSMO Tester using simple examples.
The reader is expected to have basic knowledge of Java programming and ability to use their own
favourite IDE such as Eclipse, IntelliJ, or Netbeans.
Most of the code shown in this tutorial should be available in the examples source package.

Hello World
-----------
The test models are in practice executable programs written in the Java programming language.
Sometimes referred to more aptly as “model programs”.
First a simple example model that just prints “HELLO” on the console:

```java
public class HelloModel {
  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
  }
}
```
So how do we run this model?
We run the OSMO Tester generator as a normal Java program and pass it the model object as an argument:

```java
public class Main {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new HelloModel());
    tester.generate(52);
  }
}
```

Now when we run this, we get:

```
HELLO
HELLO
HELLO
HELLO
HELLO
HELLO
HELLO
HELLO
generated 3 tests.
```

Notice that the generate() method takes as a parameter the seed for random values.
This is needed to produce deterministic values.
If you want to different results every time, you can use a dynamic seed such as System.currentTimeMillis().
Basically, the output shown above tells us that the generator produced 3 test cases from the model.
Since they all just print “HELLO” on the console and nothing in between,
all the printouts look like a long list where the text is merged into one single long list.
To show which "HELLO" belongs to which test, we can modify the model as:

```java
public class HelloModel2 {
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
    System.out.println("HELLO");
  }
}
```

Running the generator with this model gives this:

```
TEST START
HELLO
HELLO
HELLO
TEST END
TEST START
HELLO
HELLO
TEST END
TEST START
HELLO
HELLO
HELLO
TEST END
generated 3 tests.
```

The @BeforeTest and @AfterTest can be used for tasks such as setting up and tearing down (cleanup) generated test cases.

Controlling the length of generated test cases
----------------------------------------------

In these examples the length of the generated test cases is practically random.
This is because the generator uses a default configuration of minimum length of 1 and
ending with some probability after (10% for test, 5% for suite when writing this).
Length can also be specified explicitly by configuration:

```java
public class Main3 {
  public static void main(String[] args) {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setTestEndCondition(new Length(5));
    config.setSuiteEndCondition(new Length(2));
    config.addModelObject(new HelloModel2());
    OSMOTester tester = new OSMOTester();
    tester.setConfig(config);
    tester.generate(52);
  }
}
```

Running the example model above with this configuration now produces:

```
TEST START
HELLO
HELLO
HELLO
HELLO
HELLO
TEST END
TEST START
HELLO
HELLO
HELLO
HELLO
HELLO
TEST END
generated 2 tests.
```

OSMO Tester Javadocs describe a number of algorithms that can be used as end conditions.
Changing the ones used can provide a powerful means to control test generation for different types of variants in terms of length.
It is also possible to combine several into one:

```java
public class Main4 {
  public static void main(String[] args) {
    OSMOConfiguration config = new OSMOConfiguration();
    config.setTestEndCondition(new Length(5));
    config.setSuiteEndCondition(new Length(2));
    config.addModelObject(new HelloModel2());
    OSMOTester tester = new OSMOTester();
    tester.setConfig(config);
    tester.generate(52);
  }
}
```

This results in test cases with a minimum length of 5 and after that ending with a probability of 33%.
From the program viewpoint, it requires both conditions to be true so that length is at least 5 and
generating a random value between 0 and 1 gives a result smaller than 0.33.
The suite should have exact number of 6 test cases. The output is left as an exercise to the reader.

Of course, a set of basic end conditions such as this already exist in the OSMO Tester code base ready for use.
However, new ones can be added as required (e.g., domain specific) and as illustrated above.

Adding more test steps
----------------------

So far, the examples have focused only on one test step.
No real test case has only one possible step so let’s add another:

```java
public class HelloModel3 {
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
    System.out.println("HELLO");
  }

  @TestStep
  public void world() {
    System.out.println("WORLD");
  }
}
```

Notice the alternative notation here to define the name of the test step.
If the annotation has no parameter, the name of the method becomes the name of the test step.
Running the generator with this model results in this output:

```
TEST START
WORLD
HELLO
WORLD
WORLD
HELLO
TEST END
TEST START
WORLD
HELLO
WORLD
HELLO
HELLO
TEST END
generated 2 tests.
```

That’s nice.
But in real life things have to happen in correct order for it to make sense.
So what if we only want “WORLD” to appear if “HELLO” has already appeared?
We modify the model by adding a guard statement for the “WORLD” part:

```java
public class HelloModel4 {
  private int helloCount = 0;

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
    helloCount++;
  }

  @Guard
  public boolean allowWorld() {
    return helloCount >= 1;
  }

  @TestStep
  public void sayWorld() {
    System.out.println("WORLD");
  }
}
```

The guard could also be written as @Guard(“world”), and the step as @TestStep(“world”).
The guard is associated to the step by the name attribute.
If there is no name for the annotation, the name is parsed from the method name.
For guard it is the part forward from first uppercase letter.
For step it is the whole method name as is as shown before.
Matching elements by their names is case insensitive.

For the first time it is also visible here how the model has state that guides test generation.
In this case the state is the number of times “HELLO” has been printed.
The state is also reset at the beginning of each test generation or otherwise the condition
would only apply in the first generated test case
(which would increment the value to >= 1 and the rest of the tests would always see this as the state).
The resulting output:

```
TEST START
HELLO
HELLO
WORLD
WORLD
HELLO
TEST END
TEST START
HELLO
HELLO
WORLD
HELLO
HELLO
TEST END
generated 2 tests.
```

Notice how in this case each test cases starts with “HELLO” as we wanted.
But what if we want to have “HELLO” and “WORLD” to appear always in pairs?
We can add another state variable for the number of “WORLD”’s and also a guard for “HELLO”:

```java
public class HelloModel5 {
  private int helloCount = 0;
  private int worldCount = 0;

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    worldCount = 0;
    System.out.println("TEST START");
  }
  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @Guard("hello")
  public boolean hiOrNot() {
    return helloCount == worldCount;
  }
  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
    helloCount++;
  }

  @Guard
  public boolean gWorld() {
    return helloCount > worldCount;
  }
  @TestStep
  public void sayWorld() {
    System.out.println("WORLD");
    worldCount++;
  }
}
```

It is also possible to define a ModelFactory instance,
which will be used to re-create all model objects between test cases with different seeds.
When using a factory, the reset code above is not necessary when the object is re-created.

In the listing above, “HELLO” can appear only if an equal number of “WORLD”’s has been printed.
“WORLD” can only appear if more “HELLO”’s have been printed than “WORLD”’s.
Thus this will always print the text is pairs, with “HELLO” preceding “WORLD”:

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

And what if we wanted always to have “WORLD” as the last word?
We could just modify the @AfterTest method to compare that helloCount and worldCount are equal and if not,
print out the last “WORLD”.
The @LastStep annotation also provides similar function.

The end.
--------



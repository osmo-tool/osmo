package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class WeightedModel1 {
  @Transition(name = "bob2", weight = 2)
  public void hello() {

  }

  @Transition(name = "bob3", weight = 3)
  public void world() {

  }

  @Transition(name = "bob4", weight = 4)
  public void xyz() {

  }

  @Transition(name = "bob1", weight = 1)
  public void abc() {

  }

/*
  expected flow:

step 1:
bob4 = 1/4
bob3 = 1/3
bob2 = 1/2
bob1 = 1/1
->bob4

step 2:
bob4 = 2/4
bob3 = 1/3
bob2 = 1/2
bob1 = 1/1
->bob3

step 3&4:
bob4 = 2/4
bob3 = 2/3
bob2 = 1/2
bob1 = 1/1
->bob2,4

step 5:
bob4 = 3/4
bob3 = 2/3
bob2 = 2/2
bob1 = 1/1
->bob3

step 6:
bob4 = 3/4
bob3 = 3/3
bob2 = 2/2
bob1 = 1/1
->bob4

step 7-10:
bob4 = 3/4
bob3 = 3/3
bob2 = 2/2
bob1 = 1/1
->bob1,bob2,bob3,bob4

   */
}

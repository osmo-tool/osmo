package osmo.tester.model;

/**
 * One could add parameter "boolean exploration" for the createModelObjects() method to allow for customized state cloning.
 * However, this would still not solve everything as it would also require cloning all the executed test steps, the values
 * in those steps, and all other attributes for them.
 * So, currently one must still expect that between the tests the state of the SUT can be reset and repeats are done.
 * 
 * @author Teemu Kanstren */
public interface ModelFactory {
  public TestModels createModelObjects();
}

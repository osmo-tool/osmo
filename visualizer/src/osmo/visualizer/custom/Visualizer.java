package osmo.visualizer.custom;

public interface Visualizer {
  public void testSuiteStart(String testSuiteID);

  public void testSuiteStop(int testCount);

  public void testCaseStart(String testCaseID);

  public void testCaseStop(String testCaseID);

  public void stateTransition(Object oldState, Object newState, String message);

  public void info(String text, String relatedState);

  public void visualize();
}

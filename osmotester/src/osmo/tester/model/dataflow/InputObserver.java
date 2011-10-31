package osmo.tester.model.dataflow;

/** @author Teemu Kanstren */
public interface InputObserver<T> {
  public void value(String variable, T value);
}

package osmo.tester.model.dataflow;

/** @author Teemu Kanstren */
public class TemplateWrapper {
  private final SearchableInput input;

  public TemplateWrapper(SearchableInput input) {
    this.input = input;
  }

  @Override
  public String toString() {
    return ""+input.next();
  }
}

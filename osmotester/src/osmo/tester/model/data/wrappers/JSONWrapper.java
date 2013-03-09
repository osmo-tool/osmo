package osmo.tester.model.data.wrappers;

import osmo.tester.model.data.DataType;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.model.data.Text;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueRangeSet;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class JSONWrapper extends ValueSet<String> {
  public JSONWrapper(SearchableInput input) {
    if (input instanceof Text) {
      initString(input);
    } else if (input instanceof ValueRange || input instanceof ValueRangeSet) {
      initDouble(input);
    } else {
      //we default to strings since can't figure out what else to do..
      initString(input);
    }
  }

  public JSONWrapper(SearchableInput input, DataType type) {
    switch (type) {
      case DOUBLE:
        initDouble(input);
        break;
      case INT:
        initInt(input);
        break;
      case LONG:
        initLong(input);
        break;
      case BOOLEAN:
        initBoolean(input);
        break;
      case STRING:
        initString(input);
        break;
      default:
        break;
    }
  }

  public String next() {
    return super.next();
  }

  private void initDouble(SearchableInput input) {
  }

  private void initString(SearchableInput input) {
  }

  private void initBoolean(SearchableInput input) {
  }

  private void initLong(SearchableInput input) {
  }

  private void initInt(SearchableInput input) {
  }
}

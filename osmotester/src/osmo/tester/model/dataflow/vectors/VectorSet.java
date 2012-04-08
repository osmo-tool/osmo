package osmo.tester.model.dataflow.vectors;

import osmo.tester.model.dataflow.ValueSet;

import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class VectorSet extends ValueSet<String> {
  public static final String LDAP = "ldap";
  public static final String SQL_ACTIVE = "sql_active";
  public static final String SQL_PASSIVE = "sql_passive";
  public static final String XML = "xml";
  public static final String XPATH = "xpath";
  public static final String XSS = "xss";
  
  private final String name;

  public VectorSet(String name) {
    this.name = name;
    String text = getResource(getClass(), name+".txt");
    text = unifyLineSeparators(text, "\n");
    String[] lines = text.split("\n");
    for (String line : lines) {
      if (line.startsWith("#")) {
        continue;
      }
      if (line.trim().length() == 0) {
        continue;
      }
      add(line);
    }
  }
}

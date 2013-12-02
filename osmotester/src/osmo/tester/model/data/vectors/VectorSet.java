package osmo.tester.model.data.vectors;

import osmo.tester.model.data.ValueSet;

import static osmo.common.TestUtils.*;

/** 
 * Set of fuzz vectors for different types of data.
 * 
 * @author Teemu Kanstren 
 */
public class VectorSet extends ValueSet<String> {
  public static final String LDAP = "owasp/ldap";
  public static final String SQL_ACTIVE = "owasp/sql_active";
  public static final String SQL_PASSIVE = "owasp/sql_passive";
  public static final String XML = "owasp/xml";
  public static final String XPATH = "owasp/xpath";
  public static final String XSS = "owasp/xss";

  public final String name;

  public VectorSet(Class clazz, String name) {
    this.name = name;
    String text = getResource(clazz, name + ".txt");
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

  public VectorSet(String name) {
    this(VectorSet.class, name);
  }
}

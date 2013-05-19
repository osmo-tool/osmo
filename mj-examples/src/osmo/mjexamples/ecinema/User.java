package osmo.mjexamples.ecinema;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class User {
  public String name;
  public String password;
  public Set<String> tickets = new LinkedHashSet<>();

  User(String name0, String password0) {
    name = name0;
    password = password0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (name != null ? !name.equals(user.name) : user.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}

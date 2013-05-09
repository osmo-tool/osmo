package osmo.mjexamples.ecinema;

import java.util.HashSet;
import java.util.Set;

public class User
{
  public String name;
  public String password;
  public Set<String> tickets = new HashSet<String>();

  User(String name0, String password0)
  {
    name = name0;
    password = password0;
  }
}

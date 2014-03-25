package osmo.tester.examples.calendar.testmodel;

/**
 * @author Teemu Kanstren
 */
public class User {
  private static int nextId = 1;
  private final int id;
  private final String name;

  public User(String name) {
    this.id = nextId++;
    this.name = name;
  }

  /**
   * Allows creating a user with a given id, for testing "bad id" use in scripts.
   * For valid user creation, use the other constructor..
   * 
   * @param name User name.
   * @param id Given id. 
   */
  public User(String name, int id) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return "User"+id;
  }

  public String getName() {
    return name;
  }
  
  public static void resetCounter() {
    nextId = 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (id != user.id) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}

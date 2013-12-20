package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class GroupModel2 {
  private boolean loggedIn = false;
  private boolean hello = false;
  private PrintStream ps;

  public GroupModel2(PrintStream ps) {
    this.ps = ps;
  }

  @BeforeTest
  public void reset() {
    loggedIn = false;
    hello = false;
    ps.println("--new test--");
  }
  
  @TestStep(name="login")
  public void login() {
    loggedIn = true;
    ps.println("login");
  }

  @TestStep(name="hello", group="logged in")
  public void hello() {
    hello = true;
    ps.println("hello");
  }

  @TestStep(name="world", group="logged in")
  public void world() {
    ps.println("world");
  }
  
  @Guard("logged in")
  public boolean loggedInGuard() {
    return loggedIn;
  }

  @Guard("world")
  public boolean worldGuard() {
    return hello;
  }

  @Pre("logged in")
  public void loggedInPre() {
    ps.println("pre-logged");
  }

  @Post("logged in")
  public void loggedInPost() {
    ps.println("post-logged");
  }

  @Pre("world")
  public void worldPre() {
    ps.println("pre-world");
  }

  @Post("world")
  public void worldPost() {
    ps.println("post-world");
  }
}

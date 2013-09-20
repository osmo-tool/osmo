package osmo.mjexamples.ecinema;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.TracePrinter;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.Requirements;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A model of a simple eCinema. Ported from ModelJUnit examples.
 * That is, a web-based system for selecting and buying movie tickets.
 * <p/>
 * For simplicity, Movie objects and Ticket objects are just
 * modelled as strings.  Note that missing usernames and passwords
 * are modelled as empty strings ("").
 *
 * @author marku, ported by teemu kanstren
 */
public class ECinema {
  /**
   * Each of these states models a separate web page.
   * (Except for terminal, which means that the session is finished.)
   */
  public enum State {
    welcome, register, displayTickets, terminal
  }

  public State state = State.welcome;
  /** This is the most recent message that has been output. */
  public String message = null;
  public User currentUser = null;
  /** This maps each username to the User object. */
  public Map<String, User> allUsers = new LinkedHashMap<>();
  public Showtime[] showtimes = new Showtime[2];
  private boolean done = false;
  //  private ValueSet<User> users = new ValueSet<>();
//  private ValueSet<String> invalidUsers = new ValueSet<>("", "bob");
//  private ValueSet<String> invalidPasswords = new ValueSet<>("", "bad");
  @RequirementsField
  private Requirements req = new Requirements();

  @CoverageValue
  public String getState(TestCaseStep step) {
    return state.toString() + ((currentUser == null) ? "" : "_" + currentUser.name);
  }

  @BeforeTest
  public void reset() {
    state = State.welcome;
    message = null;
    currentUser = null;
    done = false;

    // one registered user
    allUsers = new LinkedHashMap<>();
//    users.clear();
    User eric = new User("ERIC", "ETO");
    allUsers.put("ERIC", eric);
//    users.add(eric);

    // two showtimes
    showtimes[0] = new Showtime();
    showtimes[0].dateTime = Showtime.DATE_CORRECT;
    showtimes[0].movie = "Star Wars I";
    showtimes[0].tickets = new LinkedHashSet<>();
    for (int i = 1 ; i <= 3 ; i++) {
      showtimes[0].tickets.add("ticket" + i);
    }
    showtimes[0].ticketsLeft = showtimes[0].tickets.size();

    showtimes[1] = new Showtime();
    showtimes[1].dateTime = Showtime.DATE_CORRECT;
    showtimes[1].movie = "Star Wars II";
    showtimes[1].tickets = new LinkedHashSet<>();
    for (int i = 4 ; i <= 10 ; i++) {
      showtimes[1].tickets.add("ticket" + i);
    }
    showtimes[1].ticketsLeft = showtimes[1].tickets.size();
  }

  @Guard("login")
  public boolean loginGuard() {
    return state == State.welcome;
  }

  @TestStep(name="Login Empty", group="login")
  public void loginEmpty() {
    login("", "ETO");
  }

  @TestStep(name="Login Eric OK", group="login")
  public void loginEricOk() {
    login("ERIC", "ETO");
  }

  @TestStep(name="Login Eric BAD", group="login")
  public void loginEricBad() {
    login("ERIC", "ACH");
  }

  @TestStep(name="Login Amandine OK", group="login")
  public void loginAmandineOk() {
    login("AMANDINE", "ACH");
  }

  @TestStep(name="Login Amandine Empty", group="login")
  public void loginAmandineEmpty() {
    login("AMANDINE", "");
  }

  public void login(String userName, String userPassword) {
    if (userName.equals("")) {
      message = "EMPTY_USERNAME";
      req.covered("@REQ: CIN_031 @");
    } else if (userPassword.equals("")) {
      message = "EMPTY_PASSWORD";
      req.covered("@REQ: CIN_032 @");
    } else if (!allUsers.containsKey(userName)) {
      message = "UNKNOWN_USER_NAME_PASSWORD";
      req.covered("@REQ: CIN_033 @");
    } else {
      User user_found = allUsers.get(userName);
      if (user_found.password.equals(userPassword)) {
        currentUser = user_found;
        message = "WELCOME";
        req.covered("@REQ: CIN_030 @");
      } else {
        message = "WRONG_PASSWORD";
        req.covered("@REQ: CIN_034 @");
      }
    }
  }

  @Guard("Logout")
  public boolean logoutGuard() {
    return currentUser != null && (state == State.welcome || state == State.displayTickets);
  }

  @TestStep("Logout")
  public void logout() {
    message = "BYE";
    currentUser = null;
    req.covered("@REQ: CIN_100@");
    state = State.welcome;
  }

  @Guard("Buy ticket for show 1")
  public boolean buyTicketShow1Guard() {
    return buyTicketGrd(showtimes[0]);
  }

  @TestStep("Buy ticket for show 1")
  public void buyTicketShow1() {
    buyTicket(showtimes[0]);
  }

  @Guard("Buy ticket for show 2")
  public boolean buyTicketShow2Guard() {
    return buyTicketGrd(showtimes[1]);
  }

  @TestStep("Buy ticket for show 2")
  public void buyTicketShow2() {
    buyTicket(showtimes[1]);
  }

  public boolean buyTicketGrd(Showtime shtime) {
    return state == State.welcome
            && shtime.ticketsLeft >= 1
            && shtime.dateTime == Showtime.DATE_CORRECT;
  }

  /** Buy one ticket for current user, from the given shtime. */
  public void buyTicket(Showtime shtime) {
    if (currentUser == null) {
      message = "LOGIN_FIRST";
      req.covered("@REQ: CIN_061@");
    } else {
      if (shtime.ticketsLeft == 1) {
        message = "NO_MORE_TICKET";
        shtime.buyButtonActive = false;
        req.covered("@REQ: CIN_062@");
      } else {
        message = null;
        shtime.buyButtonActive = true;
        req.covered("@REQ: CIN_060@");
      }
      shtime.clearAllButtonActive = currUsersTickets(shtime).size() >= 1;
      // search for an unallocated ticket [Nasty!]
      // Note: could simplify this by keeping an allocated flag in each ticket
      String free_ticket_found = null;
      for (String ticket : shtime.tickets) {
        free_ticket_found = ticket;
        for (User user : allUsers.values()) {
          if (user.tickets.contains(ticket)) {
            free_ticket_found = null;
            break;
          }
        }
        if (free_ticket_found != null)
          break;
      }
      assert free_ticket_found != null;
      currentUser.tickets.add(free_ticket_found);
      shtime.ticketsLeft--;
    }
  }

  @Guard("Delete ticket show 1")
  public boolean deleteTicketShow1Guard() {
    boolean b = deleteTicketGrd(showtimes[0]);
    return b;
  }

  @TestStep("Delete ticket show 1")
  public void deleteTicketShow1() {
    deleteTicket(showtimes[0]);
  }

  @Guard("Delete ticket show 2")
  public boolean deleteTicketShow2Guard() {
    return deleteTicketGrd(showtimes[1]);
  }

  @TestStep("Delete ticket show 2")
  public void deleteTicketShow2() {
    deleteTicket(showtimes[1]);
  }

  public boolean deleteTicketGrd(Showtime shtime) {
    if (state != State.displayTickets) {
      return false;
    }
    if (currentUser == null) {
      return false;
    }
    boolean enable = !currUsersTickets(shtime).isEmpty();
    return enable;
  }

  public void deleteTicket(Showtime shtime) {
    Set<String> shtickets = currUsersTickets(shtime);
    String ticket = shtickets.iterator().next();  // choose one to delete
    shtime.clearAllButtonActive = shtickets.size() > 1;
    shtime.buyButtonActive = true;
    currentUser.tickets.remove(ticket);
    req.covered("@REQ: CIN_090@");
  }

  @Guard("Delete all tickets show 1")
  public boolean deleteAllTicketsShow1Guard() {
    return deleteAllTicketsGrd(showtimes[0]);
  }

  @TestStep("Delete all tickets show 1")
  public void deleteAllTicketsShow1() {
    deleteAllTickets(showtimes[0]);
  }

  @Guard("Delete all tickets show 2")
  public boolean deleteAllTicketsShow2Guard() {
    return deleteAllTicketsGrd(showtimes[1]);
  }

  @TestStep("Delete all tickets show 2")
  public void deleteAllTicketsShow2() {
    deleteAllTickets(showtimes[1]);
  }

  public boolean deleteAllTicketsGrd(Showtime shtime) {
    return state == State.displayTickets
            && shtime.clearAllButtonActive;
  }

  public void deleteAllTickets(Showtime shtime) {
    shtime.ticketsLeft += currUsersTickets(shtime).size();
    currentUser.tickets.removeAll(currUsersTickets(shtime));
    shtime.clearAllButtonActive = false;
    req.covered("@REQ: CIN_080@");
  }

  @Guard("Go to register")
  public boolean gotoRegisterGuard() {
    return state == State.welcome;
  }

  @TestStep("Go to register")
  public void gotoRegister() {
    state = State.register; /*@REQ: CIN_010 @*/
  }

  @Guard("register")
  public boolean registerAmandineGuard() {
    return state == State.register;
  }

  @TestStep(name = "Register Amandine", group = "register")
  public void registerAmandine() {
    reg("AMANDINE", "ACH");
  }

  @TestStep(name = "Register Eric", group = "register")
  public void registerEric() {
    reg("ERIC", "ACH");
  }

  @TestStep(name = "Register Empty", group = "register")
  public void registerEmpty() {
    reg("", "ACH");
  }

  public void reg(String userName, String userPassword) {
    if (userName.equals("")) {
      message = "EMPTY_USERNAME";
      req.covered("@REQ: CIN_020@");
      //FIXED here
    } else if (allUsers.containsKey(userName)) {
      message = "EXISTING_USER_NAME";
      req.covered("@REQ: CIN_040@");
    } else {
      User newUser = new User(userName, userPassword);
      allUsers.put(userName, newUser);
      currentUser = newUser;
      message = "WELCOME";
      req.covered("@REQ: CIN_050@");
      state = State.welcome;
    }
  }

  @Guard("Display tickets")
  public boolean displayTicketsGuard() {
    return state == State.welcome;
  }

  @TestStep("Display tickets")
  public void displayTickets() {
    if (currentUser == null) {
      message = "LOGIN_FIRST";
      req.covered("@REQ: CIN_063@");
      // and stay in the welcome state
    } else {
      message = null;
      req.covered("@REQ: CIN_070@");
      state = State.displayTickets;
    }
  }

  @Guard("Back")
  public boolean backGuard() {
    return state == State.displayTickets;
  }

  @TestStep("Back")
  public void back() {
    state = State.welcome;
  }

  @Guard("Close")
  public boolean closeGuard() {
    // all tickets have been returned!
    return state != State.terminal
            && showtimes[0].ticketsLeft == 3
            && showtimes[1].ticketsLeft == 7;
  }

  @TestStep("Close")
  public void close() {
    message = null;
    currentUser = null;
    req.covered("@REQ: CIN_110@");
    state = State.terminal;
    done = true;
  }

  @EndCondition
  public boolean done() {
    return done;
  }

  /** the current user's tickets for the given show time. */
  protected Set<String> currUsersTickets(Showtime shtime) {
    Set<String> result = new LinkedHashSet<>(currentUser.tickets);
    result.retainAll(shtime.tickets);
    return result;
  }

//  public static void main(String[] args) throws FileNotFoundException {
//    Tester tester = new RandomTester(new ECinema());
//    // The guards make this a more difficult graph to explore, but we can
//    // increase the default maximum search to complete the exploration.
//    GraphListener graph = tester.buildGraph(100000);
//    graph.printGraphDot("ecinema.dot");
//    CoverageMetric trans = tester.addCoverageMetric(new TransitionCoverage());
//    CoverageMetric trpairs = tester.addCoverageMetric(new TransitionPairCoverage());
//    CoverageMetric states = tester.addCoverageMetric(new StateCoverage());
//    CoverageMetric actions = tester.addCoverageMetric(new ActionCoverage());
//    tester.addListener("verbose");
//    // this illustrates how to generate tests upto a given level of coverage.
//    int steps = 0;
//    while (actions.getPercentage() < 100 /* || steps < 1000*/) {
//      tester.generate();
//      steps++;
//    }
//    System.out.println("Generated " + steps + " steps.");
//    tester.printCoverage();
//  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setAlgorithm(new BalancingAlgorithm());
    tester.addListener(new TracePrinter());
    tester.addModelObject(new ECinema());
    tester.setSuiteEndCondition(new Length(200));
    tester.setTestEndCondition(new LengthProbability(10, 0.2d));
    tester.generate(44);
  }
}

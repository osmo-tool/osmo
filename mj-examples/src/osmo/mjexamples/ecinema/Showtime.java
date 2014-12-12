package osmo.mjexamples.ecinema;

import java.util.HashSet;
import java.util.Set;

public class Showtime
{
  public static final int DATE_CORRECT = 1; // not really used yet

  public int dateTime = DATE_CORRECT;
  public boolean buyButtonActive = false;
  public boolean clearAllButtonActive = false;
  public String movie;
  public Set<String> tickets = new HashSet<String>();
  public int ticketsLeft;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Showtime showtime = (Showtime) o;

    if (movie != null ? !movie.equals(showtime.movie) : showtime.movie != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return movie != null ? movie.hashCode() : 0;
  }
}

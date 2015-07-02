package osmo.common;

/**
 * TODO: remove this
 *
 * @author Teemu Kanstren
 */
public class OSMOException extends RuntimeException {
  public OSMOException() {
  }

  public OSMOException(String message) {
    super(message);
  }

  public OSMOException(String message, Throwable cause) {
    super(message, cause);
  }

  public OSMOException(Throwable cause) {
    super(cause);
  }
}

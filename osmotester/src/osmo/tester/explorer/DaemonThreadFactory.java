package osmo.tester.explorer;

import java.util.concurrent.ThreadFactory;

/** 
 * Given to thread pools to make them produce daemon threads.
 * 
 * @author Teemu Kanstren 
 */
public class DaemonThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setDaemon(true);
    return t;
  }
}

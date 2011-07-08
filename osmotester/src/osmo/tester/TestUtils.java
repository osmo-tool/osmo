/*
 * Copyright (C) 2010-2011 VTT Technical Research Centre of Finland.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package osmo.tester;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Teemu Kanstren
 */
public class TestUtils {
  private static Random random = new Random(100);
  public static String ln = System.getProperty("line.separator");

  public static int cInt() {
    return (int) Math.round(cDouble());
  }

  public static int cInt(int min, int max) {
    return (int) Math.round(cDouble(min, max));
  }

  public static float cFloat() {
    return (float) Math.round(cDouble());
  }

  public static float cFloat(float min, float max) {
    return (float) Math.round(cDouble(min, max));
  }

  public static long cLong() {
    return (long) Math.round(cDouble());
  }

  public static long cLong(long min, long max) {
    return (long) Math.round(cDouble(min, max));
  }

  public static byte cByte() {
    return (byte) Math.round(cDouble());
  }

  public static byte cByte(byte min, byte max) {
    return (byte) Math.round(cDouble(min, max));
  }

  public static char cChar() {
    return (char) Math.round(cDouble());
  }

  public static char cChar(char min, char max) {
    return (char) Math.round(cDouble(min, max));
  }

  public static double cDouble() {
    double min = Integer.MIN_VALUE;
    double max = Integer.MAX_VALUE;
    return cDouble(min, max);
  }

  public static double cDouble(double min, double max) {
    double diff = max-min;
    double rnd = random.nextDouble();
    rnd *= diff;
    rnd += min;
    return rnd;
  }

  public static int oneOf(int[] array) {
    return array[cInt(0, array.length-1)];
  }

  public static <T> T oneOf(T[] array) {
    return array[cInt(0, array.length-1)];
  }

  public static <T> T oneOf(Collection<T> array) {
    List<T> list = new ArrayList<T>(array);
    return list.get(cInt(0, array.size()-1));
  }

  public static <T extends Number> T minOf(Collection<T> array) {
    T smallest = null;
    for (T t : array) {
      if (smallest == null || t.doubleValue() < smallest.doubleValue()) {
        smallest = t;
      }
    }
    return smallest;
  }

  public static String getThreadInfo() {
    ThreadMXBean tb = ManagementFactory.getThreadMXBean();
    long[] ids = tb.getAllThreadIds();
    ThreadInfo[] infos = tb.getThreadInfo(ids, 5);
    StringBuilder builder = new StringBuilder("Information for available threads:"+ln);
    for (ThreadInfo info : infos) {
      builder.append("Thread").append(ln);
      builder.append("-name=").append(info.getThreadName()).append(ln);
      builder.append("-state=").append(info.getThreadState()).append(ln);
      builder.append("-stacktrace (5 elements):").append(ln);
      StackTraceElement[] trace = info.getStackTrace();
      for (StackTraceElement line : trace) {
        builder.append("--").append(line).append(ln);
      }
    }
    return builder.toString();
  }

  public static String getResource(Class c, String name) {
    InputStream is = c.getResourceAsStream(name);
    StringBuilder text = new StringBuilder();
    Scanner scanner = new Scanner(is, "UTF-8");
    try {
      while (scanner.hasNextLine()){
        text.append(scanner.nextLine());
        text.append("\n");
      }
    }
    finally{
      scanner.close();
    }
    return text.toString();
  }
}

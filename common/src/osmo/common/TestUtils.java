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

package osmo.common;

import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Utility classes to help in test modeling. It is initialized with a standard seed value for random numbers in order
 * to provide deterministic test generation.
 *
 * @author Teemu Kanstren
 */
public class TestUtils {
  /**
   * Used for random number generation, practically also shared in OSMOTester in many places.
   * {@see OSMOTester} and the setRandom method in it.
   */
  private static Random random = new Random();
  public static String ln = System.getProperty("line.separator");

  /**
   * Allows sharing the random number generator all over OSMO.
   *
   * @return The used random generator.
   */
  public static Random getRandom() {
    return random;
  }

  /**
   * Allows setting the random number generator to users own configuration.
   * See OSMOTester class and the setRandom() method in it for more info.
   *
   * @param random The new random generator.
   */
  public static void setRandom(Random random) {
    TestUtils.random = random;
  }

  /**
   * @return A random value.
   */
  public static int cInt() {
    return random.nextInt();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random integer between the given bounds, bounds included.
   */
  public static int cInt(int min, int max) {
    int diff = max - min + 1;
    int rnd = random.nextInt(diff);
    rnd += min;
    return rnd;
  }

  /**
   * @return A random value.
   */
  public static float cFloat() {
    return random.nextFloat();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static float cFloat(float min, float max) {
    float diff = max - min;
    float rnd = random.nextFloat() * diff;
    rnd += min;
    return rnd;
  }

  /**
   * @return A random value.
   */
  public static long cLong() {
    return random.nextLong();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static long cLong(long min, long max) {
    long diff = max - min + 1;
    return min + (long) (random.nextDouble() * diff);
  }

  /**
   * @return A random value.
   */
  public static byte cByte() {
    return (byte) cInt(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static byte cByte(byte min, byte max) {
    return (byte) cInt(min, max);
  }

  /**
   * @return A random value.
   */
  public static char cChar() {
    return (char) cInt(Character.MIN_VALUE, Character.MAX_VALUE);
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static char cChar(char min, char max) {
    return (char) cInt(min, max);
  }

  /**
   * @return A random value.
   */
  public static double cDouble() {
    return random.nextDouble();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static double cDouble(double min, double max) {
    double diff = max - min;
    double rnd = random.nextDouble();
    rnd *= diff;
    rnd += min;
    return rnd;
  }

  public static int rawWeightedRandomFrom(List<Integer> weights) {
    Collections.sort(weights);
    List<Integer> totals = new ArrayList<Integer>();
    int total = 0;
    for (Integer weight : weights) {
      if (weight <= 0) {
        throw new IllegalArgumentException("Weight must be > 0. Was "+weight+".");
      }
      total += weight;
      totals.add(total);
    }
//    System.out.println("weights:"+totals+" total:"+total);
    return sumWeightedRandomFrom(totals);
  }

  public static int sumWeightedRandomFrom(List<Integer> summedTotals) {
    int total = summedTotals.get(summedTotals.size()-1);
    int target = cInt(1, total);
//    System.out.println("target:"+target);
    int choice = Collections.binarySearch(summedTotals, target);
    if (choice < 0) {
      //Java binary search returns negative index values if there is no direct match, with additional -1 added on top
      choice = choice * -1; //make it positive again
      choice = choice -1; //remove the -1 (with after previous line is +1)
    }
    return choice;
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static int oneOf(int[] array) {
    return array[cInt(0, array.length - 1)];
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static <T> T oneOf(T[] array) {
    return array[cInt(0, array.length - 1)];
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static <T> T oneOf(Collection<T> array) {
    List<T> list = new ArrayList<T>(array);
    return list.get(cInt(0, array.size() - 1));
  }

  /**
   * Picks the minimum number from the given collection. The items in the collection must be types of {@link Number}.
   *
   * @param array The list of items where to pick the value from.
   * @return The minimum numeric value from the given collection.
   */
  public static <T extends Number> T minOf(Collection<T> array) {
    T smallest = null;
    for (T t : array) {
      if (smallest == null || t.doubleValue() < smallest.doubleValue()) {
        smallest = t;
      }
    }
    return smallest;
  }

  /**
   * Provides information on all the threads in the current JVM. Useful for debugging.
   *
   * @return Formatted string with thread names, states and 5 element stack traces.
   */
  public static String getThreadInfo() {
    ThreadMXBean tb = ManagementFactory.getThreadMXBean();
    long[] ids = tb.getAllThreadIds();
    ThreadInfo[] infos = tb.getThreadInfo(ids, 5);
    StringBuilder builder = new StringBuilder("Information for available threads:" + ln);
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

  /**
   * Reads a string resource from the resource path (=classpath) of the given class.
   *
   * @param c    The that defines where we look for the String.
   * @param name The name of the resource containing the string.
   * @return The String representation of the resource, newlines represented by '\n'.
   */
  public static String getResource(Class c, String name) {
    InputStream is = c.getResourceAsStream(name);
    StringBuilder text = new StringBuilder();
    Scanner scanner = new Scanner(is, "UTF-8");
    try {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        text.append(line);
        if (scanner.hasNextLine()) {
          text.append("\n");
        }
      }
    } finally {
      scanner.close();
    }
    return text.toString();
  }

  /**
   * Unifies line separators in given string by replacing all found instances with the given string.
   * The set of replaces separators includes \r\n, \r and \n.
   *
   * @param toUnify The string to unify.
   * @param ls      Line separator characters.
   * @return Same as input but with replaced line separators.
   */
  public static String unifyLineSeparators(String toUnify, String ls) {
    char[] chars = toUnify.toCharArray();
    StringBuilder sb = new StringBuilder(toUnify.length());
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      switch (c) {
        case '\n':
          sb.append(ls);
          break;
        case '\r':
          sb.append(ls);
          if (chars.length >= i && chars[i + 1] == '\n') {
            i++;
          }
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }

  //TODO: add tests
  public static String formatXml(String xml) throws TransformerException {
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", 2);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (Exception e) {
      throw new RuntimeException(e); // simple exception handling, please review it
    }
  }
}

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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
  private static Randomizer random = new Randomizer();
  public static String ln = System.getProperty("line.separator");

  /**
   * Allows sharing the random number generator all over OSMO.
   *
   * @return The used random generator.
   */
  public static Randomizer getRandom() {
    return random;
  }

  /**
   * Allows setting the random number generator to users own configuration.
   *
   * @param random The new random generator.
   */
  public static void setRandom(Randomizer random) {
    TestUtils.random = random;
  }

  /**
   * Creates a new Random value generator initialized with the given seed value.
   *
   * @param seed Seed for the new random generator.
   */
  public static void setSeed(long seed) {
    TestUtils.random.setSeed(seed);
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
    return random.nextInt(min, max);
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
    return random.nextFloat(min, max);
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
    return random.nextLong(min, max);
  }

  /**
   * @return A random value.
   */
  public static byte cByte() {
    return random.nextByte();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static byte cByte(byte min, byte max) {
    return random.nextByte(min, max);
  }

  /**
   * @return A random value.
   */
  public static char cChar() {
    return random.nextChar();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public static char cChar(char min, char max) {
    return random.nextChar(min, max);
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
    return random.nextDouble(min, max);
  }

  /**
   * @see Randomizer
   *
   * @param weights see same method in Randomizer.
   * @return see same method in Randomizer.
   */
  public static int rawWeightedRandomFrom(List<Integer> weights) {
    return random.rawWeightedRandomFrom(weights);
  }

  /**
   * @see Randomizer
   *
   * @param summedTotals see same method in Randomizer.
   * @return see same method in Randomizer.
   */
  public static int sumWeightedRandomFrom(List<Integer> summedTotals) {
    return random.sumWeightedRandomFrom(summedTotals);
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static int oneOf(int[] array) {
    return random.oneOf(array);
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static <T> T oneOf(T[] array) {
    return random.oneOf(array);
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public static <T> T oneOf(Collection<T> array) {
    return random.oneOf(array);
  }

  /**
   * Picks the minimum number from the given collection. The items in the collection must be types of {@link Number}.
   *
   * @param array The list of items where to pick the value from.
   * @return The minimum numeric value from the given collection.
   */
  public static <T extends Number> T minOf(Collection<T> array) {
    return random.minOf(array);
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
    return getResource(is);
  }

  public static String getResource(InputStream in) {
    StringBuilder text = new StringBuilder();
    try (Scanner scanner = new Scanner(in, "UTF-8")) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        text.append(line);
        if (scanner.hasNextLine()) {
          text.append("\n");
        }
      }
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

  /**
   * Takes given XML and indents everything with 2 spaces.
   * 
   * @param xml The XML to format in text format.
   * @return Same XML but formatted with indents of 2 spaces.
   * @throws TransformerException If it all breaks up.
   */
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

  /**
   * Writes the given text to a file with the given name. 
   * The filename should be a valid path given the working directory.
   * IOExceptions are re-thrown as RuntimeExceptions.
   * 
   * @param text The text to write.
   * @param fileName The file name and path to write it to.
   */
  public static void write(String text, String fileName) {
    File file = new File(fileName);
    File parent = file.getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(text.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to write to file:"+fileName, e);
    }
  }

  /**
   * Provide a list of all files of given type in given directory.
   * Type is identified by suffix. For example type "png" gives list of files ending with ".png".
   * 
   * @param dir  The directory where to look for the files.
   * @param type The type of files to look for.
   * @param fullPath If true, returns full path of files. Otherwise just the file name.
   * @return The list of files found.
   */
  public static List<String> listFiles(String dir, String type, boolean fullPath) {
    File folder = new File(dir);
    File[] directoryList = folder.listFiles();
    List<String> files = new ArrayList<>();

    for (File file : directoryList) {
      if (file.isFile()) {
        String name = file.getName();
        if (name.endsWith(type)) {
          if (fullPath) {
            files.add(file.getAbsolutePath());
          } else {
            files.add(name);
          }
        }
      } else if (file.isDirectory()) {
        //we ignore directories
      }
    }
    return files;
  }

  /**
   * Recursively deletes the given path (file or directory).
   * 
   * @param path The root folder to delete.
   */
  public static void recursiveDelete(String path) {
    recursiveDelete(new File(path));
  }

  /**
   * Recursively deletes the given path (file or directory).
   * 
   * @param file The root folder to delete.
   */
  public static void recursiveDelete(File file) {
    if (!file.exists()) return;
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        recursiveDelete(f);
      }
    }
    file.delete();
  }

  /**
   * Copies given files to the given target directory.
   * If source is a file, only that file is copied.
   * If source is a directory, all files and subdirectories are copied recursively.
   * 
   * @param from The source to copy from.
   * @param to Destination directory. If it does not exist, it is created. If it is a file, throws IllegalArgumentException.
   * @throws IOException In case of IO problems.. eh
   */
  public static void copyFiles(String from, String to) throws IOException {
    File src = new File(from);
    if (!src.exists()) throw new IllegalArgumentException("File/Dir to copy does not exists:"+from);
    File dest = new File(to);
    if (dest.exists() && !dest.isDirectory()) throw new IllegalArgumentException("Cannot copy to '"+to+"', target exists and is not a directory.");
    if (src.isFile()) recursiveCopy(src, dest);
    if (src.isDirectory()) {
      for (File f : src.listFiles()) {
        recursiveCopy(f, dest);
      }
    }
  }

  /**
   * Internally handles recursion for file copy.
   * 
   * @param src The source directory.
   * @param dest Where we are copying to.
   * @throws IOException In case of IO problems.. eh
   */
  private static void recursiveCopy(File src, File dest) throws IOException {
    dest.mkdirs();
    if (src.isFile()) {
      String path = dest.getAbsolutePath();
      Files.copy(Paths.get(src.getAbsolutePath()), Paths.get(path, src.getName()));
      return;
    }
    if (src.isDirectory()) {
      String name = src.getName();
      Path p = Paths.get(dest.getAbsolutePath(), name);
      File newDest = new File(p.toString());
      for (File f : src.listFiles()) {
        recursiveCopy(f, newDest);
      }
    }
  }

  private static OutputStream out = null;
  private static PrintStream sout = null;

  public static void startOutputCapture() {
    sout = System.out;
    out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    System.setOut(ps);
  }
  
  public static String getOutput() {
    return out.toString();
  }
  
  public static void endOutputCapture() {
    if (sout != null) System.setOut(sout);
  }
}

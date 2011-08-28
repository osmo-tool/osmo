package osmo.miner.model;

import osmo.miner.parser.XmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Teemu Kanstren
 */
public class ModelObject implements Comparable<ModelObject> {
  private final XmlParser parser = new XmlParser();
  private final InputStream in;
  private final String name;
  private Node root = null;

  public ModelObject(File file) {
    try {
      in = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Unable to open file "+file.getName(), e);
    }
    name = file.getName();
  }

  public synchronized Node getRoot() {
    if (root != null) {
      return root;
    }
    root = parser.parse(in);
    return root;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ModelObject that = (ModelObject) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public int compareTo(ModelObject o) {
    return name.compareTo(o.name);
  }
}

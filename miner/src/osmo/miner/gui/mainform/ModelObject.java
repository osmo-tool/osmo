package osmo.miner.gui.mainform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Teemu Kanstren
 */
public class ModelObject implements Comparable<ModelObject> {
  private final File file;
  private final String name;

  public ModelObject(File file) {
    this.file = file;
    name = file.getName();
  }

  public InputStream getInputStream() {
    InputStream in = null;
    try {
      in = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Unable to open file " + file.getName(), e);
    }
    return in;
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

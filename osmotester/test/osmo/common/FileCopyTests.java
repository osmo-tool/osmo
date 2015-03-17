package osmo.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class FileCopyTests {
  private static final String targetDir = "test-target-dir";

  @Before
  @After
  public void clear() {
    TestUtils.recursiveDelete(targetDir);
  }


  @Test
  public void fileCopy() throws Exception {
    TestUtils.copyFiles("osmotester/license.txt", targetDir);
    File file = new File(targetDir+"/license.txt");
    assertTrue("File should be copied", file.exists());
    assertTrue("File should be .. a file!", file.isFile());
  }

  @Test
  public void dirCopy() throws Exception {
    TestUtils.copyFiles("osmotester/test-data1", targetDir);
    File file = new File(targetDir+"/afile.txt");
    assertTrue("File should be copied", file.exists());
    assertTrue("File should be .. a file!", file.isFile());
    file = new File(targetDir+"/afile2.txt");
    assertTrue("File should be copied", file.exists());
    assertTrue("File should be .. a file!", file.isFile());
  }

  @Test
  public void nestedDirCopy() throws Exception {
    TestUtils.copyFiles("osmotester/test-data2", targetDir);
    File file = new File(targetDir+"/level1.txt");
    assertTrue("File should be copied", file.exists());
    assertTrue("File should be .. a file!", file.isFile());
    file = new File(targetDir+"/level2/level2.txt");
    assertTrue("Level 2 should be copied", file.exists());
    assertTrue("Level 2 file should be .. a file!", file.isFile());
    file = new File(targetDir+"/level2/level3/level3.txt");
    assertTrue("Level 3 should be copied", file.exists());
    assertTrue("Level 3 file should be .. a file!", file.isFile());
  }

  @Test
  public void invalidSource() throws Exception {
    try {
      TestUtils.copyFiles("helloworldishouldnotexist", targetDir);
      fail("Invalid source should fail copy.");
    } catch (IllegalArgumentException e) {
      assertEquals("Msg for invalid source to copy", "File/Dir to copy does not exists:helloworldishouldnotexist", e.getMessage());
    }
  }

  @Test
  public void invalidTarget() throws Exception {
    TestUtils.copyFiles("osmotester/license.txt", targetDir);
    try {
      TestUtils.copyFiles("osmotester/license.txt", targetDir+"/license.txt");
      fail("Invalid target should fail copy.");
    } catch (IllegalArgumentException e) {
      assertEquals("Msg for invalid source to copy", "Cannot copy to 'test-target-dir/license.txt', target exists and is not a directory.", e.getMessage());
    }
  }
}

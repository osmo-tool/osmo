package osmo.visualizer.custom;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MessageSequenceVisualizer extends JFrame implements Visualizer {

  private static final long serialVersionUID = 7870534648148858783L;

  private JTabbedPane tabbedPane = null;

  private String data = "";

  public MessageSequenceVisualizer(String title) {
    super(title);
    tabbedPane = new JTabbedPane();
    getContentPane().add(tabbedPane);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    setVisible(true);
  }

  @Override
  public void testSuiteStart(String testSuiteID) {
    // TODO Auto-generated method stub

  }

  @Override
  public void testSuiteStop(int testCount) {
    // TODO Auto-generated method stub

  }

  @Override
  public void testCaseStart(String testCaseID) {
    data = "";
  }

  @Override
  public void testCaseStop(String testCaseID) {

    try {
      // Build parameter string
      data = "style=default&message="
              + URLEncoder.encode(data, "UTF-8");

      // Send the request
      URL url = new URL("http://www.websequencediagrams.com");
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(
              conn.getOutputStream());

      // write parameters
      writer.write(data);
      writer.flush();

      // Get the response
      StringBuffer answer = new StringBuffer();
      BufferedReader reader = new BufferedReader(new InputStreamReader(
              conn.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        answer.append(line);
      }
      writer.close();
      reader.close();

      String json = answer.toString();
      int start = json.indexOf("?img=");
      int end = json.indexOf("\"", start);

      url = new URL("http://www.websequencediagrams.com/"
              + json.substring(start, end));
      System.out.print(url);

      OutputStream out = new BufferedOutputStream(new FileOutputStream(
              "test.png"));
      InputStream in = url.openConnection().getInputStream();
      byte[] buffer = new byte[1024];
      int numRead;
      long numWritten = 0;
      while ((numRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, numRead);
        numWritten += numRead;
      }

      in.close();
      out.close();

      tabbedPane.addTab(testCaseID, new ImagePanel(url));
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void stateTransition(Object oldState, Object newState, String message) {
    if (oldState != null && newState != null)
      data += oldState.toString() + "->" + newState.toString() + ": " + message + "\n";
  }

  @Override
  public void info(String text, String relatedState) {
    // TODO: state place
    data += "note left of " + relatedState + ": " + text + "\n";
  }

  @Override
  public void visualize() {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("serial")
  public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(URL url) {
      try {
        image = ImageIO.read(url);
      } catch (IOException ex) {
        // handle exception...
      }
    }

    @Override
    public void paintComponent(Graphics g) {
      g.drawImage(image, 0, 0, null);
    }

  }

}

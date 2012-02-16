import java.awt.Canvas; 
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics; 
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
/**
 * Draw a horizontal bar graph. Bars can have multiple sections in different colors.
 * @author xtktuom
 *
 */

 
public class BarGraph extends Canvas{
  
  //SETTINGS
  private static final int size = 2;
  private static final int leftmargin = 40;
  Map<String, Color> legend;
  
  //END OF SETTINGS
  private int barlines;
  private String barname;
  BufferedImage image;
  private boolean borders;
  private int barwidth;
  private String unit;
  /**
   * Create the bar graph. Each graph will create own Frame with the graph.
   * 
   * @param lines Define how many bars the bar graph will have
   * @param barwidthmax Range of values in the bar width
   * @param name Base of the name of the bars. Names will be constructed from this string and an number between 0 and "lines"
   * @param bordered define whether bars are bordered 
   * @param u The unit of measure displayed on the graph
   */
  public BarGraph(int lines, int barwidthmax, String n, boolean bordered, String u){
    this.unit = u;
    this.barname = n;
    this.borders = bordered;
    this.barwidth = barwidthmax;
    barlines = lines;
    legend = new HashMap<String, Color>();
    this.setBackground(Color.WHITE);
    image = new BufferedImage(size*barwidthmax, size*50*lines+60, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    g.setColor(Color.BLACK);
    for(int i = 0; i < lines; i++){
      g.drawString(barname+i, 5, 30+size*i*50);
    }
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.setSize(size*barwidthmax+20,size*50*lines+100);
    frame.setVisible(true);
    frame.pack();
  }
  /**Draw a single bar section.
   * 
   * @param line Which line should the bar section be.
   * @param start Start value of the bar section.
   * @param end End value of the bar section
   * @param color Color of the bar section.
   * @param name Description of the bar section.
   */
  public void drawBar(int line, int start, int end, Color color, String name){
    Graphics g = image.getGraphics();
    legend.put(name, color);
    g.setColor(color);    
    g.fillRect(leftmargin+size*start, size*line*50, size*end-start, size*50);
    if(this.borders){
      g.setColor(Color.BLACK);
      g.drawRect(leftmargin+size*start, size*line*50, size*end-start, size*50);
    }
    repaint();
  }
  
  public void paint(Graphics g) {    
    g.drawImage(image, 0, 0, null);    
  } 
  
  /**
   * Draws the legend display. Uses names and colors defined for bars.
   */
  
  public void spillOutLegend(){
    Graphics g = image.getGraphics();
    int i=0;    
    for(Map.Entry<String, Color> c : legend.entrySet()){
      g.setColor(c.getValue());
      int x, y;
      x= size*10+i*120;
      y=barlines*size*50+30;      
      g.fillRect(x, y, 20, 20);
      g.setColor(Color.BLACK);
      g.drawRect(x, y, 20, 20);
      g.drawString(c.getKey(), x+22, y+20);
      System.out.println(c.getKey() + ": " + c.getValue().toString()+" "+x+" "+y);
      i++;
    }
    for(i = 0; i < 10 ; i++){
      g.drawLine(leftmargin+i*barwidth*size/10, this.barlines*50*size, leftmargin+i*barwidth*size/10, this.barlines*50*size+4);
      g.drawString(""+(i*this.barwidth/10)+this.unit, leftmargin+i*barwidth*size/10, this.barlines*50*size+15);
    }
    
    repaint();
  }
}

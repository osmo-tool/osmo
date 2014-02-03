package osmo.tester.gui.jfx.configurationtab;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author Teemu Kanstren
 */
public class ConfigurationTab extends Tab {
  public ConfigurationTab() {
    super("Configuration");
    setClosable(false);
    GridPane grid = createGridPane();
    VBox box = createVBox();
    grid.add(box, 1, 1);
    ScorePane score = new ScorePane();
    grid.add(score, 3, 1);

    createSeparator(Orientation.HORIZONTAL, grid, 0, 0, 5, 1);
    createSeparator(Orientation.HORIZONTAL, grid, 0, 2, 5, 1);
    createSeparator(Orientation.VERTICAL, grid, 0, 0, 1, 3);
    createSeparator(Orientation.VERTICAL, grid, 2, 0, 1, 3);
    createSeparator(Orientation.VERTICAL, grid, 4, 0, 1, 3);

    setContent(grid);
  }
  
  private void createSeparator(Orientation orientation, GridPane grid, int x, int y, int w, int h) {
    Separator separator = new Separator(orientation);
    grid.add(separator, x, y, w, h);
    
  }

  private GridPane createGridPane() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10, 10, 10, 10));
    return grid;
  }
  
  private VBox createVBox() {
    VBox box = new VBox(10);
    ObservableList<Node> children = box.getChildren();
    BasicsPane basics = new BasicsPane();
    Separator separator = new Separator(Orientation.HORIZONTAL);
    AdvancedPane advanced = new AdvancedPane();
    children.add(basics);
    children.add(separator);
    children.add(advanced);
    return box;
  }
}

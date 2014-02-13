package osmo.tester.gui.jfx.configurationtab;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import osmo.tester.gui.jfx.GUIState;

/**
 * @author Teemu Kanstren
 */
public class ConfigurationTab extends Tab {
  private final GUIState state;
  private final ScorePane scorePane;
  private GeneralPane generalPane;

  public ConfigurationTab(GUIState state) {
    super("Configuration");
    this.state = state;
    setClosable(false);

    GridPane grid = createGridPane();
    VBox box = createVBox();
    grid.add(box, 1, 1);
    scorePane = new ScorePane(state);
    grid.add(scorePane, 3, 1);

    createSeparator(Orientation.HORIZONTAL, grid, 0, 0, 5, 1);
    createSeparator(Orientation.HORIZONTAL, grid, 0, 2, 5, 1);
    createSeparator(Orientation.VERTICAL, grid, 0, 0, 1, 3);
    createSeparator(Orientation.VERTICAL, grid, 2, 0, 1, 3);
    createSeparator(Orientation.VERTICAL, grid, 4, 0, 1, 3);

    ScrollPane scroller = new ScrollPane(grid);
    setContent(scroller);
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
    generalPane = new GeneralPane(state);
    Separator separator1 = new Separator(Orientation.HORIZONTAL);
    GeneratorPane genetor = new GeneratorPane(state);
    Separator separator2 = new Separator(Orientation.HORIZONTAL);
    children.add(generalPane);
    children.add(separator1);
    children.add(genetor);
    children.add(separator2);
    return box;
  }
  
  public void storeScoreWeights() {
    scorePane.storeWeights();
  }

  public void storeGeneralParameters() {
    generalPane.storeParameters();
  }
}

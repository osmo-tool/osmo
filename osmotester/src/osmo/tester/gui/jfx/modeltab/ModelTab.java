package osmo.tester.gui.jfx.modeltab;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.ModelFactory;
import osmo.tester.parser.MainParser;
import osmo.tester.parser.ParserResult;

import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class ModelTab extends Tab {
//  private final GridPane grid;
  private final GUIState state;
  private final VBox topVBox;
  private Button loadButton;
  private TextField field;
  private ScrollPane scroller = null;
  private boolean showGuards = true;
  private boolean showPre = true;
  private boolean showPost = true;
  private boolean showBeforeTest = true;
  private boolean showBeforeSuite = true;
  private boolean showAfterTest = true;
  private boolean showAfterSuite = true;
  private boolean showLastSteps = true;
  private ParserResult parserResult = null;

  public ModelTab(GUIState state) {
    super("Model");
    this.state = state;
    setClosable(false);
    topVBox = createMainPane();
    createFactoryPane();
    createChoicePane();
    Platform.runLater(loadButton::requestFocus);
    setContent(topVBox);
  }

  private void createFactoryPane() {
    Label label = new Label("Factory:");
    field = new TextField();
    field.setPrefColumnCount(50);
    field.setPromptText("Enter model factory class name");
    loadButton = new Button("Load");
    loadButton.setOnAction(event -> {
      loadModel();
    });
    HBox box = new HBox(label, field, loadButton);
    box.setSpacing(10);
    box.setAlignment(Pos.CENTER_LEFT);
//    grid.add(box, 0, 0);
    topVBox.getChildren().add(box);
  }
  
  private void createChoicePane() {
    HBox box = new HBox(10);
    CheckBox guard = new CheckBox("Guard");
    CheckBox pre = new CheckBox("Pre");
    CheckBox post = new CheckBox("Post");
    CheckBox beforeTest = new CheckBox("BeforeTest");
    CheckBox beforeSuite = new CheckBox("BeforeSuite");
    CheckBox afterTest = new CheckBox("AfterTest");
    CheckBox afterSuite = new CheckBox("AfterSuite");
    CheckBox lastStep = new CheckBox("LastStep");
    
    guard.setSelected(showGuards);
    pre.setSelected(showPre);
    post.setSelected(showPost);
    beforeTest.setSelected(showBeforeTest);
    beforeSuite.setSelected(showBeforeSuite);
    afterTest.setSelected(showAfterTest);
    afterSuite.setSelected(showAfterSuite);
    lastStep.setSelected(showLastSteps);

    guard.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showGuards = newValue;
      refreshModel();
    });
    pre.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showPre = newValue;
      refreshModel();
    });
    post.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showPost = newValue;
      refreshModel();
    });
    beforeTest.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showBeforeTest = newValue;
      refreshModel();
    });
    beforeSuite.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showBeforeSuite = newValue;
      refreshModel();
    });
    afterTest.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showAfterTest = newValue;
      refreshModel();
    });
    afterSuite.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showAfterSuite = newValue;
      refreshModel();
    });
    lastStep.selectedProperty().addListener((observable, oldValue, newValue) -> {
      showLastSteps = newValue;
      refreshModel();
    });
    
    ObservableList<Node> children = box.getChildren();
    guard.setSelected(true);
    pre.setSelected(true);
    post.setSelected(true);
    beforeTest.setSelected(true);
    beforeSuite.setSelected(true);
    afterTest.setSelected(true);
    afterSuite.setSelected(true);
    lastStep.setSelected(true);
    
    children.add(new Label("Show:"));
    children.add(guard);
    children.add(pre);
    children.add(post);
    children.add(beforeTest);
    children.add(beforeSuite);
    children.add(afterTest);
    children.add(afterSuite);
    children.add(lastStep);
    topVBox.getChildren().add(box);
  }
  
  private VBox createMainPane() {
    VBox grid = new VBox(10);
    grid.setPadding(new Insets(10, 10, 10, 10));
    return grid;
  }

  private void loadModel() {
    String factoryClass = field.getText();
    MainParser parser = new MainParser();
    ModelFactory factory = null;
    try {
      factory = (ModelFactory) Class.forName(factoryClass).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    parserResult = parser.parse(0, factory, null);
    refreshModel();
    state.setFactory(factory);
  }
  
  private void refreshModel() {
    if (parserResult == null) return;
    FSM fsm = parserResult.getFsm();
    
    if (scroller != null) topVBox.getChildren().remove(scroller);


    VBox box = new VBox(10);
    //this sets color for background if desired
//    box.setId("step-box");
    ObservableList<Node> children = box.getChildren();

    addGenericParts(fsm, children);
    addSteps(fsm, children);

    scroller = new ScrollPane(box);
    VBox.setVgrow(scroller, Priority.ALWAYS);
    scroller.setFitToWidth(true);
    topVBox.getChildren().add(scroller);
  }

  private void addGenericParts(FSM fsm, ObservableList<Node> children) {
    VBox inbox = new VBox();
    inbox.setStyle("-fx-background-color: orange;");
    ObservableList<Node> kids = inbox.getChildren();

    kids.add(new Separator(Orientation.HORIZONTAL));
    addBeforeTests(fsm, kids);
    addAfterTests(fsm, kids);
    addBeforeSuites(fsm, kids);
    addAfterSuites(fsm, kids);
    addLastSteps(fsm, kids);
    children.add(inbox);
  }

  private void addSteps(FSM fsm, ObservableList<Node> children) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    int x = 0;
    for (FSMTransition transition : transitions) {
      VBox inbox = new VBox();
      setRowColor(x++, inbox);
      ObservableList<Node> kids = inbox.getChildren();

      kids.add(new Separator(Orientation.HORIZONTAL));
      kids.add(new Label("STEP: "+transition.getStringName()));

      addGuards(transition, kids);
      addPrePost(transition, kids);

      children.add(inbox);
    }
  }

  private void addAfterSuites(FSM fsm, ObservableList<Node> kids) {
    if (!showAfterSuite) return;
    Collection<InvocationTarget> afterSuites = fsm.getAfterSuites();
    for (InvocationTarget afterSuite : afterSuites) {
      kids.add(new Label("AFTERSUITE: "+afterSuite.getDescription()));
    }
  }

  private void addBeforeSuites(FSM fsm, ObservableList<Node> kids) {
    if (!showBeforeSuite) return;
    Collection<InvocationTarget> beforeSuites = fsm.getBeforeSuites();
    for (InvocationTarget beforeSuite : beforeSuites) {
      kids.add(new Label("BEFORESUITE: "+beforeSuite.getDescription()));
    }
  }

  private void addAfterTests(FSM fsm, ObservableList<Node> kids) {
    if (!showAfterTest) return;
    Collection<InvocationTarget> afterTests = fsm.getAfterTests();
    for (InvocationTarget afterTest : afterTests) {
      kids.add(new Label("AFTERTEST: "+afterTest.getDescription()));
    }
  }

  private void addLastSteps(FSM fsm, ObservableList<Node> kids) {
    if (!showLastSteps) return;
    Collection<InvocationTarget> lastSteps = fsm.getLastSteps();
    for (InvocationTarget lastStep : lastSteps) {
      kids.add(new Label("LASTSTEP: "+lastStep.getDescription()));
    }
  }

  private void addBeforeTests(FSM fsm, ObservableList<Node> kids) {
    if (!showBeforeTest) return;
    Collection<InvocationTarget> beforeTests = fsm.getBeforeTests();
    for (InvocationTarget beforeTest : beforeTests) {
      kids.add(new Label("BEFORETEST: "+beforeTest.getDescription()));
    }
  }

  private void addPrePost(FSMTransition transition, ObservableList<Node> kids) {
    Collection<InvocationTarget> pres = transition.getPreMethods();
    Collection<InvocationTarget> posts = transition.getPostMethods();

    boolean preExists = showPre && pres.size() > 0;
    boolean postExists = showPost && posts.size() > 0;
    if (preExists || postExists) {
      Label plus = new Label("Following Pre/Post actions apply");
      kids.add(plus);
      if (preExists) addPre(kids, pres);
      if (postExists) addPost(kids, posts);
    }
  }

  private void setRowColor(int x, VBox inbox) {
    if (x % 2 == 0) {
      inbox.setStyle("-fx-background-color: #98bf21;");
    } else {
      inbox.setStyle("-fx-background-color: #A7C942;");
    }
  }

  private void addPost(ObservableList<Node> kids, Collection<InvocationTarget> posts) {
    for (InvocationTarget post : posts) {
      Label label = new Label("-POST: "+post.getDescription());
      kids.add(label);
    }
  }

  private void addPre(ObservableList<Node> kids, Collection<InvocationTarget> pres) {
    for (InvocationTarget pre : pres) {
      Label label = new Label("-PRE: "+pre.getDescription());
      kids.add(label);
    }
  }

  private void addGuards(FSMTransition transition, ObservableList<Node> kids) {
    if (!showGuards) return;
    Collection<InvocationTarget> guards = transition.getGuards();
    if (guards.size() == 0) {
      Label label = new Label("Always enabled");
      kids.add(label);
    } else {
      Label label = new Label("Enabled if following is true:");
      kids.add(label);
    }
    for (InvocationTarget guard : guards) {
      Label label = new Label("-"+guard.getDescription());
//        label.setId("guard");
      kids.add(label);
    }
  }


  //osmo.mjexamples.gsm.GSMModelFactory
  //osmo.tester.examples.calendar.CalendarFactory
}

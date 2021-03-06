package menu;

import addImage.AddImageGUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.PictureList;
import removeImage.RemoveImageGUI;
import repository.GlobalVariables;
import slideShow.Slideshow;

public class Menu {

    private VBox vPane;
    private AnchorPane sidePane;
    private GridPane root;
    private Stage stage;
    private Scene scene;
    private ArrayList<Button> buttons;
    private DelayNode delay;
    private double xPos, yPos;
    private Pane resize;
    //Keeps the panes for different activities
    private AddImageGUI addImagePane;
    private RemoveImageGUI removeImagePane;
    private static PictureList pictureList;
    private static boolean behandleActive = true;

    public Menu() throws IOException {
        pictureList = new PictureList();

        buttons = new ArrayList<>();
        makeButtons();

        delay = new DelayNode();

        sidePane = new AnchorPane();
        vPane = new VBox();
        buildSidePane();

        root = new GridPane();
        //Change setActivityPane if you enable GridLines (this adds a child)
        //root.setGridLinesVisible(true);
        root.add(sidePane, 0, 0);

        resize = new Pane();
        resize.setMinSize(15, 15);
        resize.setCursor(Cursor.SE_RESIZE);

        root.add(resize, 2, 1);

        removeImagePane = new RemoveImageGUI();
        addImagePane = new AddImageGUI();
        setActivityPane(addImagePane);

        //Here the default size can be changed
        scene = new Scene(root, 1000, 720, Color.TRANSPARENT);
        scene.getStylesheets().add(Menu.class.getResource("/stylesheets/Menu.css").toExternalForm());

        //Toggle Fullscreen
        sidePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !event.isConsumed()) {
                    event.consume();
                    stage.setFullScreen(!stage.isFullScreen());
                }
            }
        });

        //Moving the window
        sidePane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xPos = event.getX();
                yPos = event.getY();
            }
        });

        sidePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!stage.isFullScreen()) {
                    scene.getWindow().setX(event.getScreenX() - xPos);
                    scene.getWindow().setY(event.getScreenY() - yPos);
                }
            }
        });

        //Resizing the window      
        resize.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!stage.isFullScreen()) {
                    double x = event.getScreenX();
                    double y = event.getScreenY();
                    //Minimum size can be set here
                    if (x - scene.getWindow().getX() > 800) {
                        scene.getWindow().setWidth(event.getScreenX() - scene.getWindow().getX());
                    }
                    if (y - scene.getWindow().getY() > 600) {
                        scene.getWindow().setHeight(event.getScreenY() - scene.getWindow().getY());
                    }
                }
            }
        });

        if (behandleActive){
            buttons.get(0).arm();
            buttons.get(0).fire();
        }else {
            buttons.get(1).arm();
            buttons.get(1).fire();
        }

    }

    //Builds the side panel
    private void buildSidePane() {
        vPane.setAlignment(Pos.TOP_RIGHT);
        VBox.setVgrow(vPane, Priority.ALWAYS);
        vPane.setSpacing(10);
        vPane.getChildren().addAll(buttons);
        vPane.setMinWidth(200);
        sidePane.getChildren().add(vPane);
        AnchorPane.setTopAnchor(vPane, 5.0);
        sidePane.setMaxWidth(200.0);

        sidePane.getChildren().add(delay);
        AnchorPane.setBottomAnchor(delay, 80.0);

        //Button for closing the menu
        Button btnExit = new Button("Lukk Meny");
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        btnExit.setPrefSize(190, 40);
        btnExit.setCancelButton(true);
        btnExit.setTranslateX(5.0);
        sidePane.getChildren().add(btnExit);
        AnchorPane.setBottomAnchor(btnExit, 10.0);
    }

    //The buttons for different activities
    private void makeButtons() {

        Button btnSearch = new Button("Behandle Tags");
        Button btnDelete = new Button("Slette Bilder");

        btnSearch.setOnAction(event -> {
            try {
                System.out.println("search fire");
                btnSearch.setDisable(true);
                btnDelete.setDisable(false);
                behandleActive = true;
                addImagePane = new AddImageGUI();

            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
            setActivityPane(addImagePane);
        });

        btnDelete.setOnAction(event -> {
            System.out.println("delete fire");
            btnDelete.setDisable(true);
            btnSearch.setDisable(false);
            behandleActive = false;
            removeImagePane.update();
            setActivityPane(removeImagePane);
        });
        buttons.add(btnSearch);
        buttons.add(btnDelete);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPrefSize(190, 40);
            buttons.get(i).setTranslateY(10);
        }
    }

    //Changes the shown activity
    private void setActivityPane(Pane activityPane) {
        //Remember to +1 this if you have turned on GridLines
        if (root.getChildren().size() > 2) {
            root.getChildren().remove(2);
        }
        root.add(activityPane, 1, 0);
    }

    /**
     * This method generates the stage for the Menu Call this method to start
     * the menu.
     */
    public void generateStage() {
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("PhotoApp");
        stage.setMinHeight(620);
        stage.setMinWidth(1020);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public static PictureList getPictureList() {
        return pictureList;
    }
}

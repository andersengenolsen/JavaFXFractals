package view;

import fractal.CellularAutomata;
import fractal.Mandelbrot;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;


/**
 * The application currently holds only 2 tabs, Mandelbrot and Cellular Automata.
 * <p>
 * The GUI is made dynamically.
 * TODO: Implement GUI with Scenebuilder.
 * TODO: Implement responsive GUI
 *
 * @author Anders Engen Olsen
 * @see Mandelbrot
 * @see CellularAutomata
 */
public class Main extends Application {

    // Dimensions
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private static final int TOP_BOX_HEIGHT = 125;
    private static final int CANVAS_WIDTH = WIDTH;
    private static final int CANVAS_HEIGHT = HEIGHT - TOP_BOX_HEIGHT;

    // Tabs
    private Tab tabMandelbrot;
    private Tab tabAutomata;

    // Reference to all tabs
    private ArrayList<Tab> tabs;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("JavaFX Fractals");

        primaryStage.setScene(initScene());

        // TODO: Responsiveness
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    /**
     * TEMPORARY SOLUTION.
     * Dynamically creating GUI for the application.
     * Each tab has a button to generate the fractals, and to reset the screen.
     * <p>
     * The for-loop initializes a Canvas-object for each tab, and a GraphicsContext with a reference to the canvas,
     * to enable drawing on the canvas.
     * <p>
     * Helper methods implemented to preserve a somewhat clean code.
     *
     * @return Scene GUI
     * @see #initTabs()
     * @see #initTopBox(int, int)
     * @see #setUpMandelbrotTab(GraphicsContext, HBox)
     * @see #setUpAutomataTab(GraphicsContext, HBox)
     */
    private Scene initScene() {

        // Tabs in the layout.
        initTabs();

        // group for all elements in the layout
        Group root = new Group();

        // TabPane-layouts
        TabPane tabPane = new TabPane();

        BorderPane mainPane = new BorderPane();

        // Looping through each tab..
        for (Tab tab : tabs) {

            tab.setClosable(false);

            // -- Top box with buttons -- //
            HBox topBox = initTopBox(WIDTH, TOP_BOX_HEIGHT);
            topBox.setSpacing(10d);

            // -- Canvas and GraphicsContext for drawing -- //
            Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            Group group = new Group();
            group.getChildren().addAll(canvas);

            // Setting up
            if (tab.equals(tabMandelbrot)) {
                setUpMandelbrotTab(gc, topBox);
            } else if (tab.equals(tabAutomata))
                setUpAutomataTab(gc, topBox);

            // -- VBox container for the entire layout within the tab. -- //
            VBox vBox = new VBox();
            vBox.getChildren().addAll(topBox, group);

            // Adding content to tab
            tab.setContent(vBox);
        } // End of loop

        // Adding all tabs to tabpane
        tabPane.getTabs().addAll(tabs);

        // Tabpane to Borderpane
        mainPane.setCenter(tabPane);

        // Mainpane to root-groups
        root.getChildren().add(mainPane);

        // Scene
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.WHITE);

        // Dimensions
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());

        return scene;
    }

    /**
     * Initializing Mandelbrot-object, and adding buttons to top vbox.
     *
     * @param gc GraphicsContext for drawing
     * @see Mandelbrot
     */
    private void setUpMandelbrotTab(GraphicsContext gc, HBox hBox) {

        Mandelbrot mandelbrot = new Mandelbrot(gc, CANVAS_WIDTH, CANVAS_HEIGHT);

        Button btnDraw = new Button("Create Mandelbrot");
        Button btnReset = new Button("Reset");

        // Draw-button.
        btnDraw.setOnAction((ActionEvent e) -> {
            mandelbrot.drawMandelbrot();
        });

        // Reset button
        btnReset.setOnAction((ActionEvent e) -> {
            mandelbrot.reset();
        });

        hBox.getChildren().addAll(btnDraw, btnReset);
    }


    /**
     * Initializing CellularAutomata-object, and adding buttons to top vbox.
     *
     * @param gc GraphicsContext for drawing
     * @see CellularAutomata
     * @see CellularAutomata#start(int)
     */
    private void setUpAutomataTab(GraphicsContext gc, HBox hBox) {
        CellularAutomata cellularAutomata = new CellularAutomata(gc, CANVAS_WIDTH, CANVAS_HEIGHT);

        // TextArea for ruleset
        TextField rulesetTxt = new TextField();
        rulesetTxt.setPromptText("Ruleset (1-255)");

        Button btnGenerate = new Button("Generate CA");

        // Draw-button
        btnGenerate.setOnAction((ActionEvent e) -> {

            try {
                cellularAutomata.start(Integer.parseInt(rulesetTxt.getText()));
            } catch (IllegalArgumentException err) {
                new Alert(Alert.AlertType.ERROR, err.getMessage()).showAndWait();
            }

        });

        hBox.getChildren().addAll(btnGenerate, rulesetTxt);
    }

    /**
     * Initializing tabs in layout, adding to ArrayList-container
     */
    private void initTabs() {
        tabMandelbrot = new Tab("Mandelbrot");
        tabAutomata = new Tab("Cellular Automata");

        tabs = new ArrayList<>();

        tabs.add(tabMandelbrot);
        tabs.add(tabAutomata);
    }

    /**
     * Creating topbox, visible in each tab
     *
     * @param width  topbox width
     * @param height topbox height
     * @return HBox, topbox in each tab.
     */
    private HBox initTopBox(int width, int height) {

        HBox topBox = new HBox();
        topBox.setPrefWidth(width);
        topBox.setPrefHeight(height);
        topBox.setStyle("-fx-background-color: #212121;");
        topBox.setPadding(new Insets(30, 0, 0, 20));

        return topBox;
    }

    public static void main(String[] args) {
        launch(args);
    }


}

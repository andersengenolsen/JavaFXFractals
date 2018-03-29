package view;

import fractal.CantorSet;
import fractal.CellularAutomata;
import fractal.Fractal;
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
    private Tab tabCantor;

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
     * @see #setUpTab(Fractal, HBox)
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

            // Group containing Canvas
            Group group = new Group();
            group.getChildren().addAll(canvas);

            // The current fractal
            Fractal fractal;

            // Assigning
            if (tab.equals(tabMandelbrot))
                fractal = new Mandelbrot(gc, CANVAS_WIDTH, CANVAS_HEIGHT);
            else if (tab.equals(tabAutomata))
                fractal = new CellularAutomata(gc, CANVAS_WIDTH, CANVAS_HEIGHT);
            else
                fractal = new CantorSet(gc, CANVAS_WIDTH, CANVAS_HEIGHT);

            // Setting up tab
            setUpTab(fractal, topBox);

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
     * Setting up a tab, associated to a fractal.
     * <p>
     * If the fractal is a CellularAutomata, we need a TextField for input.
     *
     * @param fractal Fractal-object
     * @param hBox    horizontal top box, containing buttons
     */
    private void setUpTab(Fractal fractal, HBox hBox) {

        // Buttons
        Button btnDraw = new Button("Generate");
        Button btnReset = new Button("Reset");

        // Special case if it is a Cellular Automata
        if (fractal instanceof CellularAutomata) {

            CellularAutomata cellularAutomata = (CellularAutomata) fractal;

            // TextArea for ruleset
            TextField rulesetTxt = new TextField();
            rulesetTxt.setPromptText("Ruleset (1-255)");

            btnDraw.setOnAction((ActionEvent e) -> {
                try {
                    cellularAutomata.setRule(Integer.parseInt(rulesetTxt.getText()));

                    cellularAutomata.draw();
                } catch (IllegalArgumentException err) {
                    new Alert(Alert.AlertType.ERROR, err.getMessage()).showAndWait();
                }
            });

            hBox.getChildren().addAll(btnDraw, rulesetTxt, btnReset);

        } else {
            btnDraw.setOnAction((ActionEvent e) -> {
                fractal.draw();
            });

            hBox.getChildren().addAll(btnDraw, btnReset);
        }
    }

    /**
     * Initializing tabs in layout, adding to ArrayList-container
     */
    private void initTabs() {
        tabMandelbrot = new Tab("Mandelbrot");
        tabAutomata = new Tab("Cellular Automata");
        tabCantor = new Tab("Cantor Set");

        tabs = new ArrayList<>();

        tabs.add(tabMandelbrot);
        tabs.add(tabAutomata);
        tabs.add(tabCantor);
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

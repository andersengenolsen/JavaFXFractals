package fractal;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Implementation of the Mandelbrot-set.
 * Probably not the most efficient one, but it gets the job done.
 * Short explanation of the Mandelbrot set below (or at least, my understanding of it...)
 * <p>
 * The mathematical definition of the Mandelbrot set is as follows:
 * f(z) = z² + c, where C is a complex number.
 * <p>
 * The Mandelbrotset contains all numbers C (where C is a complex number, and Z starts at 0),
 * which makes the equation Z = Z² + C to never get bigger than 2!
 * <p>
 * By iterating over Z = Z² + C, we can find the values for C which keeps Z less than 2.
 * If we come across a value C which makes Z bigger than 2, we simply color a pixel on the screen.
 * We can do this because C = a + ib, thus 'a' can represent the horizontal axis,
 * and ib (the imaginary part) can represent the vertical axis.
 * In other words, a computer screen can represent the complex plane.
 * <p>
 * To sum it up: If C makes Z converge towards infinity (Z > 2), then C is not a part of M,
 * and we will _NOT_ color the pixel.
 * <p>
 * TODO: Implement a not-so horrible color scheme
 *
 * @author Anders Engen Olsen
 */
public class Mandelbrot implements EventHandler<MouseEvent> {

    // Width/height ratio for canvas
    private final double WIDTH_HEIGHT_RATIO;

    // Group with canvas and the selection rectangle
    private Group group;

    // Selection rectangle
    private Rectangle rectangle;

    // Y-positions for selection rectangle.
    private double startY, deltaY;

    // Drawing
    private GraphicsContext gc;

    // Min and max values for the real and imaginary part.
    private double reMin = -2.00; // Re
    private double reMax = 2.00; // Re
    private double imMin = -2.00; // Im
    private double imMax = 2.00; // Im

    // We are supposed to find out if C makes Z converge towards infinity.
    // To be sure, we have to test an infinite number of times...
    private int maxIterations = 512;

    // Canvas dimensions
    private double canvasWidth;
    private double canvasHeight;

    /**
     * Constructor.
     * Initializing GraphicsContext for drawing
     * Group to handle mouseevents
     *
     * @param gc           graphicscontext for drawing
     * @param canvasWidth  width
     * @param canvasHeight height
     */
    public Mandelbrot(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        this.gc = gc;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        // Ratio
        WIDTH_HEIGHT_RATIO = canvasWidth / canvasHeight;

        // Selection rectangle
        rectangle = new Rectangle();
        rectangle.setFill(new Color(0, 0, 0, .5));

        // MouseEvent listener on group
        group = (Group) gc.getCanvas().getParent();
        group.addEventFilter(MouseEvent.ANY, this);
    }

    /**
     * Setting real / imaginary values back to "normal", redrawing.
     *
     * @see #drawMandelbrot()
     */
    public void reset() {
        // Clear canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // "Normal" values
        reMin = -2.00;
        reMax = 2.00;
        imMin = -2.00;
        imMax = 2.00;

        // Drawing
        drawMandelbrot();
    }

    /**
     * Drawing the mandelbrot set.
     * Using helper methods to map screen-coordinates to coordinates in the complex plane.
     * <p>
     * Definition:
     * If C is within a circle-radius of 2, then C is in the set.
     * Example:
     * Z500 = a + ib
     * Circle:
     * a² + b² = r²
     * Within 2
     * a² + b² <= 2²
     *
     * @see #computeRe(double)
     * @see #computeIm(double)
     */
    public void drawMandelbrot() {

        // Removing selection rectangle if on screen
        if (group.getChildren().contains(rectangle))
            group.getChildren().remove(rectangle);

        // Random colors.
        Color[] colors = new Color[maxIterations];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.color(Math.random(), Math.random(), Math.random());
        }

        // Distance between pixels
        double deltaA = (reMax - reMin) / canvasWidth;
        double deltaB = (imMin - imMax) / canvasHeight;

        // Looping through all pixels on canvas.
        for (int y = 0; y < canvasHeight - 1; y++) {

            // y mapped to the imaginary part of C
            double cIm = imMax + (y * deltaB);

            for (int x = 0; x < canvasWidth - 1; x++) {

                // x mapped to the real part of C
                double cRe = reMin + (x * deltaA);

                // Z starts at 0
                double zRe = 0;
                double zIm = 0;

                // Counter
                int count = 0;

                while (zRe * zRe + zIm * zIm <= 4
                        && count <= maxIterations) {

                    // Values for next iteration.
                    // nextZRe = zRe² - zIm² + cRe,
                    double nextZRe = zRe * zRe - zIm * zIm + cRe;
                    // nextZIm = 2 * zRe * zIm + cIm
                    double nextZIm = 2 * zRe * zIm + cIm;

                    // Updating
                    zRe = nextZRe;
                    zIm = nextZIm;
                    count++;
                }

                // If we reached maxIterations, we can assume the value is in the set.
                if (count <= maxIterations) {
                    gc.setFill(colors[count - 1]);
                } else {
                    gc.setFill(Color.BLACK);
                }
                // Drawing
                gc.fillRect(x, y, 1, 1);
            }
        }
    }

    /**
     * Mapping screen-coordinates to the real part of C (a in Z = a + ib)
     *
     * @param x screen-coordinate
     * @return coordinate in the complex plane
     */
    private double computeRe(double x) {
        double deltaA = (reMax - reMin) / canvasWidth;

        return (x * deltaA) + reMin;
    }

    /**
     * Mapping screen-coordinates to the imaginary part of C (ib in Z = a + ib)
     *
     * @param y screen-coordinate
     * @return coordinate in the complex plane
     */
    private double computeIm(double y) {
        double deltaB = (imMin - imMax) / canvasHeight;

        return (y * deltaB) + imMax;
    }

    /**
     * Handling drag and drop-events.
     * Selection rectangle for zooming in
     * <p>
     * Updating reMax, reMin, imMax og imMin.
     *
     * @param mouseEvent
     * @see #computeRe(double)
     * @see #computeIm(double)
     */
    @Override
    public void handle(MouseEvent mouseEvent) {

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            // Start-position y
            startY = mouseEvent.getY();
            deltaY = 0;

            // Setting start-position for selection rectangle
            rectangle.setX(mouseEvent.getX());
            rectangle.setY(startY);

            // Initial size of rectangle
            rectangle.setWidth(canvasWidth / 100);
            rectangle.setHeight(canvasHeight / 100);
        }

        // Mouse dragged, marking
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            // New y-position
            double newY = mouseEvent.getY();

            // Screen bounds
            if (rectangle.getWidth() + rectangle.getX() > canvasWidth && deltaY < (newY - startY)
                    || rectangle.getHeight() + rectangle.getY() > canvasHeight && deltaY < (newY - startY))
                return;

            // Preserving ratio
            rectangle.setHeight(Math.abs(startY - newY));
            rectangle.setWidth(rectangle.getHeight() * WIDTH_HEIGHT_RATIO);

            // Adding new, resized rectangle to screen
            group.getChildren().remove(rectangle);
            group.getChildren().add(rectangle);

            // Updating
            deltaY = newY - startY;
        }

        // Selection done
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

            // Updating, temp-variables
            double reMin = computeRe(rectangle.getX());
            double imMax = computeIm(rectangle.getY());
            double reMax = computeRe(rectangle.getWidth() + rectangle.getX());
            double imMin = computeIm(rectangle.getHeight() + rectangle.getY());

            // Updating
            this.reMin = reMin;
            this.imMax = imMax;
            this.reMax = reMax;
            this.imMin = imMin;

            drawMandelbrot();
        }
    }
}

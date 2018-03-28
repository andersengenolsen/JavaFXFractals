package fractal;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract class for fractals.
 * The class contains a reference to the graphicscontext, which is used for drawing of the fractals.
 *
 * @author Anders Engen Olsen
 */
public abstract class Fractal {

    // Drawing
    GraphicsContext gc;

    // Dimensions
    double canvasWidth, canvasHeight;

    /**
     * Constructor.
     *
     * @param gc           GraphicsContext, drawing
     * @param canvasWidth  Width of canvas
     * @param canvasHeight Height of canvas
     */
    Fractal(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        this.gc = gc;

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public abstract void draw();

    /**
     * Clearing all content on canvas
     */
    public void reset() {
        // Clear canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }

}

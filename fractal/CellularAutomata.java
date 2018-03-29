package fractal;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class to generate all the 256 rulesets in Wolfram Elementary Cellular Automata.
 * The CA's is visualized with a 2D-grid.
 * Keep in mind that the CA's is in fact just 1D, stacked on top of eachothers.
 * In other words, one line = one CA.
 *
 * @author Anders Engen Olsen
 */
public class CellularAutomata extends Fractal {

    private final int CELL_SIZE = 1;

    // The current ruleset
    private int[] ruleset;

    // Current rule
    private int rule;

    // Array with all cells
    private int[] cells;

    // Colors. Binary CA, 2 colors possible
    private Color[] colors = new Color[2];

    /**
     * Constructor.
     *
     * @param gc           GraphicsContext, with reference to the canvas. Drawing
     * @param canvasWidth  width canvas
     * @param canvasHeight height canvas
     */
    public CellularAutomata(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        super(gc, canvasWidth, canvasHeight);

        // Init array, all cells.
        cells = new int[(int) canvasWidth / CELL_SIZE];
    }

    /**
     * Returning a binary representation of an int, reversed.
     * The array returned is used as the ruleset.
     *
     * @param rule CellularAutomata rule
     * @return ruleset reversed
     * @see #next(int, int, int)
     */
    private int[] intToBinaryReverse(int rule) {

        int[] binary = new int[8];

        // int[] from int
        for (int i = 0, num = rule; i <= 7; i++, num >>>= 1) {
            binary[i] = num & 1;
        }

        return binary;
    }

    /**
     * Drawing 1D CA on Canvas.
     * Call to generate(int[]) to generate next generation (next line)
     * <p>
     * * @param rule # ruleset to generate
     *
     * @throws IllegalArgumentException Invalid ruleset
     * @see #reset()
     * @see #intToBinaryReverse(int)
     * @see #generate(int[])
     */
    @Override
    public void draw() {

        // Rule must be 0-255
        if (rule > 255 || rule < 0) {
            throw new IllegalArgumentException("Ruleset must be between 0 and 255!");
        }

        // Resetting canvas
        reset();

        // Generating ruleset
        ruleset = intToBinaryReverse(rule);

        // Current generation (y-position on canvas)
        int generation = 0;

        // Looping while still on canvas
        while (generation < canvasHeight / CELL_SIZE) {

            // Next generation
            cells = generate(cells);

            // Drawing
            for (int i = 0; i < cells.length; i++) {
                gc.setFill(colors[cells[i]]);
                gc.fillRect(i * CELL_SIZE, generation * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }

            // Next line..
            generation++;
        }
    }

    /**
     * Generating the next generation in the 1D Cellular Automata.
     * The next generations values are calculated in next(int,int,int).
     *
     * @param cells this generation
     * @return next generation
     * @see #next(int, int, int)
     */
    private int[] generate(int[] cells) {
        // Placeholder
        int[] newCells = new int[cells.length];

        // Boundary-elements, simply ignoring them..
        for (int x = 1; x < cells.length - 1; x++) {

            // Neighborhood
            int left = cells[x - 1];
            int mid = cells[x];
            int right = cells[x + 1];

            int newState = next(left, mid, right);

            newCells[x] = newState;
        }

        return newCells;
    }

    /**
     * Calculating next generations values.
     * <p>
     * The algorithm converts a b c to a regular int,
     * which will be used as in index in the ruleset.
     * NB! Backwards!
     *
     * @param left  lhs value
     * @param mid   mid value
     * @param right rhs value
     * @return next generation value
     */
    private int next(int left, int mid, int right) {

        // int to String
        String s = "" + left + mid + right;

        // Parsing, base 2
        int index = Integer.parseInt(s, 2);

        return ruleset[index];
    }

    /**
     * Resetting value in all cells to 0. 1 in the mid cell.
     * Adding new colors.
     * Resetting canvas
     */
    @Override
    public void reset() {

        super.reset();

        // Init-values
        for (int i = 0; i < cells.length; i++) {
            cells[i] = 0;
        }

        // Random colors
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.color(Math.random(), Math.random(), Math.random());
        }

        // mid = 1.
        cells[cells.length / 2] = 1;
    }

    /**
     * Setting current rule
     *
     * @param rule rule to set
     */
    public void setRule(int rule) {
        this.rule = rule;
    }
}

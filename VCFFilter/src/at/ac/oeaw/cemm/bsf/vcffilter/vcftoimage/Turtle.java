/**
 * ****************************************************************************
 * Compilation: javac Turtle.java Execution: java Turtle
 *
 * Data type for turtle graphics using standard draw.
 *
 *****************************************************************************
 */
package at.ac.oeaw.cemm.bsf.vcffilter.vcftoimage;

import java.awt.Color;

/**
 * Turtle.java
 *
 * Turtle graphics for drawing Hilbert curve.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Heiko Müller (modified the original Turtle.java from Robert Sedgewick and Kevin Wayne)
 * @version 1.0
 * @since 1.0
 */
public class Turtle {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * The turtle position
     */
    private double x, y;     // turtle is at (x, y)

    /**
     * The angle facing this many degrees counterclockwise from the x-axis
     */
    private double angle;    // facing this many degrees counterclockwise from the x-axis

    /**
     * a counter
     */
    public int count = 0;

    /**
     * indicator if variant falls in this interval
     */
    private boolean[] interval;

    /**
     * indicator1 of variant color
     */
    private boolean[] color1;

    /**
     * indicator2 of variant color
     */
    private boolean[] color2;

    // 
    /**
     * Creates new Turtle. start at (x0, y0), facing a0 degrees counterclockwise
     * from the x-axis.
     *
     * @param x0 start position x
     * @param y0 start position y
     * @param a0 start orientation in degrees
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public Turtle(double x0, double y0, double a0) {
        x = x0;
        y = y0;
        angle = a0;
        StdDraw.clear();
    }

    /**
     * Rotate orientation delta degrees counterclockwise.
     *
     * @param delta degrees counterclockwise
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void turnLeft(double delta) {
        angle += delta;
    }

    /**
     * Move forward the given amount, with the pen down and set Variant color according to interval.
     *
     * @param step pixel count
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @author Heiko Mueller
     * @version 1.0
     * @since 1.0
     */
    public void goForward(double step) {
        if (interval[count]) {
            StdDraw.setPenRadius(0.008);
            if (color1[count] && color2[count]) {
                StdDraw.setPenColor(Color.RED);
            } else if (color1[count] && !color2[count]) {
                StdDraw.setPenColor(Color.GREEN);
            } else if (!color1[count] && color2[count]) {
                StdDraw.setPenColor(Color.BLUE);
            } else if (!color1[count] && !color2[count]) {
                StdDraw.setPenColor(Color.YELLOW);
            }
        } else {
            StdDraw.setPenColor(Color.GRAY);
            StdDraw.setPenRadius(0.001);
        }
        double oldx = x;
        double oldy = y;
        x += step * Math.cos(Math.toRadians(angle));
        y += step * Math.sin(Math.toRadians(angle));
        StdDraw.point(x, y);
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.setPenRadius(0.001);
        StdDraw.line(oldx, oldy, x, y);

        count++;
        //System.out.println(count);
    }
    
    /**
     * Move forward the given amount, with the pen down.
     *
     * @param step pixel count
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void goForward0(double step) {
        count++;
        if (count % 3 == 0) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.setPenRadius(0.004);
        } else {
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setPenRadius(0.002);
        }
        double oldx = x;
        double oldy = y;
        x += step * Math.cos(Math.toRadians(angle));
        y += step * Math.sin(Math.toRadians(angle));
        StdDraw.line(oldx, oldy, x, y);
        //StdDraw.point(x, y);
    }

    /**
     * Copy to onscreen.
     *
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void show() {
        StdDraw.show();
    }

    /**
     * Pause t milliseconds.
     *
     * @param t time in ms
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void pause(int t) {
        StdDraw.pause(t);
    }

    /**
     * Sets pen color.
     *
     * @param color color
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void setPenColor(Color color) {
        StdDraw.setPenColor(color);
    }

    /**
     * Sets pen radius.
     *
     * @param radius pen radius
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void setPenRadius(double radius) {
        StdDraw.setPenRadius(radius);
    }

    /**
     * Sets Canvas size.
     *
     * @param width canvas width
     * @param height canvas height
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void setCanvasSize(int width, int height) {
        StdDraw.setCanvasSize(width, height);
    }

    /**
     * Sets x-scale.
     *
     * @param min min value
     * @param max max value
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void setXscale(double min, double max) {
        StdDraw.setXscale(min, max);
    }

    /**
     * Sets y-scale.
     *
     * @param min min value
     * @param max max value
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @version 1.0
     * @since 1.0
     */
    public void setYscale(double min, double max) {
        StdDraw.setYscale(min, max);
    }

    /**
     * Sets interval.
     *
     * @param interval array of booleans for interval rendering
     * @author HeikoMueller
     * @version 1.0
     * @since 1.0
     */
    public void setInterval(boolean[] interval) {
        this.interval = interval;
    }

    /**
     * Sets color1.
     *
     * @param interval array of booleans for color rendering
     * @author HeikoMueller
     * @version 1.0
     * @since 1.0
     */
    public void setColor1(boolean[] interval) {
        this.color1 = interval;
    }

    /**
     * Sets color2.
     *
     * @param interval array of booleans for color rendering
     * @author HeikoMueller
     * @version 1.0
     * @since 1.0
     */
    public void setColor2(boolean[] interval) {
        this.color2 = interval;
    }

    // sample client for testing
    /*
    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        double x0 = 0.5;
        double y0 = 0.0;
        double a0 = 60.0;
        double step = Math.sqrt(3) / 2;
        Turtle turtle = new Turtle(x0, y0, a0);
        turtle.goForward(step);
        turtle.turnLeft(120.0);
        turtle.goForward(step);
        turtle.turnLeft(120.0);
        turtle.goForward(step);
        turtle.turnLeft(120.0);
    }
    */

}

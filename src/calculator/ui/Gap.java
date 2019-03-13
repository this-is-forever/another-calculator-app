package calculator.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The calculator.ui.Gap class allows the quick and easy creation of vertical or horizontal gaps between components in a Swing
 * container
 */
public class Gap {
    private static int gap = 5;

    /**
     * Sets the default gap, in pixels, that will be created when vertical() or horizontal() are called
     * @param x The new gap size in pixels
     */
    public static void setDefaultGap(int x) {
        gap = x;
    }

    /**
     * Creates a new component that will leave a vertical gap between the previous component and next component in the
     * parent container that it's added to. The gap's size is set using setDefaultGap(int)
     * @return A newly created Box with the default gap as its height
     */
    public static Component vertical() {
        return Box.createRigidArea(new Dimension(0, gap));
    }

    /**
     * Creates a new component that will leave a horizontal gap between the previous component and next component in the
     * parent container that it's added to. The gap's size is set using setDefaultGap(int)
     * @return A newly created Box with the default gap as its width
     */
    public static Component horizontal() {
        return Box.createRigidArea(new Dimension(gap, 0));
    }

    /**
     * Creates a new component that will leave a vertical gap between the previous component and next component in the
     * parent container that it's added to
     * @param height The height, in pixels that the gap should be
     * @return A newly created Box with height provided as its height
     * @see Box
     * @see Component
     */
    public static Component vertical(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    /**
     * Creates a new component that will leave a vertical gap between the previous component and next component in the
     * parent container that it's added to
     * @param width The height, in pixels that the gap should be
     * @return A newly created Box with width provided as its width
     * @see Box
     * @see Component
     */
    public static Component horizontal(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }
}

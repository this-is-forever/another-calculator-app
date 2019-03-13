package calculator.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A JButton with a font size defined by calculator.ui.CalculatorApp.APP_BUTTON_FONT_SIZE that cannot gain focus so that keyboard
 * input is not interrupted after the user clicks the button. CalculatorButtons are also sized slightly larger.
 */
public class CalculatorButton extends JButton {

    public CalculatorButton(String text) {
        // Set the text displayed on the button to the text given
        super(text);
        // Change the font size to the app-defined font size
        setFont(getFont().deriveFont(CalculatorApp.APP_BUTTON_FONT_SIZE));
        // Prevent the button from gaining focus
        setFocusable(false);
        // Set preferred size so the button is nice and big
        setPreferredSize(new Dimension(50, 50));
    }

}

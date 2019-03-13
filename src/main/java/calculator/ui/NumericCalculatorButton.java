package calculator.ui;

import java.awt.*;

/**
 * A calculator.ui.CalculatorButton that displays a single digit as input which will send that digit
 * to the calculator.ui.CalculatorApp when clicked.
 */
public class NumericCalculatorButton extends CalculatorButton {

    // The digit this button will represent
    private int value;

    public NumericCalculatorButton(int number, CalculatorApp app) {
        // Set the text of the button to the number it will represent
        super(Integer.toString(number));
        // Set the button's value to the number given
        value = number;
        // Make the font bold to make it stand out
        setFont(getFont().deriveFont(Font.BOLD));
        // Add an action listener that will send the app the digit when the button is clicked
        addActionListener(e -> app.addDigit(value));
    }

}

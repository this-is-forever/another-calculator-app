package calculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * The Calculator class is an abstract implementation of a handheld calculator that takes inputs (digits, operators)
 * and can be used to solve basic arithmetic. The readout on the calculator shows the current user input or the answer
 * to the last math problem entered by the user.
 *
 * A CalculatorOutputListener is notified when the readout on the Calculator changes.
 */
public class Calculator {

    // Values for valid mathematical operators
    public static final char
            OPERATOR_ADD        = '+',
            OPERATOR_SUBTRACT   = '-',
            OPERATOR_MULTIPLY   = '*',
            OPERATOR_DIVIDE     = '/',
            OPERATOR_POWER      = '^',
            OPERATOR_NONE       = 0;

    // Formatter used to format answers into String format for currentInput after hitting solve
    private final static DecimalFormat NUMBER_FORMATTER_NO_COMMAS = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    static {
        // Sets the maximum number of decimals to the maximum possible for the double data type
        NUMBER_FORMATTER_NO_COMMAS.setMaximumFractionDigits(340);
    }

    private CalculatorOutputListener listener;

    // Holds the application's stored input
    private double storedInput;

    // Holds the operators for the current problem and previous problem
    private char operator, previousOperator;

    // Flag that when set will cause the application to overwrite the number on display when given new input
    private boolean overwrite;

    // Holds the current input as a String
    private String currentInput;

    public Calculator(CalculatorOutputListener listener) {
        // Keep a reference to the listener which will be updated when the calculator's input/output changes
        this.listener = listener;
        // Set the overwrite flag to true so input overwrites the default '0'
        overwrite = true;
        // Set the default input to 0
        currentInput = "0";
        // Set the storedInput value to 0
        storedInput = 0;
        // Set the operator to none
        operator = Calculator.OPERATOR_NONE;
        // Set the previous operator to none
        previousOperator = Calculator.OPERATOR_NONE;
    }

    /**
     * Flips the sign (positive/negative) of the current input
     */
    public void flipSign() {
        // Add a '-' to the beginning of the input if one isn't there, otherwise remove it
        if(currentInput.startsWith("-"))
            currentInput = currentInput.substring(1);
        else
            currentInput = "-" + currentInput;
        listener.update(currentInput);
    }

    /**
     * Adds a decimal point to the current input so long as one doesn't currently exist
     */
    public void addDecimal() {
        // Check to see if the input already has a decimal point
        if(!currentInput.contains(".")) {
            // Change the input to 0 if the overwrite flag is set, so that hitting the decimal key changes input
            // to "0."
            if(overwrite) {
                overwrite = false;
                currentInput = "0";
            }
            // It doesn't, so add one
            currentInput += ".";
            // Update the display
            listener.update(currentInput);
        }
    }

    /**
     * Adds a new digit to the input number, as the least significant digit
     * @param digit The digit, 0-9, to be added to the number
     */
    public void addDigit(int digit) {
        // Convert the input to a double
        double d = Double.parseDouble(currentInput);
        // Wipe the input if the overwrite flag is set or the current input is 0 without a decimal
        if(overwrite || (d == 0 && !currentInput.contains("."))) {
            overwrite = false;
            currentInput = "";
        }

        // Limit input to 16 characters
        if(currentInput.length() > 15)
            return;
        // Add the current digit to the end of the String and update the display
        currentInput += (char)('0' + digit);
        listener.update(currentInput);
    }

    /**
     * Removes the right-most digit from the current input
     */
    public void backspace() {
        // Remove the last character of the input
        currentInput = currentInput.substring(0, currentInput.length() - 1);
        // If the string is empty, change it to 0
        if(currentInput.isEmpty())
            currentInput = "0";
        // Update the display
        listener.update(currentInput);
    }

    /**
     * Wipes the calculator's current input and memory
     */
    public void clear() {
        // Erase the current input and the stored input
        currentInput = "0";
        storedInput = 0;
        // Forget the operators that were given
        operator = OPERATOR_NONE;
        previousOperator = OPERATOR_NONE;
        // Update the display showing the calculator is reset
        listener.update(currentInput);
    }

    /**
     * Clears the current input from the display
     */
    public void clearInput() {
        currentInput = "0";
        listener.update(currentInput);
    }

    /**
     * Finds the solution to the problem that was input by the user and displays it on the calculator
     */
    public void solve() {
        // Variables to be used for arithmetic later
        double a, b;
        // Convert the current input into a double
        double input = Double.parseDouble(currentInput);

        // The following if statements determine whether the user is attempting to repeat the previous input
        // ex. If user inputs 100 / 5, then hits solve again, it will solve 20 / 5 instead of 5 / 5

        // Check to see if there was a given operator before the solve button was pressed
        if(operator == OPERATOR_NONE) {
            // If not, use the previous operator if one exists
            if(previousOperator != OPERATOR_NONE) {
                // Change the current operator to the previous one
                operator = previousOperator;
                // Use the current input as 'a' and the stored input as 'b'
                a = input;
                b = storedInput;
            } else // Otherwise, there is no problem to solve; quit
                return;
        } else { // If not, remember the operator used for later
            previousOperator = operator;
            // Set 'a' to the stored input and 'b' to the current input
            a = storedInput;
            b = input;
        }
        // Solve the expression that was input by the user
        // 'a' is the stored input and b the current input
        // UNLESS the user hits the solve button without selecting a new operator,
        // in which case 'a' is the current input and 'b' the stored input
        switch(operator) {
            case OPERATOR_ADD:
                input = a + b;
                break;
            case OPERATOR_SUBTRACT:
                input = a - b;
                break;
            case OPERATOR_MULTIPLY:
                input = a * b;
                break;
            case OPERATOR_DIVIDE:
                // See if the divisor is 0 and if it is, don't attempt to solve the problem
                if(b == 0) {
                    currentInput = "0";
                    overwrite = true;
                    listener.update("∞");
                    return;
                } else
                    input = a / b;
                break;
            case OPERATOR_POWER:
                input = Math.pow(a, b);
        }
        // Reformat the answer and store it as the current input
        currentInput = NUMBER_FORMATTER_NO_COMMAS.format(input);
        // Clear the current operator
        operator = OPERATOR_NONE;
        // Save 'b' as the stored input
        storedInput = b;
        // Prepare to overwrite the display when the user begins typing a new number
        overwrite = true;
        // Update the display with the solution
        listener.update(currentInput);
    }

    /**
     * Handles input for the operator inputs (add, subtract, multiply, etc)
     *
     * If another operator was already given, finds and displays the solution and remembers the new operator
     * @param o The operator given as a character (e.g. +, -, *, /)
     */
    public void operator(char o) {
        // If another operator was already chosen, solve
        if(operator != OPERATOR_NONE)
            solve();
        // Store the given input and prepare to overwrite the input on display
        storedInput = Double.parseDouble(currentInput);
        overwrite = true;
        // Save the operator that was chosen
        operator = o;
    }
}

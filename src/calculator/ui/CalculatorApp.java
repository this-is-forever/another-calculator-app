package calculator.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * A Calculator app that allows the user to perform basic math.
 *
 * If ran as the main class, it will create an app window and change the look and feel to the user's OS L&F.
 */
public class CalculatorApp implements KeyListener {

    /**
     * Application entry point.
     * Creates a new calculator.ui.CalculatorApp object and uses it to create a GUI that acts as a calculator
     * @param args Arguments passed by the command line
     */
    public static void main(String[] args) {
        // Set the look and feel to the user's OS L&F to make it look like a native application
        setLookAndFeel();
        // Create a new calculator.ui.CalculatorApp object which will be used to create the calculator application
        CalculatorApp application = new CalculatorApp();
        // Create the GUI and display it to the user
        application.createAndShowGUI();
        // Swing UI thread handles the rest!
    }

    /**
     * Sets the look and feel of this Swing application to that of the user's Operating System
     */
    private static void setLookAndFeel() {
        try {
            // Attempt the change the look and feel to the OS L&F. Catch any exceptions that occur
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a new thread off of the Swing UI Event thread to prevent the UI from locking up
     * @param r A Runnable object whose run method will be called off the Swing event thread
     * @see Runnable
     */
    private static void enterWorkerThread(Runnable r) {
        // Create a new thread to perform the operations defined in r's run method
        new Thread(r).start();
    }

    /**
     * Runs a task on the Swing event thread as soon as the event thread is able to
     * @param r The task to be run
     * @see Runnable
     */
    private static void onUIThread(Runnable r) {
        // Cause the Swing event thread to perform the operations defined in r's run method
        SwingUtilities.invokeLater(r);
    }

    // Determines the output display font size
    public static final float APP_DISPLAY_FONT_SIZE = 24f;
    // Determines the calculator button font size
    public static final float APP_BUTTON_FONT_SIZE = 16f;
    // Determines the amount of spacing between edges of the window and components
    public static final int APP_BORDER_SIZE = 10;
    // Determines the amount of spacing between components
    public static final int APP_COMPONENT_SPACING = 2;

    // Hotkeys that will control the application via keyboard
    private static final char
            HOTKEY_ADD          = '+',
            HOTKEY_SUBTRACT     = '-',
            HOTKEY_MULTIPLY     = '*',
            HOTKEY_DIVIDE       = '/',
            HOTKEY_POWER        = '^',
            HOTKEY_SOLVE        = KeyEvent.VK_ENTER,
            HOTKEY_CLEAR        = KeyEvent.VK_DELETE,
            HOTKEY_BACKSPACE    = KeyEvent.VK_BACK_SPACE;

    // Values for valid mathematical operators
    private static final char
            OPERATOR_ADD        = '+',
            OPERATOR_SUBTRACT   = '-',
            OPERATOR_MULTIPLY   = '*',
            OPERATOR_DIVIDE     = '/',
            OPERATOR_POWER      = '^',
            OPERATOR_NONE       = 0;

    // Used to turn integer digits into Strings
    private static final String[] DIGITS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    // References application's main window (JFrame)
    private JFrame applicationFrame;

    // References application's input/output display
    private JTextField displayField;

    // Holds the application's current and stored inputs for math
    private double storedInput;

    // Holds the operators for the current problem and previous problem
    private char operator, previousOperator;

    // Flag that when set will cause the application to overwrite the number on display when given new input
    private boolean overwrite;

    // Holds the current input as a String
    private String currentInput;

    // Formatter used to format numbers on the display
    private final static DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private final static DecimalFormat NUMBER_FORMATTER_NO_COMMAS = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    static {
        NUMBER_FORMATTER.setMaximumFractionDigits(340); // Sets the maximum number of formatted decimal places to
        // the maximum possible for the double data type
        NUMBER_FORMATTER_NO_COMMAS.setMaximumFractionDigits(340);
    }


    public void createAndShowGUI() {
        // Create the application's main window with the title "Calculator"
        applicationFrame = new JFrame("Calculator");
        // Make the program exit when the X button on the window is clicked
        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Prevent the window from being resized
        applicationFrame.setResizable(false);

        // Set the default gap size for the calculator.ui.Gap class to APP_COMPONENT_SPACING to set default spacing between layers
        Gap.setDefaultGap(APP_COMPONENT_SPACING);

        // Create a panel that will hold all of the content for the window
        JPanel mainPanel = new JPanel();
        // Make the panel focusable - it will be the only component in the window that is
        mainPanel.setFocusable(true);
        // Add a key listener so that key input is captured
        mainPanel.addKeyListener(this);
        // Give the panel a border that is APP_BORDER_SIZE pixels thick
        mainPanel.setBorder(BorderFactory.createEmptyBorder(APP_BORDER_SIZE, APP_BORDER_SIZE, APP_BORDER_SIZE, APP_BORDER_SIZE));
        // Lay items out vertically - essentially one column with a bunch of rows
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // Set the main window's content pain to the panel we made
        applicationFrame.setContentPane(mainPanel);

        // -------------------------------------------------------
        // -- Start adding components to the main content panel --
        // -------------------------------------------------------

        // -- Layer that will contain the display begins --

        // Create layer for the label at the top of the window
        JComponent layer = Box.createHorizontalBox();

        // Create a new text field with nothing in it to hold the user's input
        // Make text field 15 characters wide
        displayField = new JTextField(15);
        // Set the default text of the text field to '0'
        displayField.setText("0");
        // Resize the font of the input field to the font size set by APP_DISPLAY_FONT_SIZE
        displayField.setFont(displayField.getFont().deriveFont(APP_DISPLAY_FONT_SIZE));
        // Align text in the text field to the right
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        // Hide the carat to make it seem more like an actual calculator (0 alpha = transparent)
        displayField.setCaretColor(new Color(0, 0, 0, 0));
        // Prevent the text field from being edited
        displayField.setEditable(false);
        // Prevent the text field from gaining focus to prevent input from being interrupted
        displayField.setFocusable(false);

        // Add the label to the layer
        layer.add(displayField);
        // Add horizontal glue so the label sticks to the left of its container
        layer.add(Box.createHorizontalGlue());
        // Add the finished layer to the main window
        applicationFrame.add(layer);
        // Add a gap layer to create space between the previous layer and the next
        applicationFrame.add(Gap.vertical());

        // -- Layer that holds the input buttons begins --

        // Create the next layer as a grid with 5 rows and 4 columns
        // Buttons are added left to right and then top to bottom
        layer = new JPanel(new GridLayout(5, 4, APP_COMPONENT_SPACING, APP_COMPONENT_SPACING));

        // Used as a reference to the current button being created
        JButton b;

        // Create the CE button and add it to the layer
        layer.add(b = new CalculatorButton(("CE")));
        // Use reference to make the CE button clear the current input and update the display when clicked
        b.addActionListener(e -> {
            currentInput = "0";
            update();
        });

        // Create the C button and add it to the layer
        layer.add(b = new CalculatorButton(("C")));
        // Use reference to make the C button clear the calculator and its memory when clicked
        b.addActionListener(e -> clear());

        // Create the backspace button and add it to the layer
        layer.add(b = new CalculatorButton(("⌫")));
        // Use reference to make the backspace button delete the last digit in the input
        b.addActionListener(e -> backspace());

        // Create the division button and add it to the layer
        layer.add(b = new CalculatorButton("÷"));
        // Use reference to add functionality for the button
        b.addActionListener(e -> operator(OPERATOR_DIVIDE));

        // Add the 7, 8, and 9 button. calculator.ui.NumericCalculatorButton calls the addDigit method when the button is pressed
        layer.add(new NumericCalculatorButton(7, this));
        layer.add(new NumericCalculatorButton(8, this));
        layer.add(new NumericCalculatorButton(9, this));

        // Create the multiply button and add its functionality
        layer.add(b = new CalculatorButton("×"));
        b.addActionListener(e -> operator(OPERATOR_MULTIPLY));

        // Add the 4-6 buttons to the layer
        layer.add(new NumericCalculatorButton(4, this));
        layer.add(new NumericCalculatorButton(5, this));
        layer.add(new NumericCalculatorButton(6, this));

        // Add the subtraction button and add its functionality
        layer.add(b = new CalculatorButton(("-")));
        b.addActionListener(e -> operator(OPERATOR_SUBTRACT));

        // Add the 1-3 buttons to the layer
        layer.add(new NumericCalculatorButton(1, this));
        layer.add(new NumericCalculatorButton(2, this));
        layer.add(new NumericCalculatorButton(3, this));

        // Add the addition button and add its functionality
        layer.add(b = new CalculatorButton(("+")));
        b.addActionListener(e -> operator(OPERATOR_ADD));

        // Add the change sign button and implement its functionality
        layer.add(b = new CalculatorButton(("±")));
        b.addActionListener(e -> { flipSign(); });

        // Add the 0 button to the layer
        layer.add(new NumericCalculatorButton(0, this));

        // Add the decimal button to the layer and implement its functionality
        layer.add(b = new CalculatorButton((".")));
        b.addActionListener(e -> addDecimal());

        // Add the solve button to the layer and implement its functionality
        layer.add(b = new CalculatorButton(("=")));
        b.addActionListener(e -> solve());

        // Add the layer to the content pane
        applicationFrame.add(layer);

        // Set the overwrite flag to true so input overwrites the default '0'
        overwrite = true;
        // Set the default input to 0
        currentInput = "0";
        // Set the storedInput value to 0
        storedInput = 0;
        // Set the operator to none
        operator = OPERATOR_NONE;
        // Set the previous operator to none
        previousOperator = OPERATOR_NONE;
        // Resize the window so that all child components fit
        applicationFrame.pack();
        // Make the window visible
        applicationFrame.setVisible(true);
        // Swing UI thread takes care of the rest :)
    }


    /**
     * Flips the sign (positive/negative) of the current input
     */
    private void flipSign() {
        // Add a '-' to the beginning of the input if one isn't there, otherwise remove it
        if(currentInput.startsWith("-"))
            currentInput = currentInput.substring(1);
        else
            currentInput = "-" + currentInput;
        update();
    }

    /**
     * Updates the display to show the number stored
     */
    private void update() {
        // Find the decimal point in the current input if it exists
        int decimalLocation = currentInput.indexOf('.');
        // If it does, isolate the integer (non-decimal) part of the input and add commas, then display the number
        if(decimalLocation >= 0) {
            String integer = NUMBER_FORMATTER.format(Long.parseLong(currentInput.substring(0, decimalLocation)));
            String decimal = currentInput.substring(decimalLocation + 1);
            displayField.setText(integer + "." + decimal);
        } else
            // Otherwise, simply display the number with commas
            displayField.setText(NUMBER_FORMATTER.format(Long.parseLong(currentInput)));
    }

    /**
     * Adds a decimal point to the current input so long as one doesn't currently exist
     */
    private void addDecimal() {
        // Check to see if the input already has a decimal point
        if(!currentInput.contains(".")) {
            // It doesn't, so add one
            currentInput += ".";
            // Update the display
            update();
        }
    }

    /**
     * Adds a new digit to the input number, as the least significant digit
     * @param digit The digit, 0-9, to be added to the number
     */
    protected void addDigit(int digit) {
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
        currentInput += DIGITS[digit];
        update();
    }

    /**
     * Removes the right-most digit from the current input
     */
    private void backspace() {
        // Remove the last character of the input
        currentInput = currentInput.substring(0, currentInput.length() - 1);
        // If the string is empty, change it to 0
        if(currentInput.isEmpty())
            currentInput = "0";
        // Update the display
        update();
    }

    /**
     * Wipes the calculator's current input and memory
     */
    private void clear() {
        // Erase the current input and the stored input
        currentInput = "0";
        storedInput = 0;
        // Forget the operators that were given
        operator = OPERATOR_NONE;
        previousOperator = OPERATOR_NONE;
        // Update the display showing the calculator is reset
        update();
    }

    /**
     * Finds the solution to the problem that was input by the user and displays it on the calculator
     */
    private void solve() {
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
                    displayField.setText("∞");
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
        update();
    }

    /**
     * Handles input for the operator inputs (add, subtract, multiply, etc)
     *
     * If another operator was already given, finds and displays the solution and remembers the new operator
     * @param o The operator given as a character (e.g. +, -, *, /)
     */
    private void operator(char o) {
        // If another operator was already chosen, solve
        if(operator != OPERATOR_NONE)
            solve();
        // Store the given input and prepare to overwrite the input on display
        storedInput = Double.parseDouble(currentInput);
        overwrite = true;
        // Save the operator that was chosen
        operator = o;
    }

    /**
     * Handles keyboard input that the app receives
     * @param e A KeyEvent with information about the key that was typed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Get the key typed as a char value
        char c = e.getKeyChar();
        // If the key was a digit (0-9), add it to the current number
        if(Character.isDigit(c)) {
            addDigit(c - '0');
        } else { // Otherwise, see if a hotkey was pressed
            switch(c) {
                case '.':
                    addDecimal();
                    break;
                case HOTKEY_BACKSPACE:
                    // Delete the least significant digit if possible
                    backspace();
                    break;
                case HOTKEY_CLEAR:
                    // Clear the calculator's input and its memory
                    clear();
                    break;
                case HOTKEY_ADD:
                case HOTKEY_SUBTRACT:
                case HOTKEY_MULTIPLY:
                case HOTKEY_DIVIDE:
                case HOTKEY_POWER:
                    operator(c);
                    break;
                case HOTKEY_SOLVE:
                    // Solve and display the solution to the input problem if possible
                    solve();
                    break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}  // Unused implemented method

    @Override
    public void keyReleased(KeyEvent e) {

    } // Unused implemented method
}

package calculator;

/**
 * An interface for a class that will listen to the output of a Calculator object
 */
public interface CalculatorOutputListener {
    /**
     * update is called by a Calculator object when the readout on the calculator is changed, either because
     * the user added a new digit to the input or a solution is displayed
     * @param output The calculator's current readout
     */
    void update(String output);
}

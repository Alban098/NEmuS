package utils.javafx;

import gui.inputs.NESInputs;
import javafx.scene.control.TextField;

/**
 * This class is just a normal TextField that can retains an Input
 * Used for convenience
 */
public class TextFieldInput extends TextField {

    private NESInputs input;

    /**
     * Get the Console Input of the TextField
     *
     * @return the corresponding ConsoleInput
     */
    public NESInputs getInput() {
        return input;
    }

    /**
     * Set the Console Input of the TextField
     *
     * @param input the new Console Input
     * @return the TextField
     */
    public TextFieldInput setInput(NESInputs input) {
        this.input = input;
        return this;
    }
}

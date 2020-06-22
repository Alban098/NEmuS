package gui.lwjgui.windows;

import gui.inputs.KeyTuple;
import gui.inputs.NESInputs;
import gui.inputs.TextFieldInput;
import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSWindow;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.ResourceBundle;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ControllersSettings extends Application implements Initializable {

    private static ControllersSettings instance;

    private NEmuSWindow emulator;
    private Stage stage;

    @FXML
    private TextFieldInput p1_keyboard_up;
    @FXML
    private TextFieldInput p1_keyboard_down;
    @FXML
    private TextFieldInput p1_keyboard_right;
    @FXML
    private TextFieldInput p1_keyboard_left;
    @FXML
    private TextFieldInput p1_keyboard_start;
    @FXML
    private TextFieldInput p1_keyboard_select;
    @FXML
    private TextFieldInput p1_keyboard_a;
    @FXML
    private TextFieldInput p1_keyboard_b;
    @FXML
    private TextFieldInput p2_keyboard_up;
    @FXML
    private TextFieldInput p2_keyboard_down;
    @FXML
    private TextFieldInput p2_keyboard_right;
    @FXML
    private TextFieldInput p2_keyboard_left;
    @FXML
    private TextFieldInput p2_keyboard_start;
    @FXML
    private TextFieldInput p2_keyboard_select;
    @FXML
    private TextFieldInput p2_keyboard_a;
    @FXML
    private TextFieldInput p2_keyboard_b;
    @FXML
    private TextFieldInput p1_gamepad_up;
    @FXML
    private TextFieldInput p1_gamepad_down;
    @FXML
    private TextFieldInput p1_gamepad_right;
    @FXML
    private TextFieldInput p1_gamepad_left;
    @FXML
    private TextFieldInput p1_gamepad_start;
    @FXML
    private TextFieldInput p1_gamepad_select;
    @FXML
    private TextFieldInput p1_gamepad_a;
    @FXML
    private TextFieldInput p1_gamepad_b;
    @FXML
    private TextFieldInput p2_gamepad_up;
    @FXML
    private TextFieldInput p2_gamepad_down;
    @FXML
    private TextFieldInput p2_gamepad_right;
    @FXML
    private TextFieldInput p2_gamepad_left;
    @FXML
    private TextFieldInput p2_gamepad_start;
    @FXML
    private TextFieldInput p2_gamepad_select;
    @FXML
    private TextFieldInput p2_gamepad_a;
    @FXML
    private TextFieldInput p2_gamepad_b;

    private TextFieldInput keyboardEdited = null;
    private TextFieldInput gamepadEdited = null;

    public ControllersSettings() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void focusInstance() {
        instance.stage.setIconified(false);
        instance.stage.requestFocus();
    }

    /**
     * Initialize the Settings Window
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        p1_keyboard_a.setInput(NESInputs.CONTROLLER_1_A).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_A));
        p1_keyboard_b.setInput(NESInputs.CONTROLLER_1_B).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_B));
        p1_keyboard_start.setInput(NESInputs.CONTROLLER_1_START).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_START));
        p1_keyboard_select.setInput(NESInputs.CONTROLLER_1_SELECT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_SELECT));
        p1_keyboard_up.setInput(NESInputs.CONTROLLER_1_UP).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_UP));
        p1_keyboard_down.setInput(NESInputs.CONTROLLER_1_DOWN).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_DOWN));
        p1_keyboard_left.setInput(NESInputs.CONTROLLER_1_LEFT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_LEFT));
        p1_keyboard_right.setInput(NESInputs.CONTROLLER_1_RIGHT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_RIGHT));

        p2_keyboard_a.setInput(NESInputs.CONTROLLER_2_A).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_A));
        p2_keyboard_b.setInput(NESInputs.CONTROLLER_2_B).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_B));
        p2_keyboard_start.setInput(NESInputs.CONTROLLER_2_START).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_START));
        p2_keyboard_select.setInput(NESInputs.CONTROLLER_2_SELECT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_SELECT));
        p2_keyboard_up.setInput(NESInputs.CONTROLLER_2_UP).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_UP));
        p2_keyboard_down.setInput(NESInputs.CONTROLLER_2_DOWN).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_DOWN));
        p2_keyboard_left.setInput(NESInputs.CONTROLLER_2_LEFT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_LEFT));
        p2_keyboard_right.setInput(NESInputs.CONTROLLER_2_RIGHT).setText(emulator.getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_RIGHT));

        p1_gamepad_a.setInput(NESInputs.CONTROLLER_1_A).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_A));
        p1_gamepad_b.setInput(NESInputs.CONTROLLER_1_B).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_B));
        p1_gamepad_start.setInput(NESInputs.CONTROLLER_1_START).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_START));
        p1_gamepad_select.setInput(NESInputs.CONTROLLER_1_SELECT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_SELECT));
        p1_gamepad_up.setInput(NESInputs.CONTROLLER_1_UP).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_UP));
        p1_gamepad_down.setInput(NESInputs.CONTROLLER_1_DOWN).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_DOWN));
        p1_gamepad_left.setInput(NESInputs.CONTROLLER_1_LEFT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_LEFT));
        p1_gamepad_right.setInput(NESInputs.CONTROLLER_1_RIGHT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_RIGHT));

        p2_gamepad_a.setInput(NESInputs.CONTROLLER_2_A).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_A));
        p2_gamepad_b.setInput(NESInputs.CONTROLLER_2_B).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_B));
        p2_gamepad_start.setInput(NESInputs.CONTROLLER_2_START).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_START));
        p2_gamepad_select.setInput(NESInputs.CONTROLLER_2_SELECT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_SELECT));
        p2_gamepad_up.setInput(NESInputs.CONTROLLER_2_UP).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_UP));
        p2_gamepad_down.setInput(NESInputs.CONTROLLER_2_DOWN).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_DOWN));
        p2_gamepad_left.setInput(NESInputs.CONTROLLER_2_LEFT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_LEFT));
        p2_gamepad_right.setInput(NESInputs.CONTROLLER_2_RIGHT).setText(emulator.getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_RIGHT));
        instance = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("controllersSettings.fxml")));
        stage.setScene(scene);
        stage.setTitle("Controllers Settings");
        //TODO Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        instance.stage = stage;
    }

    /**
     * Will trigger a Keyboard input change routine
     *
     * @param event the event launched when the field was clicked
     */
    @FXML
    private void editControls(MouseEvent event) {
        if (keyboardEdited == null) {
            //The emulation is paused
            emulator.pause();

            //We edit the field for user feedback
            keyboardEdited = (TextFieldInput) event.getSource();
            keyboardEdited.setText("...");
            keyboardEdited.setStyle("-fx-background-color: orange;");

            //We create a new GLFW window that will capture the Keyboard input
            KeyTuple key;
            glfwInit();
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            long window = glfwCreateWindow(300, 1, "Press a Key", NULL, NULL);
            if (window == NULL)
                throw new RuntimeException("Failed to create window");
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                glfwGetWindowSize(window, pWidth, pHeight);
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                if (vidmode != null)
                    glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            }
            glfwShowWindow(window);

            //We wait for the user to press a key
            while ((key = emulator.getInputMapper().getKeyPressed(window)) == null)
                glfwPollEvents();

            //We destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            //We edit the GUI accordingly
            keyboardEdited.setText(key.keyName);
            keyboardEdited.setStyle("");

            //We update the InputMapper with the new config
            emulator.getInputMapper().setMappedKey(keyboardEdited.getInput(), key.keyID);
            keyboardEdited = null;

            //We unpause the game
            emulator.pause();
        }
    }

    /**
     * Will trigger a Gamepad input change routine
     *
     * @param event the event launched when the field was clicked
     */
    @FXML
    private void editControlsGamepad(MouseEvent event) {
        //We only edit this if a gamepad is connected
        if (gamepadEdited == null && glfwJoystickPresent(GLFW_JOYSTICK_1)) {
            //The emulation is paused
            emulator.pause();

            //We edit the field for user feedback
            gamepadEdited = (TextFieldInput) event.getSource();
            gamepadEdited.setText("...");
            gamepadEdited.setStyle("-fx-background-color: orange;");

            //We create a new GLFW window that will capture the Keyboard input
            KeyTuple key;
            glfwInit();
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            long window = glfwCreateWindow(300, 1, "Press a Button", NULL, NULL);
            if (window == NULL)
                throw new RuntimeException("Failed to create window");
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                glfwGetWindowSize(window, pWidth, pHeight);
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                if (vidmode != null)
                    glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            }
            glfwShowWindow(window);

            //We wait for the user to press a button
            while ((key = emulator.getInputMapper().getButtonPressed(window)) == null)
                glfwPollEvents();

            //We destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            //We edit the GUI accordingly
            gamepadEdited.setText(key.keyName);
            gamepadEdited.setStyle("");

            //We update the InputMapper with the new config
            emulator.getInputMapper().setMappedButton(gamepadEdited.getInput(), key.keyID);
            gamepadEdited = null;

            //We unpause the game
            emulator.pause();
        }
    }
}

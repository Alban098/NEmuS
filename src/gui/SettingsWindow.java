package gui;

import core.apu.APU_2A03;
import core.apu.components.pulse.Oscillator;
import core.apu.components.triangle.TriangleOscillator;
import gui.inputs.NESInputs;
import gui.inputs.TextFieldInput;
import gui.inputs.KeyTuple;
import gui.interfaces.NEmuS_Runnable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import openGL.postProcessing.PostProcessingStep;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ResourceBundle;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class represents the Settings Window of the emulator
 * from which you can edit most of the parameters
 */
public class SettingsWindow implements Initializable {

    private static SettingsWindow instance;
    @FXML
    private Button pauseButton;
    @FXML
    private Button ramLeftButton;
    @FXML
    private Button ramLeftPlusButton;
    @FXML
    private Button ramRightButton;
    @FXML
    private Button ramRightPlusButton;
    @FXML
    private Button cpuStepButton;
    @FXML
    private Button frameStepButton;
    @FXML
    private Button paletteSwapButton;
    @FXML
    private ListView<PostProcessingStep> postProcessingList;
    @FXML
    private ComboBox<PostProcessingStep> postProcessingComboBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider soundQualitySlider;
    @FXML
    private CheckBox audioRenderingCheck;
    @FXML
    private CheckBox rawAudioCheck;
    @FXML
    private CheckBox pulse1Checkbox;
    @FXML
    private CheckBox pulse2Checkbox;
    @FXML
    private CheckBox triangleCheckbox;
    @FXML
    private CheckBox noiseCheckbox;
    @FXML
    private CheckBox dmcCheckbox;

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

    /**
     * Return the current Settings Window
     *
     * @return the current Settings Window
     */
    public static SettingsWindow getInstance() {
        return instance;
    }

    /**
     * Initialize the Settings Window
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        postProcessingComboBox.getItems().addAll(NEmuS_Runnable.getInstance().getPipeline().getAllSteps());
        if (!NEmuS.DEBUG_MODE) {
            ramLeftButton.setDisable(true);
            ramLeftPlusButton.setDisable(true);
            ramRightButton.setDisable(true);
            ramRightPlusButton.setDisable(true);
            paletteSwapButton.setDisable(true);
        } else {
            volumeSlider.setDisable(true);
            soundQualitySlider.setDisable(true);
            audioRenderingCheck.setDisable(true);
            rawAudioCheck.setDisable(true);
            pulse1Checkbox.setDisable(true);
            pulse2Checkbox.setDisable(true);
            triangleCheckbox.setDisable(true);
            noiseCheckbox.setDisable(true);
            dmcCheckbox.setDisable(true);
        }
        cpuStepButton.setDisable(true);
        frameStepButton.setDisable(true);
        instance = this;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> APU_2A03.setVolume(newValue.intValue() / 100.0));
        soundQualitySlider.valueProperty().addListener((observableValue, oldValue, newValue) ->  {Oscillator.setHarmonics(newValue.intValue() + 5);
            TriangleOscillator.setHarmonics(newValue.intValue()/4);
        });

        p1_keyboard_a.setInput(NESInputs.CONTROLLER_1_A).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_A));
        p1_keyboard_b.setInput(NESInputs.CONTROLLER_1_B).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_B));
        p1_keyboard_start.setInput(NESInputs.CONTROLLER_1_START).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_START));
        p1_keyboard_select.setInput(NESInputs.CONTROLLER_1_SELECT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_SELECT));
        p1_keyboard_up.setInput(NESInputs.CONTROLLER_1_UP).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_UP));
        p1_keyboard_down.setInput(NESInputs.CONTROLLER_1_DOWN).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_DOWN));
        p1_keyboard_left.setInput(NESInputs.CONTROLLER_1_LEFT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_LEFT));
        p1_keyboard_right.setInput(NESInputs.CONTROLLER_1_RIGHT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_1_RIGHT));

        p2_keyboard_a.setInput(NESInputs.CONTROLLER_2_A).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_A));
        p2_keyboard_b.setInput(NESInputs.CONTROLLER_2_B).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_B));
        p2_keyboard_start.setInput(NESInputs.CONTROLLER_2_START).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_START));
        p2_keyboard_select.setInput(NESInputs.CONTROLLER_2_SELECT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_SELECT));
        p2_keyboard_up.setInput(NESInputs.CONTROLLER_2_UP).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_UP));
        p2_keyboard_down.setInput(NESInputs.CONTROLLER_2_DOWN).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_DOWN));
        p2_keyboard_left.setInput(NESInputs.CONTROLLER_2_LEFT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_LEFT));
        p2_keyboard_right.setInput(NESInputs.CONTROLLER_2_RIGHT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedKey(NESInputs.CONTROLLER_2_RIGHT));

        p1_gamepad_a.setInput(NESInputs.CONTROLLER_1_A).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_A));
        p1_gamepad_b.setInput(NESInputs.CONTROLLER_1_B).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_B));
        p1_gamepad_start.setInput(NESInputs.CONTROLLER_1_START).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_START));
        p1_gamepad_select.setInput(NESInputs.CONTROLLER_1_SELECT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_SELECT));
        p1_gamepad_up.setInput(NESInputs.CONTROLLER_1_UP).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_UP));
        p1_gamepad_down.setInput(NESInputs.CONTROLLER_1_DOWN).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_DOWN));
        p1_gamepad_left.setInput(NESInputs.CONTROLLER_1_LEFT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_LEFT));
        p1_gamepad_right.setInput(NESInputs.CONTROLLER_1_RIGHT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_1_RIGHT));

        p2_gamepad_a.setInput(NESInputs.CONTROLLER_2_A).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_A));
        p2_gamepad_b.setInput(NESInputs.CONTROLLER_2_B).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_B));
        p2_gamepad_start.setInput(NESInputs.CONTROLLER_2_START).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_START));
        p2_gamepad_select.setInput(NESInputs.CONTROLLER_2_SELECT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_SELECT));
        p2_gamepad_up.setInput(NESInputs.CONTROLLER_2_UP).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_UP));
        p2_gamepad_down.setInput(NESInputs.CONTROLLER_2_DOWN).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_DOWN));
        p2_gamepad_left.setInput(NESInputs.CONTROLLER_2_LEFT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_LEFT));
        p2_gamepad_right.setInput(NESInputs.CONTROLLER_2_RIGHT).setText(NEmuS_Runnable.getInstance().getInputMapper().getMappedButton(NESInputs.CONTROLLER_2_RIGHT));

    }

    /**
     * Will trigger an add filter event to the Emulator
     */
    @FXML
    public void addProcessingStep() {
        if (postProcessingComboBox.getValue() != null) {
            try {
                postProcessingList.getItems().add(postProcessingComboBox.getValue());
                postProcessingList.layout();
                NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Will trigger a filter shift left event to the Emulator
     */
    @FXML
    public void shiftLeftPostProcessingStep() {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == 0)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index - 1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index - 1, selected);
        postProcessingList.getSelectionModel().select(index - 1);
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    /**
     * Will trigger a filter shift right event to the Emulator
     */
    @FXML
    public void shiftRightPostProcessingStep() {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == postProcessingList.getItems().size() - 1)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index + 1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index + 1, selected);
        postProcessingList.getSelectionModel().select(index + 1);
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    /**
     * Will trigger a ROM loading event to the Emulator
     */
    @FXML
    public void fireLoadROMEvent() {
        FileChooser romLoader = new FileChooser();
        romLoader.setInitialDirectory(new File("./"));
        romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
        File file = romLoader.showOpenDialog(null);
        if (file != null) {
            NEmuS_Runnable.getInstance().fireLoadROMEvent(file.getAbsolutePath(), file.getName());
            cpuStepButton.setDisable(true);
            frameStepButton.setDisable(true);
            pauseButton.setText("Pause");
        }
    }

    /**
     * Will trigger a reset event to the Emulator
     */
    @FXML
    public void fireResetEvent() {
        NEmuS_Runnable.getInstance().fireResetEvent();
        cpuStepButton.setDisable(true);
        frameStepButton.setDisable(true);
        pauseButton.setText("Pause");
    }

    /**
     * Will trigger a pause event to the Emulator
     */
    @FXML
    public void triggerPauseEvent() {
        if (NEmuS_Runnable.getInstance().pause()) {
            cpuStepButton.setDisable(!NEmuS.DEBUG_MODE || NEmuS_Runnable.getInstance().isEmulationRunning());
            frameStepButton.setDisable(NEmuS_Runnable.getInstance().isEmulationRunning());
            if (!NEmuS_Runnable.getInstance().isEmulationRunning())
                pauseButton.setText("Resume");
            else
                pauseButton.setText("Pause");
        }
    }

    /**
     * Will trigger a filter remove event to the Emulator
     */
    @FXML
    public void removeSelectedFilter() {
        postProcessingList.getItems().remove(postProcessingList.getSelectionModel().getSelectedItem());
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    /**
     * Will trigger a CPU instruction event to the Emulator
     */
    @FXML
    public void cpuStepEvent() {
        NEmuS_Runnable.getInstance().cpuStepEvent();
    }

    /**
     * Will trigger an audio renderings state event to the Emulator
     */
    @FXML
    public void fireAudioRenderingEvent() {
        NEmuS_Runnable.getInstance().fireAudioRenderingEvent(audioRenderingCheck.isSelected());
    }

    /**
     * Will trigger an audio renderings state event to the Emulator
     */
    @FXML
    public void fireRawAudioEvent() {
        NEmuS_Runnable.getInstance().fireRawAudioEvent(rawAudioCheck.isSelected());
    }

    /**
     * Will trigger a RAM Page shift event to the Emulator
     */
    @FXML
    public void ramPageRightEvent() {
        NEmuS_Runnable.getInstance().ramPageRightEvent();
    }

    /**
     * Will trigger a RAM Page shift event to the Emulator
     */
    @FXML
    public void ramPageLeftEvent() {
        NEmuS_Runnable.getInstance().ramPageLeftEvent();
    }

    /**
     * Will trigger a RAM Page shift event to the Emulator
     */
    @FXML
    public void ramPageRightPlusEvent() {
        NEmuS_Runnable.getInstance().ramPageRightPlusEvent();
    }

    /**
     * Will trigger a RAM Page shift event to the Emulator
     */
    @FXML
    public void ramPageLeftPlusEvent() {
        NEmuS_Runnable.getInstance().ramPageLeftPlusEvent();
    }

    /**
     * Will trigger a frame advance event to the Emulator
     */
    @FXML
    public void frameStepEvent() {
        NEmuS_Runnable.getInstance().frameStepEvent();
    }

    /**
     * Will trigger a palette swap event to the Emulator
     */
    @FXML
    public void paletteSwapEvent() {
        NEmuS_Runnable.getInstance().paletteSwapEvent();
    }

    @FXML
    public void pulse1Event() {
        NEmuS_Runnable.getInstance().pulse1Event(pulse1Checkbox.isSelected());
    }

    @FXML
    public void pulse2Event() {
        NEmuS_Runnable.getInstance().pulse2Event(pulse2Checkbox.isSelected());
    }

    @FXML
    public void triangleEvent() {
        NEmuS_Runnable.getInstance().triangleEvent(triangleCheckbox.isSelected());
    }

    @FXML
    public void noiseEvent() {
        NEmuS_Runnable.getInstance().noiseEvent(noiseCheckbox.isSelected());
    }

    @FXML
    public void dmcEvent() {
        NEmuS_Runnable.getInstance().dmcEvent(dmcCheckbox.isSelected());
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
            NEmuS_Runnable.getInstance().pause();

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
            while ((key = NEmuS_Runnable.getInstance().getInputMapper().getKeyPressed(window)) == null)
                glfwPollEvents();

            //We destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            //We edit the GUI accordingly
            keyboardEdited.setText(key.keyName);
            keyboardEdited.setStyle("");

            //We update the InputMapper with the new config
            NEmuS_Runnable.getInstance().getInputMapper().setMappedKey(keyboardEdited.getInput(), key.keyID);
            keyboardEdited = null;

            //We unpause the game
            NEmuS_Runnable.getInstance().pause();
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
            NEmuS_Runnable.getInstance().pause();

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
            while ((key = NEmuS_Runnable.getInstance().getInputMapper().getButtonPressed(window)) == null)
                glfwPollEvents();

            //We destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            //We edit the GUI accordingly
            gamepadEdited.setText(key.keyName);
            gamepadEdited.setStyle("");

            //We update the InputMapper with the new config
            NEmuS_Runnable.getInstance().getInputMapper().setMappedButton(gamepadEdited.getInput(), key.keyID);
            gamepadEdited = null;

            //We unpause the game
            NEmuS_Runnable.getInstance().pause();
        }
    }
}

package gui;

import core.apu.APU_2A03;
import core.apu.components.Oscillator;
import gui.interfaces.NEmuS_Runnable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import openGL.postProcessing.PostProcessingStep;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
        }
        audioRenderingCheck.setSelected(true);
        cpuStepButton.setDisable(true);
        frameStepButton.setDisable(true);
        instance = this;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> APU_2A03.setVolume(newValue.intValue() / 100.0));
        soundQualitySlider.valueProperty().addListener((observableValue, oldValue, newValue) -> Oscillator.setHarmonics(newValue.intValue() + 5));
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
    public void fireAudioRenderingEvent() {
        NEmuS_Runnable.getInstance().fireAudioRenderingEvent(audioRenderingCheck.isSelected());
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
}

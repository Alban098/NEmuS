package gui.lwjgui.windows;

import core.apu.APU_2A03;
import core.apu.channels.components.pulse.Oscillator;
import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSWindow;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class AudioSettings extends Application implements Initializable {

    private static AudioSettings instance;

    private NEmuSWindow emulator;
    private Stage stage;

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

    public AudioSettings() {
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
        instance = this;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> APU_2A03.setVolume(newValue.intValue() / 100.0));
        soundQualitySlider.valueProperty().addListener((observableValue, oldValue, newValue) -> Oscillator.setHarmonics(newValue.intValue() + 5));
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("audioSettings.fxml")));
        stage.setScene(scene);
        stage.setTitle("Audio Settings");
        //TODO Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        instance.stage = stage;
    }

    /**
     * Will trigger an audio renderings state event to the Emulator
     */
    @FXML
    public void fireAudioRenderingEvent() {
        emulator.fireAudioRenderingEvent(audioRenderingCheck.isSelected());
    }

    /**
     * Will trigger an audio renderings state event to the Emulator
     */
    @FXML
    public void fireRawAudioEvent() {
        emulator.fireRawAudioEvent(rawAudioCheck.isSelected());
    }

    /**
     * Will trigger a pulse 1 channel enable event to the Emulator
     */
    @FXML
    public void pulse1Event() {
        emulator.pulse1Event(pulse1Checkbox.isSelected());
    }

    /**
     * Will trigger a pulse 2 channel channel enable event to the Emulator
     */
    @FXML
    public void pulse2Event() {
        emulator.pulse2Event(pulse2Checkbox.isSelected());
    }

    /**
     * Will trigger a triangle channel enable event to the Emulator
     */
    @FXML
    public void triangleEvent() {
        emulator.triangleEvent(triangleCheckbox.isSelected());
    }

    /**
     * Will trigger a noise channel enable event to the Emulator
     */
    @FXML
    public void noiseEvent() {
        emulator.noiseEvent(noiseCheckbox.isSelected());
    }

    /**
     * Will trigger a DMC channel enable event to the Emulator
     */
    @FXML
    public void dmcEvent() {
        emulator.dmcEvent(dmcCheckbox.isSelected());
    }
}

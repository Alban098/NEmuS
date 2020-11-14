package gui.lwjgui.windows;

import core.AudioEngine;
import core.apu.APU_2A03;
import core.apu.channels.components.pulse.Oscillator;
import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class represent the Audio Settings Window
 */
public class AudioSettings extends Application implements Initializable {

    private static AudioSettings instance;
    private static AudioEngine audioEngine;
    private final NEmuSContext emulator;

    private Stage stage;

    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider soundQualitySlider;
    @FXML
    private Slider audioSampleSkipSlider;
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
    private CheckBox linearCheck;
    @FXML
    private ComboBox<AudioOutput> audioOutCombo;


    /**
     * Create a new instance of AudioSettings
     */
    public AudioSettings() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    public static void linkSoundIO(AudioEngine audioEngine) {
        AudioSettings.audioEngine = audioEngine;
    }

    /**
     * Does an instance of AudioSettings exist
     *
     * @return does an instance exist
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    /**
     * Focus the current instance is it exist
     */
    public static void focusInstance() {
        if (instance != null) {
            instance.stage.setIconified(false);
            instance.stage.requestFocus();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> APU_2A03.setVolume(newValue.intValue() / 100.0));
        soundQualitySlider.valueProperty().addListener((observableValue, oldValue, newValue) -> Oscillator.setHarmonics(newValue.intValue() + 5));
        audioSampleSkipSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> APU_2A03.setSampleSkip(newValue.intValue()));
        volumeSlider.setValue(APU_2A03.getVolume() * 100);
        soundQualitySlider.setValue(Oscillator.getHarmonics() - 5);
        audioSampleSkipSlider.setValue(0);
        audioRenderingCheck.setSelected(emulator.isAudioRenderingEnabled());
        rawAudioCheck.setSelected(emulator.isRAWAudioEnabled());
        pulse1Checkbox.setSelected(emulator.isPulse1Rendered());
        pulse2Checkbox.setSelected(emulator.isPulse2Rendered());
        triangleCheckbox.setSelected(emulator.isTriangleRendered());
        noiseCheckbox.setSelected(emulator.isNoiseRendered());
        dmcCheckbox.setSelected(emulator.isDMCRendered());
        linearCheck.setSelected(emulator.isLinear());
        audioOutCombo.getItems().addAll(audioEngine.getValidOutputs());
        audioOutCombo.getSelectionModel().select(audioEngine.getSelectedOutput());
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
     * Will trigger a linear event the Emulator
     */
    @FXML
    public void fireLinearEvent() {
        emulator.linearEvent(linearCheck.isSelected());
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

    @FXML
    public void switchAudioOutput() throws InterruptedException {
        audioEngine.stop();
        Thread.sleep(500);
        if (audioOutCombo.getSelectionModel().getSelectedItem() != null) {
            audioEngine.selectIO(audioOutCombo.getSelectionModel().getSelectedItem());
        }
        audioEngine.start();
    }
}

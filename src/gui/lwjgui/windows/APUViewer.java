package gui.lwjgui.windows;

import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.AudioSampleCollection;
import utils.Dialogs;

import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * This class represent the APU Debug Window
 */
public class APUViewer extends Application implements Initializable {

    private static APUViewer instance;

    private final NEmuSContext emulator;
    private Stage stage;

    @FXML
    private Canvas pulse_1_canvas;
    @FXML
    private Canvas pulse_2_canvas;
    @FXML
    private Canvas triangle_canvas;
    @FXML
    private Canvas noise_canvas;
    @FXML
    private Canvas dmc_canvas;
    @FXML
    private Canvas mixer_canvas;

    /**
     * Create a new instance of PPUViewer
     */
    public APUViewer() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    /**
     * Does an instance of APUViewer exist
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
        pulse_1_canvas.getGraphicsContext2D().fillRect(0, 0, pulse_1_canvas.getWidth(), pulse_1_canvas.getHeight());
        pulse_2_canvas.getGraphicsContext2D().fillRect(0, 0, pulse_2_canvas.getWidth(), pulse_2_canvas.getHeight());
        triangle_canvas.getGraphicsContext2D().fillRect(0, 0, triangle_canvas.getWidth(), triangle_canvas.getHeight());
        noise_canvas.getGraphicsContext2D().fillRect(0, 0, noise_canvas.getWidth(), noise_canvas.getHeight());
        dmc_canvas.getGraphicsContext2D().fillRect(0, 0, dmc_canvas.getWidth(), dmc_canvas.getHeight());
        mixer_canvas.getGraphicsContext2D().fillRect(0, 0, mixer_canvas.getWidth(), mixer_canvas.getHeight());
        new Thread(this::updateImages).start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("APUViewer.fxml")));
        stage.setScene(scene);
        stage.setTitle("APU Viewer");
        //TODO Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        instance.stage = stage;
    }

    /**
     * The rendering loop of the window
     * run until the window is closed
     */
    private void updateImages() {
        while(instance != null) {
            if (emulator.isEmulationRunning()) {
                Platform.runLater(() -> {
                    Queue<AudioSampleCollection> samples = emulator.getNes().getApu().getAudioVisualizerQueue();
                    pulse_1_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    pulse_2_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    triangle_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    noise_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    dmc_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    mixer_canvas.getGraphicsContext2D().fillRect(0, 0, 512, 100);
                    pulse_1_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    pulse_2_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    triangle_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    noise_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    dmc_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    mixer_canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                    int index = 0;
                    AudioSampleCollection last = samples.poll();
                    for (AudioSampleCollection sample : samples) {
                        if (last != null) {
                            pulse_1_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.pulse1 * 110, index + 2, 90 - sample.pulse1 * 110);
                            pulse_2_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.pulse2 * 110, index + 2, 90 - sample.pulse2 * 110);
                            triangle_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.triangle * 110, index + 2, 90 - sample.triangle * 110);
                            noise_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.noise * 110, index + 2, 90 - sample.noise * 110);
                            dmc_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.dmc * 110, index + 2, 90 - sample.dmc * 110);
                            mixer_canvas.getGraphicsContext2D().strokeLine(index, 90 - last.mixer * 110, index + 2, 90 - sample.mixer * 110);
                        }
                        index += 2;
                        last = sample;
                    }
                });
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Dialogs.showError("CPU Viewer Loop Error", "Error while drawing CPU Viewer");
            }
        }
    }
}

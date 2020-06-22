package gui.lwjgui.windows;

import core.NES;
import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;

public class APUViewer extends Application  implements Initializable {

    private static APUViewer instance;

    private NEmuSWindow emulator;
    private NES nes;
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

    public APUViewer() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
        this.nes = emulator.getNes();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void focusInstance() {
        instance.stage.setIconified(false);
        instance.stage.requestFocus();
    }

    public static APUViewer getInstance() {
        return instance;
    }

    /**
     * Initialize the Settings Window
     */
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

    private void updateImages() {
        long next_frame = 0;
        while(instance != null) {
            if (emulator.isEmulationRunning() && System.currentTimeMillis() >= next_frame) {
                next_frame = System.currentTimeMillis() + 33;
                Platform.runLater(() -> {
                    drawWaveForm(nes.getApu().getPulse1VisualizerQueue(), pulse_1_canvas);
                    drawWaveForm(nes.getApu().getPulse2VisualizerQueue(), pulse_2_canvas);
                    drawWaveForm(nes.getApu().getTriangleVisualizerQueue(), triangle_canvas);
                    drawWaveForm(nes.getApu().getNoiseVisualizerQueue(), noise_canvas);
                    drawWaveForm(nes.getApu().getDmcVisualizerQueue(), dmc_canvas);
                    drawWaveForm(nes.getApu().getMixerVisualizerQueue(), mixer_canvas);

                });
            }
        }
    }

    private void drawWaveForm(Queue<Double> waveform, Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 512, 100);
        g.setStroke(Color.WHITE);
        int index = 0;
        Double last = waveform.poll();
        for (double sample : waveform) {
            if (last != null)
                g.strokeLine(index, 90 - last * 110, index+4, 90 - sample*110);
            index += 4;
            last = sample;
        }
    }
}

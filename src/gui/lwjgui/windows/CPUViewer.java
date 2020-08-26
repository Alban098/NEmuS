package gui.lwjgui.windows;

import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class represent the CPU Debug Window
 */
public class CPUViewer extends Application implements Initializable {

    private static CPUViewer instance;

    private final NEmuSContext emulator;
    private Stage stage;
    private Label[][] ram_fields;
    private Label[][] code_fields;
    private Map<Integer, String> decompiled;

    @FXML
    private GridPane ram_area;
    @FXML
    private ScrollBar ram_scroll;
    @FXML
    private Tab ram_tab;
    @FXML
    private Tab cpu_tab;
    @FXML
    private GridPane code_area;
    @FXML
    private TextField y_field;
    @FXML
    private TextField stkp_field;
    @FXML
    private TextField pc_field;
    @FXML
    private TextField a_field;
    @FXML
    private TextField x_field;
    @FXML
    private Label n_label;
    @FXML
    private Label v_label;
    @FXML
    private Label u_label;
    @FXML
    private Label b_label;
    @FXML
    private Label d_label;
    @FXML
    private Label i_label;
    @FXML
    private Label z_label;
    @FXML
    private Label c_label;

    private boolean redraw;

    /**
     * Create a new instance of CPUViewer
     */
    public CPUViewer() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    /**
     * Does an instance of CPUViewer exist
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
        code_fields = new Label[code_area.getRowCount()][7];
        ram_fields = new Label[17][16];
        for (int i = 1; i <= 0x10; i++) {
            Label t1 = new Label(String.format("%02X", i - 1));
            t1.setStyle("-fx-background-color: -fx-control-inner-background; -fx-text-fill: blue; -fx-font-size: 14; -fx-padding: 0; -fx-font-family: monospace");
            Label t2 = new Label(String.format("%04X", (i - 1) << 4));
            t2.setStyle("-fx-background-color: -fx-control-inner-background; -fx-text-fill: blue; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            ram_fields[0][i - 1] = t2;
            ram_area.add(t1, i, 0);
            ram_area.add(t2, 0, i);
        }
        for (int i = 1; i <= 0x10; i++) {
            for (int j = 1; j <= 0x10; j++) {
                Label field = new Label("00");
                field.setStyle("-fx-background-color: -fx-control-inner-background; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
                ram_area.add(field, i, j);
                ram_fields[i][j - 1] = field;
            }
        }
        ram_area.setStyle("-fx-background-color: -fx-control-inner-background;");

        for (int i = 0; i < code_area.getRowCount(); i++) {
            Label addr = new Label(String.format("%04X", i));
            addr.setStyle("-fx-text-fill: #007306; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label code = new Label("BRK");
            code.setStyle("-fx-text-fill: #0000ff; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label operand = new Label("$00");
            operand.setStyle("-fx-text-fill: #0000ff; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label mode = new Label("{IMM}");
            mode.setStyle("-fx-text-fill: #0000ff; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label hex1 = new Label("00");
            hex1.setStyle("-fx-text-fill: #000000; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label hex2 = new Label("00");
            hex2.setStyle("-fx-text-fill: #000000; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            Label hex3 = new Label("");
            hex3.setStyle("-fx-text-fill: #000000; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
            code_area.add(addr, 0, i);
            code_area.add(code, 1, i);
            code_area.add(operand, 2, i);
            code_area.add(mode, 3, i);
            code_area.add(hex1, 4, i);
            code_area.add(hex2, 5, i);
            code_area.add(hex3, 6, i);
            code_fields[i] = new Label[]{addr, code, operand, mode, hex1, hex2, hex3};
        }
        code_fields[0][0].setStyle("-fx-background-color: #ffff00; -fx-font-size: 14; -fx-padding: 0;-fx-font-family: monospace");
        new Thread(this::updateRAM).start();
    }

    /**
     * The rendering loop of the window
     * run until the window is closed
     */
    private void updateRAM() {
        while(instance != null) {
            if (emulator.isEmulationRunning() || redraw && emulator.isStarted()) {
                redraw = false;
                Platform.runLater(() -> {
                    if (ram_tab.isSelected()) {
                        int base_addr = (int) (ram_scroll.getValue()) << 4;
                        for (int i = 1; i <= 0x10; i++) {
                            for (int j = 1; j <= 0x10; j++) {
                                ram_fields[i][j - 1].setText(String.format("%02X", emulator.getNes().cpuRead(base_addr + 0x10 * (j - 1) + i, true)));
                            }
                        }
                        for (int i = 0; i < 0x10; i++)
                            ram_fields[0][i].setText(String.format("%04X", base_addr + (i << 4)));
                    }
                });
                if (cpu_tab.isSelected()) {
                    final int pc = emulator.getNes().getCpu().getProgramCounter();
                    final int a = emulator.getNes().getCpu().getAccumulator();
                    final int x = emulator.getNes().getCpu().getXRegister();
                    final int y = emulator.getNes().getCpu().getYRegister();
                    final int stkp = emulator.getNes().getCpu().getStackPointer();
                    final int status = emulator.getNes().getCpu().getStatus();
                    decompiled = emulator.getNes().getCpu().disassemble(pc, 15, "!");
                    Platform.runLater(() -> {
                        a_field.setText(String.format("$%02X", a) + "[" + a + "]");
                        x_field.setText(String.format("$%02X", x) + "[" + x + "]");
                        y_field.setText(String.format("$%02X", y) + "[" + y + "]");
                        stkp_field.setText(String.format("$%02X", stkp) + "[" + stkp + "]");
                        pc_field.setText(String.format("$%02X", pc) + "[" + pc + "]");

                        n_label.setStyle("-fx-text-fill: " + ((status & 0x80) == 0x80 ? "green" : "red"));
                        v_label.setStyle("-fx-text-fill: " + ((status & 0x40) == 0x40 ? "green" : "red"));
                        u_label.setStyle("-fx-text-fill: " + ((status & 0x20) == 0x20 ? "green" : "red"));
                        b_label.setStyle("-fx-text-fill: " + ((status & 0x10) == 0x10 ? "green" : "red"));
                        d_label.setStyle("-fx-text-fill: " + ((status & 0x8) == 0x8 ? "green" : "red"));
                        i_label.setStyle("-fx-text-fill: " + ((status & 0x4) == 0x4 ? "green" : "red"));
                        z_label.setStyle("-fx-text-fill: " + ((status & 0x2) == 0x2 ? "green" : "red"));
                        c_label.setStyle("-fx-text-fill: " + ((status & 0x1) == 0x1 ? "green" : "red"));

                        if (decompiled.get(pc) != null) {
                            int i = 0;
                            for (String line : decompiled.values()) {
                                String[] split = line.split("!");
                                code_fields[i][0].setText(split[0]);
                                code_fields[i][1].setText(split[1]);
                                code_fields[i][2].setText(split[2]);
                                code_fields[i][3].setText(split[3]);
                                code_fields[i][4].setText(split[4]);
                                code_fields[i][5].setText(split.length > 5 ? split[5] : "");
                                code_fields[i][6].setText(split.length > 6 ? split[6] : "");
                                switch (split[split.length - 1]) {
                                    case "0" -> {
                                        code_fields[i][1].setStyle("-fx-text-fill: #0000ff");
                                        code_fields[i][2].setStyle("-fx-text-fill: #0000ff");
                                        code_fields[i][3].setStyle("-fx-text-fill: #0000ff");
                                    }
                                    case "1" -> {
                                        code_fields[i][1].setStyle("-fx-text-fill: #ff00ff");
                                        code_fields[i][2].setStyle("-fx-text-fill: #ff00ff");
                                        code_fields[i][3].setStyle("-fx-text-fill: #ff00ff");
                                    }
                                    case "2" -> {
                                        code_fields[i][1].setStyle("-fx-text-fill: #ff0000");
                                        code_fields[i][2].setStyle("-fx-text-fill: #ff0000");
                                        code_fields[i][3].setStyle("-fx-text-fill: #ff0000");
                                    }
                                }
                                i++;
                            }
                        }
                    });
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("CPUViewer.fxml")));
        stage.setScene(scene);
        stage.setTitle("CPU Viewer");
        //TODO Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        instance.stage = stage;
    }

    /**
     * Will trigger a CPU instruction event to the Emulator
     */
    @FXML
    public void cpuStepEvent() {
        emulator.cpuStepEvent();
        redraw = true;
    }
    /**
     * Will trigger a frame advance event to the Emulator
     */
    @FXML
    public void frameStepEvent() {
        emulator.frameStepEvent();
        redraw = true;
    }

    /**
     * Trigger a redraw of the window
     */
    @FXML
    private void scrollEvent() {
        redraw = true;
    }
}

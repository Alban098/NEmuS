package gui.lwjgui.windows;

import core.NES;
import core.ppu.registers.ObjectAttribute;
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
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class PPUViewer extends Application implements Initializable {

    private static PPUViewer instance;

    private NEmuSWindow emulator;
    private NES nes;
    private Stage stage;
    private Image nametable1_render_target;
    private Image nametable2_render_target;
    private Image nametable3_render_target;
    private Image nametable4_render_target;
    private Image patterntable1_render_target;
    private Image patterntable2_render_target;
    private int selected_palette = 0x00;

    @FXML
    private Canvas nt_1_canvas;
    @FXML
    private Canvas nt_2_canvas;
    @FXML
    private Canvas nt_3_canvas;
    @FXML
    private Canvas nt_4_canvas;
    @FXML
    private Canvas pt_1_canvas;
    @FXML
    private Canvas pt_2_canvas;
    @FXML
    private Canvas palette_1_canvas;
    @FXML
    private Canvas palette_2_canvas;
    @FXML
    private Canvas palette_3_canvas;
    @FXML
    private Canvas palette_4_canvas;
    @FXML
    private Canvas palette_5_canvas;
    @FXML
    private Canvas palette_6_canvas;
    @FXML
    private Canvas palette_7_canvas;
    @FXML
    private Canvas palette_8_canvas;
    @FXML
    private Tab nt_tab;
    @FXML
    private Tab pt_tab;
    @FXML
    private Tab oam_tab;
    @FXML
    private ListView<String> oam_list;
    @FXML
    private Canvas oam_canvas;

    private Canvas[] palette_images;

    public PPUViewer() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
        this.nes = emulator.getNes();
        patterntable1_render_target = new WritableImage(128, 128);
        patterntable2_render_target = new WritableImage(128, 128);
        nametable1_render_target = new WritableImage(256, 240);
        nametable2_render_target = new WritableImage(256, 240);
        nametable3_render_target = new WritableImage(256, 240);
        nametable4_render_target = new WritableImage(256, 240);
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void focusInstance() {
        instance.stage.setIconified(false);
        instance.stage.requestFocus();
    }

    public static PPUViewer getInstance() {
        return instance;
    }

    /**
     * Initialize the Settings Window
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        palette_images = new Canvas[]{palette_1_canvas, palette_2_canvas, palette_3_canvas, palette_4_canvas, palette_5_canvas, palette_6_canvas, palette_7_canvas, palette_8_canvas};
        instance = this;
        for (int i = 0; i < palette_images.length; i++ ) {
            final int finalI = i;
            palette_images[i].getGraphicsContext2D().fillRect(0, 0, palette_images[i].getWidth(), palette_images[i].getHeight());
            palette_images[i].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> selected_palette = finalI);
        }
        nt_1_canvas.getGraphicsContext2D().fillRect(0, 0, nt_1_canvas.getWidth(), nt_1_canvas.getHeight());
        nt_2_canvas.getGraphicsContext2D().fillRect(0, 0, nt_2_canvas.getWidth(), nt_2_canvas.getHeight());
        nt_3_canvas.getGraphicsContext2D().fillRect(0, 0, nt_3_canvas.getWidth(), nt_3_canvas.getHeight());
        nt_4_canvas.getGraphicsContext2D().fillRect(0, 0, nt_4_canvas.getWidth(), nt_4_canvas.getHeight());
        pt_1_canvas.getGraphicsContext2D().fillRect(0, 0, pt_1_canvas.getWidth(), pt_1_canvas.getHeight());
        pt_2_canvas.getGraphicsContext2D().fillRect(0, 0, pt_2_canvas.getWidth(), pt_2_canvas.getHeight());
        oam_canvas.getGraphicsContext2D().fillRect(0, 0, oam_canvas.getWidth(), oam_canvas.getHeight());
        new Thread(this::updateImages).start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("PPUViewer.fxml")));
        stage.setScene(scene);
        stage.setTitle("PPU Viewer");
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
                next_frame = System.currentTimeMillis() + 100;
                Platform.runLater(() -> {
                    if (nt_tab.isSelected()) {
                        nes.getPpu().getNametable(0, (WritableImage) nametable1_render_target);
                        nes.getPpu().getNametable(1, (WritableImage) nametable2_render_target);
                        nes.getPpu().getNametable(2, (WritableImage) nametable3_render_target);
                        nes.getPpu().getNametable(3, (WritableImage) nametable4_render_target);
                        nt_1_canvas.getGraphicsContext2D().drawImage(nametable1_render_target, 0, 0);
                        nt_2_canvas.getGraphicsContext2D().drawImage(nametable2_render_target, 0, 0);
                        nt_3_canvas.getGraphicsContext2D().drawImage(nametable3_render_target, 0, 0);
                        nt_4_canvas.getGraphicsContext2D().drawImage(nametable4_render_target, 0, 0);
                    } else if (pt_tab.isSelected()) {
                        nes.getPpu().getPatternTable(0, selected_palette, (WritableImage) patterntable1_render_target);
                        nes.getPpu().getPatternTable(1, selected_palette, (WritableImage) patterntable2_render_target);
                        pt_1_canvas.getGraphicsContext2D().drawImage(patterntable1_render_target, 0, 0, 256, 256);
                        pt_2_canvas.getGraphicsContext2D().drawImage(patterntable2_render_target, 0, 0, 256, 256);
                        for (int i = 0; i < 8; i++) {
                            GraphicsContext g = palette_images[i].getGraphicsContext2D();
                            for (int j = 0; j < 4; j++) {
                                g.setFill(nes.getPpu().threadSafeGetColorFromPalette(i, j));
                                g.fillRect((j & 1) == 0 ? 0 : palette_images[i].getWidth()/2, (j & 2) == 0 ? 0 : palette_images[i].getHeight()/2, palette_images[i].getWidth()/2, palette_images[i].getHeight()/2);
                            }
                            if (i == selected_palette) {
                                g.setFill(Color.RED);
                                g.fillRect(0, 0, palette_images[i].getWidth(),6);
                                g.fillRect(0, palette_images[i].getHeight() - 6, palette_images[i].getWidth(),6);
                                g.fillRect(0, 0, 6,palette_images[i].getHeight());
                                g.fillRect(palette_images[i].getWidth() - 6, 0, 6,palette_images[i].getHeight());
                            }
                        }
                    } else if (oam_tab.isSelected()) {
                        int selectedIndex = oam_list.getSelectionModel().getSelectedIndex();
                        oam_list.getItems().clear();
                        GraphicsContext g = oam_canvas.getGraphicsContext2D();
                        int control = nes.getPpu().cpuRead(0, true);
                        switch (control & 0x20) {
                            case 0x00: // 8x8
                                for (int i = 0; i < 64; i++) {
                                    ObjectAttribute entry = nes.getPpu().getOams()[i];
                                    oam_list.getItems().add(String.format("%02X:", i) + " (" + String.format("%03d", entry.getX()) + ", " + String.format("%03d", entry.getY()) + ") ID: " + String.format("%02X", entry.getId()) + " AT: " + String.format("%02X", entry.getAttribute()));
                                    for (int row = 0; row < 8; row++) {
                                        int sprite_pattern_low, sprite_pattern_high;
                                        int sprite_pattern_addr_low, sprite_pattern_addr_high;
                                        if ((entry.getAttribute() & 0x80) != 0x80)
                                            sprite_pattern_addr_low = (((control & 0x20) == 0x20) ? 1 << 12 : 0) | (entry.getId() << 4) | row;
                                        else
                                            sprite_pattern_addr_low = (((control & 0x20) == 0x20) ? 1 << 12 : 0) | (entry.getId() << 4) | (7 - row);

                                        sprite_pattern_addr_high = (sprite_pattern_addr_low + 8) & 0xFFFF;
                                        sprite_pattern_low = nes.getPpu().ppuDebugRead(sprite_pattern_addr_low);
                                        sprite_pattern_high = nes.getPpu().ppuDebugRead(sprite_pattern_addr_high);

                                        if ((entry.getAttribute() & 0x40) == 0x40) {
                                            sprite_pattern_low = NumberUtils.byteFlip(sprite_pattern_low);
                                            sprite_pattern_high = NumberUtils.byteFlip(sprite_pattern_high);
                                        }
                                        for (int col = 0; col < 8; col++) {
                                            int px = ((sprite_pattern_low & 0x80) == 0x80 ? 0x1 : 0x0) | ((((sprite_pattern_high & 0x80) == 0x80 ? 0x1 : 0x0)) << 1);
                                            int pal = (entry.getAttribute() & 0x3) + 4;
                                            g.setFill(nes.getPpu().threadSafeGetColorFromPalette(px == 0 ? 0 : pal, px));
                                            g.fillRect(((i & 0x7) << 5) | (col << 2), ((i >> 3) << 5) | (row << 2), 4, 4);
                                            sprite_pattern_high <<= 1;
                                            sprite_pattern_low <<= 1;
                                        }
                                    }
                                    if (i == selectedIndex) {
                                        g.setFill(Color.RED);
                                        int x = (i % 8) * (8*4);
                                        int y = (i / 8) * (8*4);
                                        g.fillRect(x, y, 32, 3);
                                        g.fillRect(x, y + 29, 32,3);
                                        g.fillRect(x, y, 3, 32);
                                        g.fillRect(x + 29, y, 3, 32);
                                    }
                                }
                                break;
                            case 0x20: // 8x16
                                for (int i = 0; i < 64; i++) {
                                    ObjectAttribute entry = nes.getPpu().getOams()[i];
                                    oam_list.getItems().add(String.format("%02X:", i) + " (" + String.format("%03d", entry.getX()) + ", " + String.format("%03d", entry.getY()) + ") ID: " + String.format("%02X", entry.getId()) + " AT: " + String.format("%02X", entry.getAttribute()));
                                    for (int row = 0; row < 16; row++) {
                                        int sprite_pattern_low, sprite_pattern_high;
                                        int sprite_pattern_addr_low, sprite_pattern_addr_high;
                                        if ((entry.getAttribute() & 0x80) != 0x80) {
                                            if (row < 8)
                                                sprite_pattern_addr_low = ((entry.getId() & 0x1) << 12) | ((entry.getId() & 0xFE) << 4) | row;
                                            else
                                                sprite_pattern_addr_low = ((entry.getId() & 0x1) << 12) | (((entry.getId() & 0xFE) + 1) << 4) | (row - 8);
                                        } else {
                                            if (row < 8)
                                                sprite_pattern_addr_low = ((entry.getId() & 0x1) << 12) | (((entry.getId() & 0xFE) + 1) << 4) | (7 - row);
                                            else
                                                sprite_pattern_addr_low = ((entry.getId() & 0x1) << 12) | ((entry.getId() & 0xFE) << 4) | (7 - row + 8);
                                        }
                                        sprite_pattern_addr_high = (sprite_pattern_addr_low + 8) & 0xFFFF;
                                        sprite_pattern_low = nes.getPpu().ppuDebugRead(sprite_pattern_addr_low);
                                        sprite_pattern_high = nes.getPpu().ppuDebugRead(sprite_pattern_addr_high);

                                        if ((entry.getAttribute() & 0x40) == 0x40) {
                                            sprite_pattern_low = NumberUtils.byteFlip(sprite_pattern_low);
                                            sprite_pattern_high = NumberUtils.byteFlip(sprite_pattern_high);
                                        }
                                        for (int col = 0; col < 8; col++) {
                                            int px = ((sprite_pattern_low & 0x80) == 0x80 ? 0x1 : 0x0) | ((((sprite_pattern_high & 0x80) == 0x80 ? 0x1 : 0x0)) << 1);
                                            int pal = (entry.getAttribute() & 0x3) + 4;
                                            g.setFill(nes.getPpu().threadSafeGetColorFromPalette(px == 0 ? 0 : pal, px));
                                            g.fillRect(((i & 0x7) << 5) | (col << 2), ((i >> 3) << 6) | (row << 2), 4, 4);

                                            sprite_pattern_high <<= 1;
                                            sprite_pattern_low <<= 1;
                                        }
                                    }
                                    if (i == selectedIndex) {
                                        g.setFill(Color.RED);
                                        int x = (i % 8) * (8*4);
                                        int y = (i / 8) * (16 * 4);
                                        g.fillRect(x, y, 32, 3);
                                        g.fillRect(x, y + 61, 32,3);
                                        g.fillRect(x, y, 3, 64);
                                        g.fillRect(x + 29, y, 3, 64);
                                    }
                                }
                                break;
                        }
                        oam_list.getSelectionModel().select(selectedIndex);
                    }
                });
            }
        }
    }

    @FXML
    private void selectOAM(MouseEvent event) {
        int x = (int)event.getX() >> 5;
        int y = (int)event.getY() >> 5;
        if ((nes.getPpu().cpuRead(0, true) & 0x20) == 0x20)
            y >>= 1;
        oam_list.getSelectionModel().clearSelection();
        oam_list.getSelectionModel().select((y << 3) | x);
        oam_list.scrollTo((y << 3) | x);
    }
}

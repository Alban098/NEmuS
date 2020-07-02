package gui.lwjgui.windows;

import core.NES;
import core.ppu.registers.ObjectAttribute;
import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Dialogs;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class represent the PPU Debug Window
 */
public class PPUViewer extends Application implements Initializable {

    private static PPUViewer instance;

    private final NEmuSContext emulator;
    private final NES nes;
    private Stage stage;
    private final Image nametable1_render_target;
    private final Image nametable2_render_target;
    private final Image nametable3_render_target;
    private final Image nametable4_render_target;
    private final Image patterntable1_render_target;
    private final Image patterntable2_render_target;
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
    @FXML
    private Canvas preview_canvas;

    private Canvas[] palette_images;
    private Tooltip tooltip;

    /**
     * Create a new instance of PPUViewer
     */
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

    /**
     * Does an instance of PPUViewer exist
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
        palette_images = new Canvas[]{palette_1_canvas, palette_2_canvas, palette_3_canvas, palette_4_canvas, palette_5_canvas, palette_6_canvas, palette_7_canvas, palette_8_canvas};
        instance = this;
        for (int i = 0; i < palette_images.length; i++ ) {
            final int finalI = i;
            palette_images[i].getGraphicsContext2D().fillRect(0, 0, palette_images[i].getWidth(), palette_images[i].getHeight());
            palette_images[i].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> selected_palette = finalI);
        }

        tooltip = new Tooltip();
        tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Label tooltip_label = new Label();
        tooltip_label.setMaxWidth(130);
        tooltip_label.setMinWidth(130);
        tooltip_label.setStyle("-fx-font-size: 14px;");
        HBox tooltip_root = new HBox();
        Canvas tooltip_canvas = new Canvas(64, 64);
        tooltip_root.getChildren().add(tooltip_label);
        tooltip_root.getChildren().add(tooltip_canvas);
        tooltip.setGraphic(tooltip_root);


        nt_1_canvas.getGraphicsContext2D().fillRect(0, 0, nt_1_canvas.getWidth(), nt_1_canvas.getHeight());
        nt_2_canvas.getGraphicsContext2D().fillRect(0, 0, nt_2_canvas.getWidth(), nt_2_canvas.getHeight());
        nt_3_canvas.getGraphicsContext2D().fillRect(0, 0, nt_3_canvas.getWidth(), nt_3_canvas.getHeight());
        nt_4_canvas.getGraphicsContext2D().fillRect(0, 0, nt_4_canvas.getWidth(), nt_4_canvas.getHeight());
        pt_1_canvas.getGraphicsContext2D().fillRect(0, 0, pt_1_canvas.getWidth(), pt_1_canvas.getHeight());
        pt_2_canvas.getGraphicsContext2D().fillRect(0, 0, pt_2_canvas.getWidth(), pt_2_canvas.getHeight());
        oam_canvas.getGraphicsContext2D().fillRect(0, 0, oam_canvas.getWidth(), oam_canvas.getHeight());
        preview_canvas.getGraphicsContext2D().fillRect(0, 0, preview_canvas.getWidth(), preview_canvas.getHeight());

        nt_tab.getContent().addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (emulator.isEmulationRunning()) {
                Tile tile = null;
                if (event.getX() > nt_1_canvas.getLayoutX() && event.getX() < nt_1_canvas.getLayoutX() + nt_1_canvas.getWidth() && event.getY() > nt_1_canvas.getLayoutY() && event.getY() < nt_1_canvas.getLayoutY() + nt_1_canvas.getHeight()) {
                    tile = nes.getPpu().getNametableTile((int) (event.getX() - nt_1_canvas.getLayoutX()) / 8, (int) (event.getY() - nt_1_canvas.getLayoutY()) / 8, 0);
                    tooltip.show(nt_tab.getContent(), event.getScreenX() - event.getX() + nt_1_canvas.getLayoutX() + nt_1_canvas.getWidth() + 3, event.getScreenY());
                } else if (event.getX() > nt_2_canvas.getLayoutX() && event.getX() < nt_2_canvas.getLayoutX() + nt_2_canvas.getWidth() && event.getY() > nt_2_canvas.getLayoutY() && event.getY() < nt_2_canvas.getLayoutY() + nt_2_canvas.getHeight()) {
                    tile = nes.getPpu().getNametableTile((int) (event.getX() - nt_2_canvas.getLayoutX()) / 8, (int) (event.getY() - nt_2_canvas.getLayoutY()) / 8, 1);
                    tooltip.show(nt_tab.getContent(), event.getScreenX() - event.getX() + nt_2_canvas.getLayoutX() - tooltip.getWidth() + 15, event.getScreenY());
                } else if (event.getX() > nt_3_canvas.getLayoutX() && event.getX() < nt_3_canvas.getLayoutX() + nt_3_canvas.getWidth() && event.getY() > nt_3_canvas.getLayoutY() && event.getY() < nt_3_canvas.getLayoutY() + nt_3_canvas.getHeight()) {
                    tile = nes.getPpu().getNametableTile((int) (event.getX() - nt_3_canvas.getLayoutX()) / 8, (int) (event.getY() - nt_3_canvas.getLayoutY()) / 8, 2);
                    tooltip.show(nt_tab.getContent(), event.getScreenX() - event.getX() + nt_3_canvas.getLayoutX() + nt_3_canvas.getWidth() + 3, event.getScreenY());
                } else if (event.getX() > nt_4_canvas.getLayoutX() && event.getX() < nt_4_canvas.getLayoutX() + nt_4_canvas.getWidth() && event.getY() > nt_4_canvas.getLayoutY() && event.getY() < nt_4_canvas.getLayoutY() + nt_4_canvas.getHeight()) {
                    tile = nes.getPpu().getNametableTile((int) (event.getX() - nt_4_canvas.getLayoutX()) / 8, (int) (event.getY() - nt_4_canvas.getLayoutY()) / 8, 3);
                    tooltip.show(nt_tab.getContent(), event.getScreenX() - event.getX() + nt_4_canvas.getLayoutX() - tooltip.getWidth() + 15, event.getScreenY());
                } else
                    tooltip.hide();
                if (tile != null) {
                    tooltip_label.setText(
                            "Tile : " + String.format("$%04X : ", tile.addr) + String.format("$%02X", tile.tile) +
                            "\nPosition : " + tile.x + ", " + tile.y +
                            "\nAttributes : " + String.format("$%02X", tile.attribute) +
                            "\nPalette : " + tile.palette
                    );
                    tooltip_canvas.setHeight(64);
                    int x = 0, y = 0;
                    for (Color c : tile.colors) {
                        tooltip_canvas.getGraphicsContext2D().setFill(c);
                        tooltip_canvas.getGraphicsContext2D().fillRect(x * 8, y * 8, 8, 8);
                        x++;
                        if (x >= 8) {
                            y++;
                            x = 0;
                        }
                    }
                }
            }
        });
        pt_tab.getContent().addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (emulator.isEmulationRunning()) {
                Tile tile = null;
                if (event.getX() > pt_1_canvas.getLayoutX() && event.getX() < pt_1_canvas.getLayoutX() + pt_1_canvas.getWidth() && event.getY() > pt_1_canvas.getLayoutY() && event.getY() < pt_1_canvas.getLayoutY() + pt_1_canvas.getHeight()) {
                    tile = nes.getPpu().getPatterntableTile((int) (event.getX() - pt_1_canvas.getLayoutX()) / 16, (int) (event.getY() - pt_1_canvas.getLayoutY()) / 16, selected_palette, 0);
                    tooltip.show(pt_tab.getContent(), event.getScreenX() - event.getX() + pt_1_canvas.getLayoutX() + pt_1_canvas.getWidth() + 3, event.getScreenY());
                } else if (event.getX() > pt_2_canvas.getLayoutX() && event.getX() < pt_2_canvas.getLayoutX() + pt_2_canvas.getWidth() && event.getY() > pt_2_canvas.getLayoutY() && event.getY() < pt_2_canvas.getLayoutY() + pt_2_canvas.getHeight()) {
                    tile = nes.getPpu().getPatterntableTile((int) (event.getX() - pt_2_canvas.getLayoutX()) / 16, (int) (event.getY() - pt_2_canvas.getLayoutY()) / 16, selected_palette, 1);
                    tooltip.show(pt_tab.getContent(), event.getScreenX() - event.getX() + pt_2_canvas.getLayoutX() - tooltip.getWidth() + 15, event.getScreenY());
                } else
                    tooltip.hide();
                if (tile != null) {
                    tooltip_label.setText(
                            "Tile : " + String.format("$%02X", tile.tile) +
                            "\nAddress : " + String.format("$%04X", tile.addr)
                    );
                    tooltip_canvas.setHeight(64);
                    int x = 0, y = 0;
                    for (Color c : tile.colors) {
                        tooltip_canvas.getGraphicsContext2D().setFill(c);
                        tooltip_canvas.getGraphicsContext2D().fillRect(x * 8, y * 8, 8, 8);
                        x++;
                        if (x >= 8) {
                            y++;
                            x = 0;
                        }
                    }
                }
            }
        });

        oam_canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (emulator.isEmulationRunning()) {
                Tile tile;
                if ((nes.getPpu().cpuRead(0, true) & 0x20) == 0x20)
                    tile = nes.getPpu().getOamTile8x16((int) (event.getX() / 32) + (int) (event.getY() / 64) * 8);
                else
                    tile = nes.getPpu().getOamTile8x8((int) (event.getX() / 32) + (int) (event.getY() / 32) * 8);
                if (tile != null) {
                    tooltip.show(oam_tab.getContent(), event.getScreenX() - event.getX() - tooltip.getWidth() + 15, event.getScreenY());
                    int sprite_x = (int) event.getX() >> 5;
                    int sprite_y = (int) event.getY() >> 5;
                    if ((nes.getPpu().cpuRead(0, true) & 0x20) == 0x20)
                        sprite_y >>= 1;
                    oam_list.getSelectionModel().clearSelection();
                    oam_list.getSelectionModel().select((sprite_y << 3) | sprite_x);
                    oam_list.scrollTo((sprite_y << 3) | sprite_x);

                    tooltip_label.setText(
                            "Tile : " + String.format("$%02X", tile.tile) +
                                    "\nAddress : " + String.format("$%04X", tile.addr) +
                                    "\nPosition : " + tile.x + ", " + tile.y +
                                    "\nFlags : " + ((tile.attribute & 0x80) == 0x80 ? "V" : "_") + ((tile.attribute & 0x40) == 0x40 ? "H" : "_") + ((tile.attribute & 0x20) == 0x20 ? "P" : "_") +
                                    "\nPalette : " + tile.palette
                    );
                    int x = 0, y = 0;
                    tooltip_canvas.setHeight((nes.getPpu().cpuRead(0, true) & 0x20) == 0x20 ? 128 : 64);
                    for (Color c : tile.colors) {
                        tooltip_canvas.getGraphicsContext2D().setFill(c);
                        tooltip_canvas.getGraphicsContext2D().fillRect(x * 8, y * 8, 8, 8);
                        x++;
                        if (x >= 8) {
                            y++;
                            x = 0;
                        }
                    }
                } else
                    tooltip.hide();
            }
        });
        oam_canvas.addEventHandler(MouseEvent.MOUSE_EXITED, event -> tooltip.hide());

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

    /**
     * The rendering loop of the window
     * run until the window is closed
     */
    private void updateImages() {
        while(instance != null) {
            if (emulator.isEmulationRunning()) {
                Platform.runLater(() -> {
                    //If the current tab is the nametables one
                    if (nt_tab.isSelected()) {
                        //We retrieve the nametables and draw them
                        nes.getPpu().getNametable(0, (WritableImage) nametable1_render_target);
                        nes.getPpu().getNametable(1, (WritableImage) nametable2_render_target);
                        nes.getPpu().getNametable(2, (WritableImage) nametable3_render_target);
                        nes.getPpu().getNametable(3, (WritableImage) nametable4_render_target);
                        nt_1_canvas.getGraphicsContext2D().drawImage(nametable1_render_target, 0, 0);
                        nt_2_canvas.getGraphicsContext2D().drawImage(nametable2_render_target, 0, 0);
                        nt_3_canvas.getGraphicsContext2D().drawImage(nametable3_render_target, 0, 0);
                        nt_4_canvas.getGraphicsContext2D().drawImage(nametable4_render_target, 0, 0);
                    //If the selected tab is the pattern tables one
                    } else if (pt_tab.isSelected()) {
                        //We retrieve the pattern tables and draw them
                        nes.getPpu().getPatternTable(0, selected_palette, (WritableImage) patterntable1_render_target);
                        nes.getPpu().getPatternTable(1, selected_palette, (WritableImage) patterntable2_render_target);
                        pt_1_canvas.getGraphicsContext2D().drawImage(patterntable1_render_target, 0, 0, 256, 256);
                        pt_2_canvas.getGraphicsContext2D().drawImage(patterntable2_render_target, 0, 0, 256, 256);

                        //For each palette
                        for (int i = 0; i < 8; i++) {
                            //We get the correct canvas
                            GraphicsContext g = palette_images[i].getGraphicsContext2D();
                            //We draw each color
                            for (int j = 0; j < 4; j++) {
                                g.setFill(nes.getPpu().getColorFromPalette(i, j));
                                g.fillRect((j & 1) == 0 ? 0 : palette_images[i].getWidth()/2, (j & 2) == 0 ? 0 : palette_images[i].getHeight()/2, palette_images[i].getWidth()/2, palette_images[i].getHeight()/2);
                            }
                            //If this is the selected palette, we highlight it
                            if (i == selected_palette) {
                                g.setFill(Color.RED);
                                g.fillRect(0, 0, palette_images[i].getWidth(),6);
                                g.fillRect(0, palette_images[i].getHeight() - 6, palette_images[i].getWidth(),6);
                                g.fillRect(0, 0, 6,palette_images[i].getHeight());
                                g.fillRect(palette_images[i].getWidth() - 6, 0, 6,palette_images[i].getHeight());
                            }
                        }
                    //If the current tab is the OAM one
                    } else if (oam_tab.isSelected()) {
                        //We backup the currently selected index and clear the list
                        int selectedIndex = oam_list.getSelectionModel().getSelectedIndex();
                        oam_list.getItems().clear();
                        GraphicsContext g = oam_canvas.getGraphicsContext2D();
                        GraphicsContext preview = preview_canvas.getGraphicsContext2D();
                        preview.setFill(Color.GREY);
                        preview.fillRect(0, 0, preview_canvas.getWidth(), preview_canvas.getHeight());
                        g.setFill(Color.GREY);
                        g.fillRect(0, 0, oam_canvas.getWidth(), oam_canvas.getHeight());
                        //We get the current state of the PPU Control Register
                        int control = nes.getPpu().cpuRead(0, true);
                        switch (control & 0x20) {
                            case 0x00: //If sprite mode is 8x8 px
                                //For each ObjectAttribute
                                for (int i = 0; i < 64; i++) {
                                    //We retrieve the entry
                                    ObjectAttribute entry = nes.getPpu().getOams()[i];
                                    //We populate the list
                                    oam_list.getItems().add(String.format("%02X:", i) + " (" + String.format("%03d", entry.getX()) + ", " + String.format("%03d", entry.getY()) + ") ID: " + String.format("%02X", entry.getId()) + " AT: " + String.format("%02X", entry.getAttribute()));
                                    //For each row of the sprite
                                    Tile tile = nes.getPpu().getOamTile8x8(i);

                                    for (int row = 0; row < 8; row++) {
                                        for (int col = 0; col < 8; col++) {
                                            g.setFill(tile.colors[col | (row << 3)]);
                                            g.fillRect(((i & 0x7) << 5) | (col << 2), ((i >> 3) << 5) | (row << 2), 4, 4);
                                            preview.setFill(tile.colors[col | (row << 3)]);
                                            preview.fillRect(entry.getX() + col, entry.getY() + row, 1, 1);
                                        }
                                    }

                                    //If the current ObjectAttribute is the selected one, we highlight it
                                    if (i == selectedIndex) {
                                        g.setFill(Color.RED);
                                        preview.setFill(Color.RED);
                                        int x = (i % 8) * (8*4);
                                        int y = (i / 8) * (8*4);
                                        g.fillRect(x, y, 32, 3);
                                        g.fillRect(x, y + 29, 32,3);
                                        g.fillRect(x, y, 3, 32);
                                        g.fillRect(x + 29, y, 3, 32);
                                        preview.fillRect(entry.getX(), entry.getY(), 8, 1);
                                        preview.fillRect(entry.getX(), entry.getY() + 7, 8,1);
                                        preview.fillRect(entry.getX(), entry.getY(), 1, 8);
                                        preview.fillRect(entry.getX() + 7, entry.getY(), 1, 8);
                                    }
                                }
                                break;
                            case 0x20: //If sprite mode is 8x16 px
                                //The same as 8x8 mode but the nametable is hardcoded as bit 0 of the Attribute
                                //and we have to check which half of sprite is currently drawn
                                for (int i = 0; i < 64; i++) {
                                    ObjectAttribute entry = nes.getPpu().getOams()[i];
                                    oam_list.getItems().add(String.format("%02X:", i) + " (" + String.format("%03d", entry.getX()) + ", " + String.format("%03d", entry.getY()) + ") ID: " + String.format("%02X", entry.getId()) + " AT: " + String.format("%02X", entry.getAttribute()));

                                    Tile tile = nes.getPpu().getOamTile8x16(i);
                                    for (int row = 0; row < 16; row++) {
                                        for (int col = 0; col < 8; col++) {
                                            g.setFill(tile.colors[col | (row << 3)]);
                                            g.fillRect(((i & 0x7) << 5) | (col << 2), ((i >> 3) << 6) | (row << 2), 4, 4);
                                            preview.setFill(tile.colors[col | (row << 3)]);
                                            preview.fillRect(entry.getX() + col, entry.getY() + row, 1, 1);
                                        }
                                    }
                                    if (i == selectedIndex) {
                                        g.setFill(Color.RED);
                                        preview.setFill(Color.RED);
                                        int x = (i % 8) * (8*4);
                                        int y = (i / 8) * (16 * 4);
                                        g.fillRect(x, y, 32, 3);
                                        g.fillRect(x, y + 61, 32,3);
                                        g.fillRect(x, y, 3, 64);
                                        g.fillRect(x + 29, y, 3, 64);
                                        preview.fillRect(entry.getX(), entry.getY(), 8, 1);
                                        preview.fillRect(entry.getX(), entry.getY() + 15, 8,1);
                                        preview.fillRect(entry.getX(), entry.getY(), 1, 16);
                                        preview.fillRect(entry.getX() + 7, entry.getY(), 1, 16);
                                    }
                                }
                                break;
                        }
                        //We reselect the Object Attribute
                        oam_list.getSelectionModel().select(selectedIndex);
                    }
                });
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Dialogs.showError("PPU Viewer Loop Error", "Error while drawing PPU Viewer");
            }
        }
    }
}

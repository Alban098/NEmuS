package gui.lwjgui;

import core.ppu.PPU_2C02;
import gui.lwjgui.windows.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lwjgui.LWJGUIApplication;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.BorderPane;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import utils.Dialogs;

import java.io.File;

import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;

/**
 * This class represent the GUI of the emulator
 */
public class NEmuSUnified extends LWJGUIApplication {

    private static NEmuSUnified instance;

    private String currentDirectory;
    private Window window;
    private NEmuSContext emulator;
    private MenuBar menu;

    public static void main(String[] args) {
        ModernOpenGL = false;
        launch(args);
    }

    @Override
    public void start(String[] strings, Window window) {
        instance = this;
        currentDirectory = "./";
        window.setTitle("NEmuS Unified");

        //We initialize the emulator context
        this.window = window;
        emulator = new NEmuSContext(window.getContext().getWindowHandle());

        //Just to initialize JavaFX
        new JFXPanel();
        Platform.setImplicitExit(false);

        //We initialize the Menu and its behaviour
        BorderPane root = new BorderPane();
        root.setBackgroundLegacy(null);
        menu = new MenuBar();
        root.setTop(menu);

        MenuItem load = new MenuItem("Open");
        load.setOnAction(actionEvent -> Platform.runLater( () -> {
            FileChooser romLoader = new FileChooser();
            romLoader.setInitialDirectory(new File(currentDirectory));
            romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
            File file = romLoader.showOpenDialog(null);
            if (file != null) {
                currentDirectory = file.getAbsolutePath().replace(file.getName(), "");
                emulator.fireLoadROMEvent(file.getAbsolutePath());
            }
        }));

        MenuItem pause = new MenuItem("Pause/Resume");
        pause.setOnAction(actionEvent -> emulator.pause());

        MenuItem reset = new MenuItem("Reset");
        reset.setOnAction(actionEvent -> emulator.fireResetEvent());

        MenuItem audio = new MenuItem("Audio");
        audio.setOnAction(actionEvent -> {
            if (AudioSettings.hasInstance()) {
                Platform.runLater(AudioSettings::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new AudioSettings().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("Audio Settings Error", "Error opening Audio Settings Window", e);
                    }
                });
            }
        });

        MenuItem controller = new MenuItem("Controllers");
        controller.setOnAction(actionEvent -> {
            if (ControllerSettings.hasInstance()) {
                Platform.runLater(ControllerSettings::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new ControllerSettings().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("Controllers Settings Error", "Error opening Controllers Settings Window", e);
                    }
                });
            }
        });

        MenuItem graphics = new MenuItem("Graphics");
        graphics.setOnAction(actionEvent -> {
            if (GraphicsSettings.hasInstance()) {
                Platform.runLater(GraphicsSettings::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new GraphicsSettings().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("Graphics Settings Error", "Error opening Graphics Settings Window", e);
                    }
                });
            }
        });

        MenuItem ppu = new MenuItem("PPU Viewer");
        ppu.setOnAction(actionEvent -> {
            if (PPUViewer.hasInstance()) {
                Platform.runLater(PPUViewer::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new PPUViewer().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("PPU Viewer Error", "Error opening PPU Viewer Window", e);
                    }
                });
            }
        });

        MenuItem apu = new MenuItem("APU Viewer");
        apu.setOnAction(actionEvent -> {
            if (APUViewer.hasInstance()) {
                Platform.runLater(APUViewer::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new APUViewer().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("APU Viewer Error", "Error opening APU Viewer Window", e);
                    }
                });
            }
        });

        MenuItem cpu = new MenuItem("CPU Viewer");
        cpu.setOnAction(actionEvent -> {
            if (CPUViewer.hasInstance()) {
                Platform.runLater(CPUViewer::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new CPUViewer().start(new Stage());
                    } catch (Exception e) {
                        Dialogs.showException("CPU Viewer Error", "Error opening CPU Viewer Window", e);
                    }
                });
            }
        });

        Menu file = new Menu("File");
        file.getItems().add(load);
        menu.getItems().add(file);

        Menu emulation = new Menu("Emulation");
        emulation.getItems().add(pause);
        emulation.getItems().add(reset);
        menu.getItems().add(emulation);

        Menu settings = new Menu("Settings");
        settings.getItems().add(audio);
        settings.getItems().add(controller);
        settings.getItems().add(graphics);
        menu.getItems().add(settings);

        Menu debug = new Menu("Debug");
        debug.getItems().add(cpu);
        debug.getItems().add(ppu);
        debug.getItems().add(apu);
        menu.getItems().add(debug);

        //We setup the Window
        window.setScene(new Scene(root, PPU_2C02.SCREEN_WIDTH * 2, (int) (PPU_2C02.SCREEN_HEIGHT * 2 + menu.getHeight())));
        glfwSetWindowAspectRatio(window.getContext().getWindowHandle(), window.getContext().getWidth(), window.getContext().getHeight());
        window.show();

        //We link the Window and the emulator context
        window.setRenderingCallback(emulator);
        window.getWindowCloseCallback().addCallback(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                emulator.cleanUp();
                System.exit(0);
            }
        });
    }

    /**
     * Return the current emulator context
     *
     * @return the current emulator context
     */
    public NEmuSContext getEmulator() {
        return emulator;
    }

    /**
     * Return the instance of the GUI
     *
     * @return the current instance of the GUI
     */
    public static NEmuSUnified getInstance() {
        return instance;
    }

    /**
     * Return the width of the game view
     *
     * @return the width of the game view
     */
    public int getWidth() {
        return window.getContext().getWidth();
    }

    /**
     * Return the height of the game view (omitting the menu)
     *
     * @return the height of the game view without the menu
     */
    public int getHeight() {
        return (int) (window.getContext().getHeight() - menu.getHeight());
    }
}

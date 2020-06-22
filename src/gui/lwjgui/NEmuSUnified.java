package gui.lwjgui;

import core.ppu.PPU_2C02;
import gui.lwjgui.windows.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import lwjgui.LWJGUIApplication;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.BorderPane;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import utils.Dialogs;

import java.io.File;
import java.util.Stack;

import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;

public class NEmuSUnified extends LWJGUIApplication {

    private static NEmuSUnified instance;

    private String currentDirectory;
    private Stack<Pair<String, String>> recentRoms;

    private Window window;
    private NEmuSWindow emulator;

    private MenuBar menu;

    public static void main(String[] args) {
        ModernOpenGL = false;
        launch(args);
    }

    @Override
    public void start(String[] strings, Window window) {
        instance = this;
        currentDirectory = "./";
        recentRoms = new Stack<>();

        this.window = window;
        emulator = new NEmuSWindow(window.getContext().getWindowHandle());

        BorderPane root = new BorderPane();
        root.setBackgroundLegacy(null);

        //Just to initialize JavaFX
        new JFXPanel();
        Platform.setImplicitExit(false);

        menu = new MenuBar();
        root.setTop(menu);

        MenuItem load = new MenuItem("Open");
        load.setOnAction(actionEvent -> {
            Platform.runLater( () -> {
                FileChooser romLoader = new FileChooser();
                romLoader.setInitialDirectory(new File(currentDirectory));
                romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
                File file = romLoader.showOpenDialog(null);
                if (file != null) {
                    currentDirectory = file.getAbsolutePath().replace(file.getName(), "");
                    emulator.fireLoadROMEvent(file.getAbsolutePath(), file.getName());
                }
            });
        });

        MenuItem pause = new MenuItem("Pause/Resume");
        pause.setOnAction(actionEvent -> {
           emulator.pause();
        });

        MenuItem reset = new MenuItem("Reset");
        reset.setOnAction(actionEvent -> {
            emulator.fireResetEvent();
        });

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
            if (ControllersSettings.hasInstance()) {
                Platform.runLater(ControllersSettings::focusInstance);
            } else {
                Platform.runLater(() -> {
                    try {
                        new ControllersSettings().start(new Stage());
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

        window.setScene(new Scene(root, PPU_2C02.SCREEN_WIDTH * 2, (int) (PPU_2C02.SCREEN_HEIGHT * 2 + menu.getHeight())));
        glfwSetWindowAspectRatio(window.getContext().getWindowHandle(), window.getContext().getWidth(), window.getContext().getHeight());
        window.show();

        window.setRenderingCallback(emulator);
        window.getWindowCloseCallback().addCallback(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                NEmuSWindow.getInstance().cleanUp();
                System.exit(0);
            }
        });
    }

    public NEmuSWindow getEmulator() {
        return emulator;
    }

    public static NEmuSUnified getInstance() {
        return instance;
    }

    public int getWidth() {
        return window.getContext().getWidth();
    }

    public int getHeight() {
        return (int) (window.getContext().getHeight() - menu.getHeight());
    }
}

package gui.lwjgui;

import core.ppu.PPU_2C02;
import gui.lwjgui.windows.AudioSettings;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lwjgui.LWJGUIApplication;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.BorderPane;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import utils.Dialogs;

import java.io.File;

import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;

public class NEmuSUnified extends LWJGUIApplication {

    private static NEmuSUnified instance;

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
                romLoader.setInitialDirectory(new File("./"));
                romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
                File file = romLoader.showOpenDialog(null);
                if (file != null) {
                    emulator.fireLoadROMEvent(file.getAbsolutePath(), file.getName());
                }
            });
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

        Menu file = new Menu("File");
        file.getItems().add(load);
        menu.getItems().add(file);

        Menu emulation = new Menu("Emulation");
        //emulation.getItems().add(new MenuItem("Pause/Resume"));
        //emulation.getItems().add(new MenuItem("Reset"));
        menu.getItems().add(emulation);

        Menu settings = new Menu("Settings");
        settings.getItems().add(audio);
        //settings.getItems().add(new MenuItem("Graphics"));
        //settings.getItems().add(new MenuItem("Controls"));
        menu.getItems().add(settings);

        Menu debug = new Menu("Debug");
        //debug.getItems().add(new MenuItem("CPU"));
        //debug.getItems().add(new MenuItem("PPU"));
        //debug.getItems().add(new MenuItem("APU"));
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

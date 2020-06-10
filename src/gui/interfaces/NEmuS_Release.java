package gui.interfaces;

import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import javafx.application.Platform;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;
import utils.Dialogs;

import java.io.EOFException;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * This class is the entry point of the Emulator
 */
public class NEmuS_Release extends NEmuS_Runnable {

    private AudioContext ac;

    /**
     * Create a new Instance of the emulator in release mode
     */
    public NEmuS_Release() {
        super();
        JavaSoundAudioIO jsaIO = new JavaSoundAudioIO();
        jsaIO.selectMixer(3);
        ac = new AudioContext(jsaIO);

        //Initialize the Game Window
        initGameWindow();
        instance = this;
    }

    /**
     * Clean up the memory, kill the windows and stop the audio context
     */
    @Override
    public void cleanUp() {
        pipeline.cleanUp();
        default_shader.cleanUp();
        screen_quad.cleanUp();
        screen_texture.cleanUp();

        //Destroy the Game Window
        glfwFreeCallbacks(game_window);
        glfwDestroyWindow(game_window);

        ac.stop();

        //Terminate the application
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    /**
     * Create an instance of a NES and load the game
     */
    @Override
    protected void initEmulator(String rom) throws UnsupportedMapperException, EOFException, InvalidFileException {
        super.initEmulator(rom);

        nes.setSampleFreq((int) ac.getSampleRate());
    }

    /**
     * Create and initialize the Game Window
     */
    @Override
    protected void initGameWindow() {
        super.initGameWindow();

        //Create the audio context responsible of everything time and sound related
        Function function = new Function(new WaveShaper(ac)) {
            public float calculate() {
                synchronized (nes) {
                    if (emulationRunning)
                        while (!nes.clock()) ;
                    return emulationRunning ? (float) nes.dAudioSample : 0;
                }
            }
        };
        ac.out.addInput(function);
    }

    /**
     * Run the emulator
     * handle input and redraw the screen
     */
    @Override
    public void loopGameWindow() {
        ac.start();
        long last_frame = 0, next_frame = 0;
        while (!glfwWindowShouldClose(game_window)) {
            //If a ROM Loading has been requested
            if (loadROMRequested) {
                loadROMRequested = false;
                synchronized (nes) {
                    emulationRunning = false;
                    try {
                        initEmulator(requestedRom);
                        emulationRunning = true;
                    } catch (EOFException | InvalidFileException | UnsupportedMapperException e) {
                        Platform.runLater(() -> Dialogs.showException("ROM Loading Error", "An error occur during ROM Loading", e));
                    }
                }
            }

            //If a Reset has been requested
            if (resetRequested) {
                resetRequested = false;
                synchronized (nes) {
                    emulationRunning = false;
                    nes.reset();
                    emulationRunning = true;
                }
            }

            //If we need to render the screen
            if ((emulationRunning && nes.getPpu().frameComplete) || redraw) {
                nes.getPpu().frameComplete = false;
                glClearColor(.6f, .6f, .6f, 0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                InputHandling();
                screen_texture.load(nes.getPpu().getScreenBuffer());
                renderGameScreen();
                glfwSetWindowTitle(game_window, game_name + " | " + 100000 / ((System.currentTimeMillis() - last_frame) * 100 + 1) + " fps");
                last_frame = System.currentTimeMillis();
                glfwSwapBuffers(game_window);
                if (redraw)
                    redraw = false;
            }
            glfwPollEvents();
        }
        cleanUp();
        System.exit(0);
    }
}

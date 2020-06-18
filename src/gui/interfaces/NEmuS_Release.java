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
    private long clock_count = 0;

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
        glfwFreeCallbacks(game_window_id);
        glfwDestroyWindow(game_window_id);

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
                if (emulation_running)
                    while (!nes.clock())
                        clock_count++;
                return emulation_running ? (float) nes.final_audio_sample : 0;
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
        long last_frame = 0;
        int frame_duration, fps;
        while (!glfwWindowShouldClose(game_window_id)) {
            //If a ROM Loading has been requested
            if (load_rom_requested) {
                load_rom_requested = false;
                synchronized (nes) {
                    emulation_running = false;
                    try {
                        initEmulator(requested_rom);
                        emulation_running = true;
                    } catch (EOFException | InvalidFileException | UnsupportedMapperException e) {
                        Platform.runLater(() -> Dialogs.showException("ROM Loading Error", "An error occur during ROM Loading", e));
                    }
                }
            }

            //If a Reset has been requested
            if (reset_requested) {
                reset_requested = false;
                synchronized (nes) {
                    emulation_running = false;
                    nes.reset();
                    emulation_running = true;
                }
            }

            //If we need to render the screen
            if ((emulation_running && nes.getPpu().frame_complete) || redraw) {
                frame_duration = (int) (System.currentTimeMillis() - last_frame);
                fps = (int) (10000f / frame_duration / 10);
                last_frame = System.currentTimeMillis();
                frame_count++;
                nes.getPpu().frame_complete = false;
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                InputHandling();
                screen_texture.load(nes.getPpu().getScreenBuffer());
                renderGameScreen();
                if (frame_count % 10 == 0) {
                    glfwSetWindowTitle(game_window_id, game_name + " | " + frame_duration + " ms (" + fps + "fps)");
                }
                glfwSwapBuffers(game_window_id);
                if (redraw)
                    redraw = false;
            }
            glfwPollEvents();
        }
        cleanUp();
        System.exit(0);
    }
}

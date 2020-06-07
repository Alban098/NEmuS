package gui;

import core.NES;
import core.cartridge.Cartridge;
import core.ppu.PPU_2C02;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import javafx.application.Platform;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;
import openGL.*;
import openGL.postProcessing.PostProcessingPipeline;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import utils.Dialogs;

import java.io.EOFException;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class is the entry point of the Emulator
 */
public class NEmuS_Release {

    private static NEmuS_Release instance;

    private final NES nes;

    private InputMapper inputMapper;
    private AudioContext ac;

    private int game_width = PPU_2C02.SCREEN_WIDTH * 2;
    private int game_height = PPU_2C02.SCREEN_HEIGHT * 2;
    private long game_window;
    private Texture screen_texture;
    private Quad screen_quad;
    private Fbo fbo;
    private ShaderProgram default_shader;
    private PostProcessingPipeline pipeline;

    private String requestedRom;
    private boolean loadROMRequested = false;
    private boolean resetRequested = false;
    private boolean emulationRunning = false;

    public synchronized static NEmuS_Release getInstance() {
        return instance;
    }

    public NEmuS_Release() {
        JavaSoundAudioIO jsaIO = new JavaSoundAudioIO();
        jsaIO.selectMixer(3);
        ac = new AudioContext(jsaIO);

        nes = new NES();

        //Initialize the Game Window
        initGameWindow();

        instance = this;
    }

    public void cleanUp() {
        screen_texture.delete();
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
    private void initEmulator(String rom) throws UnsupportedMapperException, EOFException, InvalidFileException {
        Cartridge cart = new Cartridge(rom);
        //Load the game into the NES
        nes.insertCartridge(cart);
        //Reset the CPU to its default state
        nes.startup();
        nes.setSampleFreq((int) ac.getSampleRate());
    }

    /**
     * Create and initialize the Game Window
     */
    private void initGameWindow() {
        //Initialize GLFW on the current Thread
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        game_window = createContextSepraratedWindow(game_width, game_height);
        glfwSetWindowAspectRatio(game_window, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        inputMapper = new InputMapper(game_window);

        //Show the window
        glfwMakeContextCurrent(game_window);
        glfwSwapInterval(0);
        glfwShowWindow(game_window);

        //Enable OpenGL on the window
        GL.createCapabilities();
        glfwSetWindowSizeCallback(game_window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windows, int w, int h) {
                game_width = w;
                game_height = h;
            }
        });

        //Activate textures and create the Screen Texture target
        glEnable(GL_TEXTURE_2D);
        screen_texture = new Texture(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT, nes.getPpu().getScreenBuffer());
        fbo = new Fbo(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        screen_quad = new Quad(new float[]{-1, -1, 1, -1, 1, 1, -1, 1});

        try {
            pipeline = new PostProcessingPipeline(screen_quad);
            default_shader = new ShaderProgram("shaders/vertex.glsl", "shaders/filters/no_filter.glsl");
        } catch (Exception e) {
            Platform.runLater(() -> Dialogs.showException("Shader Error", "An error occur during Shader Compilation", e));
            cleanUp();
            System.exit(-1);
        }

        //Create the audio context responsible of everything time and sound related
        Function function = new Function(new WaveShaper(ac)) {
            public float calculate() {
                synchronized (nes) {
                    if (emulationRunning)
                        while (!nes.clock());
                    return emulationRunning ? (float) nes.dAudioSample : 0;
                }
            }
        };

        ac.out.addInput(function);
    }

    /**
     * Create a new GLFW Window with its own context
     * Used to create windows on multiple Threads
     *
     * @param width  the window width
     * @param height the window height
     * @return the id of the windows as returned by GLFW
     */
    private long createContextSepraratedWindow(int width, int height) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW Init failed");

        //Set the window's properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Create the window
        long window = glfwCreateWindow(width, height, "", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window");
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null)
                glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }
        return window;
    }

    /**
     * Run the emulator
     * this is essentially where the emulation occur
     */
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
            if (emulationRunning && System.currentTimeMillis() > next_frame) {
                glClearColor(.6f, .6f, .6f, 0f);
                GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                InputHandling();
                screen_texture.load(nes.getPpu().getScreenBuffer());
                renderGameScreen();
                next_frame = System.currentTimeMillis() + 13;
                glfwSetWindowTitle(game_window, 1000 / ((System.currentTimeMillis() - last_frame) + 1) + " fps");
                last_frame = System.currentTimeMillis();
                glfwSwapBuffers(game_window);
            }
            glfwPollEvents();
        }
        cleanUp();
        System.exit(0);
    }

    /**
     * Get the current user inputs (Keyboard and Gamepad 1 and 2) and write it to NES
     */
    private void InputHandling() {
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_UP, 1)) nes.controller[0] |= 0x08;
        else nes.controller[0] &= ~0x08;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_DOWN, 1)) nes.controller[0] |= 0x04;
        else nes.controller[0] &= ~0x04;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_LEFT, 1)) nes.controller[0] |= 0x02;
        else nes.controller[0] &= ~0x02;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_RIGHT, 1)) nes.controller[0] |= 0x01;
        else nes.controller[0] &= ~0x01;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_A, 1)) nes.controller[0] |= 0x80;
        else nes.controller[0] &= ~0x80;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_B, 1)) nes.controller[0] |= 0x40;
        else nes.controller[0] &= ~0x40;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_SELECT, 1)) nes.controller[0] |= 0x20;
        else nes.controller[0] &= ~0x20;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_1_START, 1)) nes.controller[0] |= 0x10;
        else nes.controller[0] &= ~0x10;


        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_UP, 2)) nes.controller[1] |= 0x08;
        else nes.controller[1] &= ~0x08;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_DOWN, 2)) nes.controller[1] |= 0x04;
        else nes.controller[1] &= ~0x04;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_LEFT, 2)) nes.controller[1] |= 0x02;
        else nes.controller[1] &= ~0x02;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_RIGHT, 2)) nes.controller[1] |= 0x01;
        else nes.controller[1] &= ~0x01;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_A, 2)) nes.controller[1] |= 0x80;
        else nes.controller[1] &= ~0x80;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_B, 2)) nes.controller[1] |= 0x40;
        else nes.controller[1] &= ~0x40;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_SELECT, 2)) nes.controller[1] |= 0x20;
        else nes.controller[1] &= ~0x20;
        if (inputMapper.isPressed(GamepadInputs.CONTROLLER_2_START, 2)) nes.controller[1] |= 0x10;
        else nes.controller[1] &= ~0x10;
    }

    /**
     * Render the Game Window
     * the Quad is centered and scale to fit the window without stretching
     */
    private void renderGameScreen() {
        fbo.bindFrameBuffer();
        default_shader.bind();
        screen_texture.bind();
        screen_quad.render(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        screen_texture.unbind();
        default_shader.unbind();
        fbo.unbindFrameBuffer();
        pipeline.applyFilters(fbo.getTexture());
    }

    public synchronized void fireLoadROMEvent(String filename) {
        requestedRom = filename;
        loadROMRequested = true;
    }

    public synchronized void fireResetEvent() {
        resetRequested = true;
    }

    public int getWidth() {
        return game_width;
    }

    public int getHeight() {
        return game_height;
    }

    public PostProcessingPipeline getPipeline() {
        return pipeline;
    }

    public synchronized void pause() {
        emulationRunning = !emulationRunning;
    }
}

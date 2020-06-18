package gui.interfaces;

import core.NES;
import core.cartridge.Cartridge;
import core.ppu.PPU_2C02;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import gui.inputs.NESInputs;
import gui.inputs.InputMapper;
import javafx.application.Platform;
import openGL.Fbo;
import openGL.Quad;
import openGL.ShaderProgram;
import openGL.Texture;
import openGL.postProcessing.PostProcessingPipeline;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import utils.Dialogs;

import java.io.EOFException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class is an abstraction of the entry point of the Emulator
 */
public abstract class NEmuS_Runnable {

    protected static NEmuS_Runnable instance;
    protected final NES nes;

    private InputMapper inputMapper;
    private Fbo fbo;
    private int game_width = PPU_2C02.SCREEN_WIDTH * 2;
    private int game_height = PPU_2C02.SCREEN_HEIGHT * 2;

    Texture screen_texture;
    Quad screen_quad;
    ShaderProgram default_shader;
    PostProcessingPipeline pipeline;
    String game_name;
    String requested_rom;
    boolean load_rom_requested = false;
    boolean reset_requested = false;
    boolean emulation_running = false;
    boolean redraw = false;
    long game_window_id;
    long frame_count = 0;

    /**
     * Create a new Instance of the emulator
     */
    public NEmuS_Runnable() {
        nes = new NES();
    }

    /**
     * Get the current Instance a the emulator
     *
     * @return the current instance of the emulator
     */
    public synchronized static NEmuS_Runnable getInstance() {
        return instance;
    }

    /**
     * Clean up the memory, kill the windows and stop the audio context
     */
    public abstract void cleanUp();

    /**
     * Run the emulator
     */
    public abstract void loopGameWindow();

    /**
     * Create an instance of a NES and load the game
     */
    protected void initEmulator(String rom) throws UnsupportedMapperException, EOFException, InvalidFileException {
        Cartridge cart = new Cartridge(rom);
        //Load the game into the NES
        nes.insertCartridge(cart);
        //Reset the CPU to its default state
        nes.startup();
    }

    /**
     * Create and initialize the Game Window
     */
    protected void initGameWindow() {
        //Initialize GLFW on the current Thread
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        game_window_id = createContextSeparateWindow(game_width, game_height);
        glfwSetWindowAspectRatio(game_window_id, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        inputMapper = new InputMapper(game_window_id);

        //Show the window
        glfwMakeContextCurrent(game_window_id);
        glfwSwapInterval(0);
        glfwShowWindow(game_window_id);

        //Enable OpenGL on the window
        GL.createCapabilities();
        glfwSetWindowSizeCallback(game_window_id, new GLFWWindowSizeCallback() {
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
        screen_quad = new Quad();

        try {
            pipeline = new PostProcessingPipeline(screen_quad);
            default_shader = new ShaderProgram("shaders/vertex.glsl", "shaders/filters/no_filter.glsl");
        } catch (Exception e) {
            Platform.runLater(() -> Dialogs.showException("Shader Error", "An error occur during Shader Compilation", e));
            cleanUp();
            System.exit(-1);
        }
    }

    /**
     * Create a new GLFW Window with its own context
     * Used to create windows on multiple Threads
     *
     * @param width  the window hwidth
     * @param height the window height
     * @return the id of the windows as returned by GLFW
     */
    private long createContextSeparateWindow(int width, int height) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW Init failed");

        //Set the window's properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Create the window
        long window = glfwCreateWindow(width, height, "Game", NULL, NULL);
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
     * Get the current user inputs (Keyboard and Gamepad 1 and 2) and write it to NES
     */
    void InputHandling() {
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_UP, 1)) nes.controller[0] |= 0x08;
        else nes.controller[0] &= ~0x08;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_DOWN, 1)) nes.controller[0] |= 0x04;
        else nes.controller[0] &= ~0x04;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_LEFT, 1)) nes.controller[0] |= 0x02;
        else nes.controller[0] &= ~0x02;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_RIGHT, 1)) nes.controller[0] |= 0x01;
        else nes.controller[0] &= ~0x01;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_A, 1)) nes.controller[0] |= 0x80;
        else nes.controller[0] &= ~0x80;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_B, 1)) nes.controller[0] |= 0x40;
        else nes.controller[0] &= ~0x40;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_SELECT, 1)) nes.controller[0] |= 0x20;
        else nes.controller[0] &= ~0x20;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_1_START, 1)) nes.controller[0] |= 0x10;
        else nes.controller[0] &= ~0x10;


        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_UP, 2)) nes.controller[1] |= 0x08;
        else nes.controller[1] &= ~0x08;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_DOWN, 2)) nes.controller[1] |= 0x04;
        else nes.controller[1] &= ~0x04;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_LEFT, 2)) nes.controller[1] |= 0x02;
        else nes.controller[1] &= ~0x02;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_RIGHT, 2)) nes.controller[1] |= 0x01;
        else nes.controller[1] &= ~0x01;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_A, 2)) nes.controller[1] |= 0x80;
        else nes.controller[1] &= ~0x80;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_B, 2)) nes.controller[1] |= 0x40;
        else nes.controller[1] &= ~0x40;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_SELECT, 2)) nes.controller[1] |= 0x20;
        else nes.controller[1] &= ~0x20;
        if (inputMapper.isPressed(NESInputs.CONTROLLER_2_START, 2)) nes.controller[1] |= 0x10;
        else nes.controller[1] &= ~0x10;
    }

    /**
     * Render the Game Window
     * the Quad is centered and scale to fit the window without stretching
     */
    void renderGameScreen() {
        fbo.bindFrameBuffer();
        default_shader.bind();
        screen_texture.bind();
        screen_quad.render(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        screen_texture.unbind();
        default_shader.unbind();
        fbo.unbindFrameBuffer();
        pipeline.applyFilters(fbo.getTexture());
    }

    /**
     * Notify the emulator that it needs to load a ROM on the next game loop
     *
     * @param filename the file to load
     * @param name     the name of the game
     */
    public synchronized void fireLoadROMEvent(String filename, String name) {
        requested_rom = filename;
        game_name = name;
        load_rom_requested = true;
    }

    /**
     * Notify the emulator that it needs to reset on the next game loop
     */
    public synchronized void fireResetEvent() {
        if (nes.getCartridge() != null)
            reset_requested = true;
    }

    /**
     * Return the width of the game window
     *
     * @return the width of the game window
     */
    public int getGameWidth() {
        return game_width;
    }

    /**
     * Return the height of the game window
     *
     * @return the height of the game window
     */
    public int getGameHeight() {
        return game_height;
    }

    /**
     * Return the current post processing pipeline
     *
     * @return the post processing pipeline
     */
    public PostProcessingPipeline getPipeline() {
        return pipeline;
    }

    /**
     * Pause the emulation if it has started
     *
     * @return was the pause event successful
     */
    public synchronized boolean pause() {
        if (nes.getCartridge() != null) {
            emulation_running = !emulation_running;
            return true;
        }
        return false;
    }

    /**
     * Advance the emulation by one frame
     */
    public synchronized void frameStepEvent() {
        if (!emulation_running) {
            do {
                nes.debugClock();
            } while (!nes.getPpu().frame_complete);
            do {
                nes.debugClock();
            } while (nes.getCpu().complete());
            nes.getPpu().frame_complete = false;
            redraw = true;
        }
    }

    /**
     * Set the current RAM Page to be the 16th previous one
     * (loop when overflow)
     */
    public synchronized void ramPageLeftPlusEvent() {
    }

    /**
     * Set the current RAM Page to be the 16th next one
     * (loop when overflow)
     */
    public synchronized void ramPageRightPlusEvent() {
    }

    /**
     * Set the current RAM Page to be the previous one
     * (loop when overflow)
     */
    public synchronized void ramPageLeftEvent() {
    }

    /**
     * Set the current RAM Page to be the next one
     * (loop when overflow)
     */
    public synchronized void ramPageRightEvent() {
    }

    /**
     * Advance by one CPU Instruction
     */
    public synchronized void cpuStepEvent() {
    }

    /**
     * Swap the currently selected palette
     * (loop when overflow)
     */
    public synchronized void paletteSwapEvent() {
    }

    /**
     * Return whether or not the emulation is currently running
     *
     * @return is the emulation running
     */
    public synchronized boolean isEmulationRunning() {
        return emulation_running;
    }

    /**
     * Enable or Disable sampling in the APU
     *
     * @param enabled should sampling be activated
     */
    public synchronized void fireAudioRenderingEvent(boolean enabled) {
        nes.enableSoundRendering(enabled);
    }

    /**
     * Enable or Disable RAW Audio mode in the APU
     *
     * @param raw should RAW Audio mode be activated
     */
    public synchronized void fireRawAudioEvent(boolean raw) {
        nes.toggleRawAudio(raw);
    }

    /**
     * Return the current InputMapper
     *
     * @return the current InputMapper
     */
    public InputMapper getInputMapper() {
        return inputMapper;
    }

    /**
     * Enable or Disable the first pulse channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void pulse1Event(boolean enabled) {
        nes.getApu().setPulse1Rendered(enabled);
    }

    /**
     * Enable or Disable the second pulse channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void pulse2Event(boolean enabled) {
        nes.getApu().setPulse2Rendered(enabled);
    }

    /**
     * Enable or Disable the triangle channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void triangleEvent(boolean enabled) {
        nes.getApu().setTriangleRendered(enabled);
    }

    /**
     * Enable or Disable the noise channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void noiseEvent(boolean enabled) {
        nes.getApu().setNoiseRendered(enabled);
    }

    /**
     * Enable or Disable the DMC channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void dmcEvent(boolean enabled) {
        nes.getApu().setDMCRendered(enabled);
    }
}

package gui.interfaces;

import core.NES;
import core.cartridge.Cartridge;
import core.ppu.PPU_2C02;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import gui.inputs.GamepadInputs;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class NEmuS_Runnable {

    protected static NEmuS_Runnable instance;

    protected final NES nes;

    protected InputMapper inputMapper;

    protected int game_width = PPU_2C02.SCREEN_WIDTH * 2;
    protected int game_height = PPU_2C02.SCREEN_HEIGHT * 2;
    protected long game_window;
    protected Texture screen_texture;
    protected Quad screen_quad;
    protected Fbo fbo;
    protected ShaderProgram default_shader;
    protected PostProcessingPipeline pipeline;

    protected String requestedRom;
    protected boolean loadROMRequested = false;
    protected boolean resetRequested = false;
    protected boolean emulationRunning = false;
    protected boolean redraw = false;


    public synchronized static NEmuS_Runnable getInstance() {
        return instance;
    }

    public NEmuS_Runnable() {
        nes = new NES();
    }

    public abstract void cleanUp();

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
        game_window = createContextSepraratedWindow(game_width, game_height, "Game", true);
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
    }

    /**
     * Create a new GLFW Window with its own context
     * Used to create windows on multiple Threads
     *
     * @param width  the window hwidth
     * @param height the window height
     * @param title  the window title
     * @return the id of the windows as returned by GLFW
     */
    protected long createContextSepraratedWindow(int width, int height, String title, boolean resizeable) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW Init failed");

        //Set the window's properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE);

        //Create the window
        long window = glfwCreateWindow(width, height, title, NULL, NULL);
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
    protected void InputHandling() {
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
    protected void renderGameScreen() {
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
        if (nes.getCartridge() != null)
            resetRequested = true;
    }

    public int getGameWidth() {
        return game_width;
    }

    public int getGameHeight() {
        return game_height;
    }

    public PostProcessingPipeline getPipeline() {
        return pipeline;
    }

    public synchronized boolean pause() {
        if (nes.getCartridge() != null) {
            emulationRunning = !emulationRunning;
            return true;
        }
        return false;
    }

    public synchronized void frameStepEvent() {
        if (!emulationRunning) {
            do {
                nes.debugClock();
            } while (!nes.getPpu().frameComplete);
            do {
                nes.debugClock();
            } while (nes.getCpu().complete());
            nes.getPpu().frameComplete = false;
            redraw = true;
        }
    }

    public synchronized void ramPageLeftPlusEvent() {}

    public synchronized void ramPageRightPlusEvent() {}

    public synchronized void ramPageLeftEvent() {}

    public synchronized void ramPageRightEvent() {}

    public synchronized void cpuStepEvent() {}

    public synchronized void paletteSwapEvent() {}

    public boolean isEmulationRunning() {
        return emulationRunning;
    }
}

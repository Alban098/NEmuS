package gui;

import core.NES;
import core.cartridge.Cartridge;
import core.ppu.PPU_2C02;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;
import openGL.Texture;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import utils.NumberUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
public class NEmuS_Sound {

    private static final int FRAME_DURATION = 1000 / 50;

    private static String game_name;

    private long game_window;
    private GLFWKeyCallback gameKeyCallBack;
    private InputMapper inputMapper;

    private int game_width = PPU_2C02.SCREEN_WIDTH * 2;
    private int game_height = PPU_2C02.SCREEN_HEIGHT * 2;
    private float game_aspect = (float) game_width / game_height;

    private final NES nes;
    private AudioContext ac;

    private Texture screen_texture;

    private boolean emulationRunning = true;

    public static void main(String[] args) {
        new NEmuS_Sound();
    }

    public NEmuS_Sound() {
        JavaSoundAudioIO jsaIO = new JavaSoundAudioIO();
        jsaIO.selectMixer(3);
        ac = new AudioContext(jsaIO);

        nes = new NES();

        //Load a Game ROM
        JFileChooser romSelector = new JFileChooser("./");
        romSelector.setFileFilter(new FileNameExtensionFilter("iNES file (.nes)", "nes"));
        if (romSelector.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filename = romSelector.getSelectedFile().getAbsolutePath();
            game_name = romSelector.getSelectedFile().getName();
            try {
                initEmulator(filename);
            } catch (EOFException | InvalidFileException | UnsupportedMapperException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "ROM Loading Error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        } else {
            System.exit(-1);
        }


        //Create KeyHandler
        gameKeyCallBack = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (window == game_window) {
                    if (key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                        emulationRunning = false;
                        JFileChooser romSelector = new JFileChooser("./");
                        romSelector.setFileFilter(new FileNameExtensionFilter("iNES file (.nes)", "nes"));
                        if (romSelector.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            String filename = romSelector.getSelectedFile().getAbsolutePath();
                            game_name = romSelector.getSelectedFile().getName();
                            try {
                                initEmulator(filename);
                            } catch (EOFException | InvalidFileException | UnsupportedMapperException e) {
                                JOptionPane.showMessageDialog(null, e.getMessage() + "\nGame not loaded", "ROM Loading Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        emulationRunning = true;
                    } else if (key == GLFW_KEY_F2 && action == GLFW_PRESS) {
                        emulationRunning = false;
                        nes.reset();
                        emulationRunning = true;
                    } else if (key == GLFW_KEY_F3 && action == GLFW_PRESS) {
                        emulationRunning = false;
                        do {
                            nes.clock();
                        } while (!nes.getPpu().frameComplete);
                        //We ensure the PPU is at the top left corner
                        nes.clock();
                        nes.getPpu().frameComplete = false;
                        nes.getCartridge().save();
                        emulationRunning = true;
                    } else if (key == GLFW_KEY_F12 && action == GLFW_PRESS) {
                        JOptionPane.showMessageDialog(null, "F1 : Load ROM\nF2 : Reset NES\nF3 : Force Savegame", "Keyboard Shortcut Help", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        };

        //Initialize the Game Window
        initGameWindow();

        //Start the NES
        loopGameWindow();

        //Destroy the Game Window
        glfwFreeCallbacks(game_window);
        glfwDestroyWindow(game_window);

        ac.stop();


        //Terminate the application
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
    // ========================================================= //

    public static void main() {
        new NEmuS_Sound();
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
        game_window = createContextSepraratedWindow(game_width, game_height, "Game Window");
        inputMapper = new InputMapper(game_window);
        //Set the window's resize event
        glfwSetWindowSizeCallback(game_window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windows, int w, int h) {
                game_aspect = (float) w / h;
                game_width = w;
                game_height = h;
            }
        });
        //Show the window
        glfwMakeContextCurrent(game_window);
        glfwSwapInterval(0);
        glfwShowWindow(game_window);

        //Enable OpenGL on the window
        GL.createCapabilities();

        //Activate textures and create the Screen Texture target
        glEnable(GL_TEXTURE_2D);
        screen_texture = new Texture(256, 240, nes.getPpu().getScreenBuffer());

        //Set the KeyCallBack
        glfwSetKeyCallback(game_window, gameKeyCallBack);

        //Create the audio context responsible of everithing time and sound related
        Function function = new Function(new WaveShaper(ac)) {
            public float calculate() {
                synchronized (nes) {
                    if (emulationRunning)
                        while (!nes.clock()) {
                        }
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
     * @param width  the window hwidth
     * @param height the window height
     * @param title  the window title
     * @return the id of the windows as returned by GLFW
     */
    private long createContextSepraratedWindow(int width, int height, String title) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW Init failed");

        //Set the window's properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

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
     * Run the emulator
     * this is essentially where the emulation occur
     */
    private void loopGameWindow() {
        ac.start();
        long last_frame = 0;
        while (!glfwWindowShouldClose(game_window)) {
            //Set the current OpenGL context
            glfwMakeContextCurrent(game_window);
            glClearColor(.6f, .6f, .6f, 0f);
            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //We load the screen pixels into VRAM and display them
            //We update the controller registers
            if (emulationRunning) {
                InputHandling();
                screen_texture.load(nes.getPpu().getScreenBuffer());
                renderGameScreen();
            }
            glfwSetWindowTitle(game_window, game_name + " / " + 1000000000 / ((System.nanoTime() - last_frame) + 1) + " fps");
            last_frame = System.nanoTime();
            glfwSwapBuffers(game_window);
            //Get input events
            glfwPollEvents();
        }

        screen_texture.delete();
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
        glViewport(0, 0, game_width, game_height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-game_aspect, game_aspect, -1, 1, -1, 1);
        screen_texture.bind();

        float view_aspect = (float) PPU_2C02.SCREEN_WIDTH / PPU_2C02.SCREEN_HEIGHT, quad_end_x = game_aspect, quad_end_y = -1;
        if (game_height / view_aspect > game_width)
            quad_end_y = NumberUtils.map(game_width * view_aspect, 0, game_height, 1, -1);
        else
            quad_end_x = NumberUtils.map(game_height * view_aspect, 0, game_width, -game_aspect, game_aspect);
        float quad_width = (2 * game_aspect - (quad_end_x + game_aspect)) / 2;
        float quad_height = (2 - (1 - quad_end_y)) / 2;
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-game_aspect + quad_width, 1 - quad_height);
        glTexCoord2f(1, 0);
        glVertex2f(quad_end_x + quad_width, 1 - quad_height);
        glTexCoord2f(1, 1);
        glVertex2f(quad_end_x + quad_width, quad_end_y - quad_height);
        glTexCoord2f(0, 1);
        glVertex2f(-game_aspect + quad_width, quad_end_y - quad_height);
        glEnd();
    }
}

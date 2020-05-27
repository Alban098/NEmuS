import cartridge.Cartridge;
import cpu.Bus;
import graphics.PPU_2C02;
import javafx.geometry.Point2D;
import openGL.Texture;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import utils.NumberUtils;

import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class NEmuS {

    private static final int FRAME_DURATION = 1000000000/60;
    private static final boolean DEBUG = true;

    private int width = 1920;
    private int height = 1010;
    private float aspect = (float) width / height;

    private long window;

    private boolean emulationRunning = true;
    private long frameCount = 0;
    private long next_frame = 0;
    private long last_frame = 0;
    private int selectedPalette = 0x00;

    private Bus nes;
    private Map<Integer, String> decompiled;

    private Texture screen_texture;
    private Texture patternTable1_texture;
    private Texture patternTable2_texture;

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {

        //=============================================== Emulator Creation ===============================================
        nes = new Bus();
        Cartridge cart = new Cartridge("smb.nes");
        nes.insertCartridge(cart);
        decompiled = nes.getCpu().disassemble(0x0000, 0xFFFF);
        nes.getCpu().reset();

        //=============================================== OpenGL Init ===============================================
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW Init failed");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "NES", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0))/2, (vidmode.height() - pHeight.get(0))/2);
        }

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windows, int w, int h) {
                aspect = (float)w/h;
                width = w;
                height = h;
            }
        });
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_TEXTURE_2D);
        screen_texture = new Texture(nes.getPpu().getScreen());
        patternTable1_texture = new Texture(nes.getPpu().getPatternTable(0, selectedPalette));
        patternTable2_texture = new Texture(nes.getPpu().getPatternTable(1, selectedPalette));

        if (DEBUG) {
            glfwSetKeyCallback(window, new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if (key == GLFW_KEY_SPACE && action == GLFW_PRESS)
                        emulationRunning = !emulationRunning;
                    else if (key == GLFW_KEY_P && action == GLFW_PRESS)
                        selectedPalette = (selectedPalette + 1) & 0x07;
                    else if (key == GLFW_KEY_R && action == GLFW_PRESS)
                        nes.reset();
                    else if (key == GLFW_KEY_C && action == GLFW_PRESS) {
                        do { nes.clock(); } while (!nes.getCpu().complete());
                        do { nes.clock(); } while (nes.getCpu().complete());
                    } else  if (key == GLFW_KEY_V && action == GLFW_PRESS) {
                        for (int i = 0; i < 10; i++) {
                            do { nes.clock(); } while (!nes.getCpu().complete());
                            do { nes.clock(); } while (nes.getCpu().complete());
                        }
                    } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
                        for (int i = 0; i < 50; i++) {
                            do { nes.clock(); } while (!nes.getCpu().complete());
                            do { nes.clock(); } while (nes.getCpu().complete());
                        }
                    } else if (key == GLFW_KEY_F && action == GLFW_PRESS) {
                        do { nes.clock(); } while (!nes.getCpu().complete());
                        do { nes.clock(); } while (nes.getCpu().complete());
                        nes.getPpu().frameComplete = false;
                    }
                }
            });
        }
    }

    private void loop() {
        glClearColor(.6f, .6f, .6f, 0f);

        while (!glfwWindowShouldClose(window)) {
            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            emulationStep();
            handleInput();
            renderScreen();
            renderPatternTables();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        screen_texture.delete();
        patternTable2_texture.delete();
        patternTable1_texture.delete();
    }

    private void emulationStep() {
        if (emulationRunning) {
            if (System.nanoTime() >= next_frame) {
                next_frame = System.nanoTime() + FRAME_DURATION;
                do {
                    nes.clock();
                } while (!nes.getPpu().frameComplete);
                nes.getPpu().frameComplete = false;
                nes.getPpu().getPatternTable(0, selectedPalette);
                nes.getPpu().getPatternTable(1, selectedPalette);
                frameCount++;
                screen_texture.update();
                patternTable1_texture.update();
                patternTable2_texture.update();
                glfwSetWindowTitle(window, 1000000000 / (System.nanoTime() - last_frame) + " fps");
                last_frame = System.nanoTime();
            }
        }
    }

    private void handleInput() {
        // Controller 1
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) nes.controller[0] |= 0x08; else nes.controller[0] &= ~0x08;      // Up
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) nes.controller[0] |= 0x04; else nes.controller[0] &= ~0x04;    // Down
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) nes.controller[0] |= 0x02; else nes.controller[0] &= ~0x02;    // Left
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) nes.controller[0] |= 0x01; else nes.controller[0] &= ~0x01;   // Right
        if (glfwGetKey(window, GLFW_KEY_I) == GLFW_PRESS) nes.controller[0] |= 0x80; else nes.controller[0] &= ~0x80;       // A
        if (glfwGetKey(window, GLFW_KEY_O) == GLFW_PRESS) nes.controller[0] |= 0x40; else nes.controller[0] &= ~0x40;       // B
        if (glfwGetKey(window, GLFW_KEY_K) == GLFW_PRESS) nes.controller[0] |= 0x20; else nes.controller[0] &= ~0x20;       // Select
        if (glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS) nes.controller[0] |= 0x10; else nes.controller[0] &= ~0x10;       // Start

        // Controller 2
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) nes.controller[1] |= 0x08; else nes.controller[1] &= ~0x08;       // Up
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) nes.controller[1] |= 0x04; else nes.controller[1] &= ~0x04;       // Down
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) nes.controller[1] |= 0x02; else nes.controller[1] &= ~0x02;       // Left
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) nes.controller[1] |= 0x01; else nes.controller[1] &= ~0x01;       // Right
        if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) nes.controller[1] |= 0x80; else nes.controller[1] &= ~0x80;       // A
        if (glfwGetKey(window, GLFW_KEY_Y) == GLFW_PRESS) nes.controller[1] |= 0x40; else nes.controller[1] &= ~0x40;       // B
        if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) nes.controller[1] |= 0x20; else nes.controller[1] &= ~0x20;       // Select
        if (glfwGetKey(window, GLFW_KEY_H) == GLFW_PRESS) nes.controller[1] |= 0x10; else nes.controller[1] &= ~0x10;       // Start
    }

    private void drawQuadHeight(int x, int y, int height,float quadAspect, Texture texture) {
        texture.bind();
        glBegin(GL_QUADS);

        glTexCoord2f(0, 0);
        glVertex2f(toWorldSpaceX(x), toWorldSpaceY(y));

        glTexCoord2f(1, 0);
        glVertex2f(toWorldSpaceX(x + height * quadAspect), toWorldSpaceY(y));

        glTexCoord2f(1, 1);
        glVertex2f(toWorldSpaceX(x + height * quadAspect), toWorldSpaceY(y + height));

        glTexCoord2f(0, 1);
        glVertex2f(toWorldSpaceX(x), toWorldSpaceY(y + height));

        glEnd();
    }

    private void prepareRendering() {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-aspect, aspect, -1, 1, -1, 1);
    }

    private void renderScreen() {
        prepareRendering();
        drawQuadHeight(10, 10, 990, (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT, screen_texture);
    }

    private void renderPatternTables() {
        prepareRendering();
        drawQuadHeight((int) (20 + 990 * (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT), 1000-256, 256, 1, patternTable1_texture);
        drawQuadHeight((int) (20 + 990 * (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT) + 266, 1000-256, 256, 1, patternTable2_texture);
    }

    public float toWorldSpaceX(float screenSpaceX) {
        return NumberUtils.map(screenSpaceX, 0, width, aspect, -aspect);
    }

    public float toWorldSpaceY(float screenSpaceY) {
        return NumberUtils.map(screenSpaceY, 0, height, -1, 1);
    }

    public static void main(String[] args) {
        new NEmuS().run();
    }
}

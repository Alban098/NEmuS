import cartridge.Cartridge;
import cpu.Bus;
import cpu.Flags;
import graphics.PPU_2C02;
import openGL.Texture;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import utils.NumberUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class NEmuS {

    private static final int FRAME_DURATION = 1000000000/60;

    private static boolean debug = false;

    private int width = 1920;
    private int height = 1010;
    private float aspect = (float) width / height;

    private long window;

    private boolean emulationRunning = true;
    private long frameCount = 0;
    private long next_frame = 0;
    private long last_frame = 0;
    private int selectedPalette = 0x00;
    private int ram_page = 0x00;


    private Bus nes;
    private Map<Integer, String> decompiled;

    private Texture screen_texture;
    private Texture patternTable1_texture;
    private Texture patternTable2_texture;
    private Texture cpu_texture;
    private Texture oam_texture;

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
        cpu_texture = new Texture(new BufferedImage(258+258+3, 990-258-30, BufferedImage.TYPE_INT_RGB));
        oam_texture = new Texture(new BufferedImage(305, 990, BufferedImage.TYPE_INT_RGB));

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (debug) {
                    if (key == GLFW_KEY_SPACE && action == GLFW_PRESS)
                        emulationRunning = !emulationRunning;
                    else if (key == GLFW_KEY_P && action == GLFW_PRESS)
                        selectedPalette = (selectedPalette + 1) & 0x07;
                    else if (key == GLFW_KEY_R && action == GLFW_PRESS)
                        nes.reset();
                    else if (key == GLFW_KEY_3 && action == GLFW_PRESS)
                        ram_page++;
                    else if (key == GLFW_KEY_2 && action == GLFW_PRESS)
                        ram_page--;
                    else if (key == GLFW_KEY_4 && action == GLFW_PRESS)
                        ram_page += 0x10;
                    else if (key == GLFW_KEY_1 && action == GLFW_PRESS)
                        ram_page -= 0x10;
                    else if (key == GLFW_KEY_C && action == GLFW_PRESS) {
                        do { nes.clock(); } while (!nes.getCpu().complete());
                        do { nes.clock(); } while (nes.getCpu().complete());
                        if (nes.getPpu().frameComplete) {
                            frameCount++;
                            nes.getPpu().frameComplete = false;
                        }
                    } else if (key == GLFW_KEY_V && action == GLFW_PRESS) {
                        for (int i = 0; i < 10; i++) {
                            do { nes.clock(); } while (!nes.getCpu().complete());
                            do { nes.clock(); } while (nes.getCpu().complete());
                            if (nes.getPpu().frameComplete) {
                                frameCount++;
                                nes.getPpu().frameComplete = false;
                            }
                        }
                    } else if (key == GLFW_KEY_B && action == GLFW_PRESS) {
                        for (int i = 0; i < 50; i++) {
                            do { nes.clock(); } while (!nes.getCpu().complete());
                            do { nes.clock(); } while (nes.getCpu().complete());
                            if (nes.getPpu().frameComplete) {
                                frameCount++;
                                nes.getPpu().frameComplete = false;
                            }
                        }
                    } else if (key == GLFW_KEY_F && action == GLFW_PRESS) {
                        do { nes.clock(); } while (!nes.getPpu().frameComplete);
                        do { nes.clock(); } while (nes.getCpu().complete());
                        nes.getPpu().frameComplete = false;
                        frameCount++;
                    }
                }
                if (key == GLFW_KEY_ENTER && action == GLFW_PRESS)
                    debug = !debug;
            }
        });
    }

    private void loop() {
        glClearColor(.6f, .6f, .6f, 0f);

        while (!glfwWindowShouldClose(window)) {
            if (ram_page < 0x00) ram_page += 0x100;
            if (ram_page > 0xFF) ram_page -= 0x100;
            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            emulationStep();
            screen_texture.update();
            handleInput();
            renderScreen();
            renderPatternTables();
            renderCpu();
            renderOam();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        screen_texture.delete();
        patternTable2_texture.delete();
        patternTable1_texture.delete();
    }

    private void emulationStep() {
        if (System.nanoTime() >= next_frame) {
            next_frame = System.nanoTime() + FRAME_DURATION;
            if (emulationRunning) {
                do {
                    nes.clock();
                } while (!nes.getPpu().frameComplete);
                nes.getPpu().frameComplete = false;
                frameCount++;
            }
            if (debug) {
                nes.getPpu().getPatternTable(0, selectedPalette);
                nes.getPpu().getPatternTable(1, selectedPalette);
                computeCpuTexture();
                computeOAMTexture();

                patternTable1_texture.update();
                patternTable2_texture.update();
                cpu_texture.update();
                oam_texture.update();
            }
            glfwSetWindowTitle(window, (debug ? "Debug - " : "") + 1000000000 / (System.nanoTime() - last_frame) + " fps");
            last_frame = System.nanoTime();
        }
    }

    private void computeOAMTexture() {
        Graphics g = oam_texture.getImg().getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0,0, oam_texture.getImg().getWidth(), oam_texture.getImg().getHeight());
        g.setFont(new Font("monospaced", Font.BOLD, 35));
        g.setColor(Color.GREEN);
        g.drawString("OAM Memory", 30, 40);
        g.setFont(new Font("monospaced", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        synchronized (nes.getPpu()) {
            for (int i = 0; i < 64; i++) {
                String s = String.format("%02X:", i) + " (" + nes.getPpu().getOams()[i].getX() + ", " + nes.getPpu().getOams()[i].getY() + ") ID: " + String.format("%02X", nes.getPpu().getOams()[i].getId()) + " AT: " + String.format("%02X", nes.getPpu().getOams()[i].getAttribute());
                g.drawString(s, 25, (int) (70 + 14.5 * i));
            }
        }
    }

    private void computeCpuTexture() {
        Graphics g = cpu_texture.getImg().getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0,0, cpu_texture.getImg().getWidth(), cpu_texture.getImg().getHeight());
        g.setFont(new Font("monospaced", Font.BOLD, 35));
        g.setColor(Color.GREEN);
        g.drawString("CPU - RAM", 150, 40);
        g.setFont(new Font("monospaced", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("STATUS:", 10 , 70);
        if (nes.getCpu().threadSafeGetState(Flags.N)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("N", 80, 70);
        if (nes.getCpu().threadSafeGetState(Flags.V)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("V", 96, 70);
        if (nes.getCpu().threadSafeGetState(Flags.U)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("-", 112, 70);
        if (nes.getCpu().threadSafeGetState(Flags.B)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("B", 128, 70);
        if (nes.getCpu().threadSafeGetState(Flags.D)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("D", 144, 70);
        if (nes.getCpu().threadSafeGetState(Flags.I)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("I", 160, 70);
        if (nes.getCpu().threadSafeGetState(Flags.Z)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("Z", 176, 70);
        if (nes.getCpu().threadSafeGetState(Flags.C)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("C", 194, 70);
        g.setColor(Color.WHITE);
        g.drawString("Program C  : $" + String.format("%02X", nes.getCpu().threadSafeGetPc()), 10 , 125 + 60);
        g.drawString("A Register : $" + String.format("%02X", nes.getCpu().threadSafeGetA()) + "[" + nes.getCpu().threadSafeGetA() + "]", 10 , 140 + 60);
        g.drawString("X Register : $" + String.format("%02X", nes.getCpu().threadSafeGetX()) + "[" + nes.getCpu().threadSafeGetX() + "]", 10 , 155 + 60);
        g.drawString("Y Register : $" + String.format("%02X", nes.getCpu().threadSafeGetY()) + "[" + nes.getCpu().threadSafeGetY() + "]", 10 , 170 + 60);
        g.drawString("Stack Ptr  : $" + String.format("%04X", nes.getCpu().threadSafeGetStkp()), 10 , 185 + 60);
        g.drawString("Ticks  : " + nes.getCpu().threadSafeGetCpuClock(), 10, 340);
        g.drawString("Frames : " + frameCount, 10 , 355);

        // RAM =======================================
        int nRamX = 5, nRamY = 440;
        int nAddr = ram_page << 8;
        for (int row = 0; row < 16; row++)
        {
            String sOffset = String.format("$%04X:", nAddr);
            for (int col = 0; col < 16; col++)
            {
                sOffset += " " +  String.format("%02X", nes.threadSafeCpuRead(nAddr));
                nAddr += 1;
            }
            g.drawString(sOffset, nRamX, nRamY);
            nRamY += 17;
        }

        // Code ======================================
        String currentLine = decompiled.get(nes.getCpu().threadSafeGetPc());
        if (currentLine != null) {
            Queue<String> before = new LinkedList<>();
            Queue<String> after = new LinkedList<>();
            boolean currentLineFound = false;
            for (Map.Entry<Integer, String> line : decompiled.entrySet()) {
                if (!currentLineFound) {
                    if (line.getKey() == nes.getCpu().threadSafeGetPc())
                        currentLineFound = true;
                    else
                        before.offer(line.getValue());
                    if (before.size() > 22 / 2)
                        before.poll();
                } else {
                    after.offer(line.getValue());
                    if (after.size() > 22 / 2)
                        break;
                }
            }
            int lineY = 70;
            g.setColor(Color.WHITE);
            for (String line : before) {
                g.drawString(line, 230, lineY);
                lineY += 15;
            }
            g.setColor(Color.CYAN);
            g.drawString(currentLine, 230, lineY);
            lineY += 15;
            g.setColor(Color.WHITE);
            for (String line : after) {
                g.drawString(line, 230, lineY);
                lineY += 15;
            }
        }
    }

    private void handleInput() {
        // Controller 1
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) nes.controller[0] |= 0x08; else nes.controller[0] &= ~0x08;       // Up
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) nes.controller[0] |= 0x04; else nes.controller[0] &= ~0x04;       // Down
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) nes.controller[0] |= 0x02; else nes.controller[0] &= ~0x02;       // Left
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) nes.controller[0] |= 0x01; else nes.controller[0] &= ~0x01;       // Right
        if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) nes.controller[0] |= 0x80; else nes.controller[0] &= ~0x80;       // A
        if (glfwGetKey(window, GLFW_KEY_Y) == GLFW_PRESS) nes.controller[0] |= 0x40; else nes.controller[0] &= ~0x40;       // B
        if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) nes.controller[0] |= 0x20; else nes.controller[0] &= ~0x20;       // Select
        if (glfwGetKey(window, GLFW_KEY_H) == GLFW_PRESS) nes.controller[0] |= 0x10; else nes.controller[0] &= ~0x10;       // Start
    }

    private void drawQuadHeight(int x, int y, int height,float quadAspect) {
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
        screen_texture.bind();
        drawQuadHeight(10, 10, 990, (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT);
    }

    private void renderPatternTables() {
        prepareRendering();
        glBindTexture(GL_TEXTURE_2D, 0);
        int x = (int) (20 + 990 * (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT);
        glColor3f(1, 0, 0);
        drawQuadHeight(x + 66 * selectedPalette - 3, 1000 - 258 - 20 - 3, 21, 3.14286f);
        for (int p = 0; p < 8; p++) {
            for (int s = 0; s < 4; s++) {
                Color color = nes.getPpu().threadSafeGetColorFromPalette(p, s);
                glColor3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
                drawQuadHeight(x + 66 * p + 15 * s, 1000 - 258 - 20, 15, 1);
            }
        }
        glColor3f(1,1,1);
        patternTable1_texture.bind();
        drawQuadHeight(x, 1000-258, 258, 1);
        patternTable2_texture.bind();
        drawQuadHeight(x + 264, 1000-258, 258, 1);
    }

    private void renderCpu() {
        prepareRendering();
        cpu_texture.bind();
        drawQuadHeight((int) (20 + 990 * (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT), 10, cpu_texture.getHeight(), (float)cpu_texture.getWidth()/cpu_texture.getHeight());
    }

    private void renderOam() {
        prepareRendering();
        oam_texture.bind();
        drawQuadHeight((int) (20 + 990 * (float)PPU_2C02.SCREEN_WIDTH/PPU_2C02.SCREEN_HEIGHT) + 10 + cpu_texture.getWidth(), 10, oam_texture.getHeight(), (float)oam_texture.getWidth()/oam_texture.getHeight());
    }

    private float toWorldSpaceX(float screenSpaceX) {
        return NumberUtils.map(screenSpaceX, 0, width, aspect, -aspect);
    }

    private float toWorldSpaceY(float screenSpaceY) {
        return NumberUtils.map(screenSpaceY, 0, height, -1, 1);
    }

    public static void main(String[] args) {
        new NEmuS().run();
    }
}

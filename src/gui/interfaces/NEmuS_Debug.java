package gui.interfaces;

import core.cpu.Flags;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import utils.Dialogs;

import java.io.EOFException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * This class is the entry point of the Emulator's Debug mode, there is no Post Processing and no Sound
 */
public class NEmuS_Debug extends NEmuS_Runnable {

    private static final int FRAME_DURATION = 1000 / 60;

    private Stage debug_stage;

    // ==================== Debug Variables ==================== //
    private int info_width = 1385;
    private int info_height = 640;


    private int selected_palette = 0x00;
    private int ram_page = 0x00;

    private Map<Integer, String> decompiled;

    private Image nametable1_render_target;
    private Image nametable2_render_target;
    private Image nametable3_render_target;
    private Image nametable4_render_target;
    private Image patterntable1_render_target;
    private Image patterntable2_render_target;
    private Canvas debug_canvas;

    private Thread info_thread;
    // ========================================================= //

    /**
     * Launch the Emulator and the Debug Window
     */
    public NEmuS_Debug() {
        super();

        patterntable1_render_target = new WritableImage(128, 128);
        patterntable2_render_target = new WritableImage(128, 128);
        nametable1_render_target = new WritableImage(256, 240);
        nametable2_render_target = new WritableImage(256, 240);
        nametable3_render_target = new WritableImage(256, 240);
        nametable4_render_target = new WritableImage(256, 240);

        //Initialize the Game Window
        initGameWindow();

        instance = this;
    }

    /**
     * Clean up the memory, kill the windows stop the threads
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

        //If the Debug Window is active, close it
        if (info_thread != null && info_thread.isAlive())
            debug_stage.close();

        //Wait for the Debug Thread to terminate
        if (info_thread != null)
            try {
                info_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

        nes.setSampleFreq(44100);
        //Launch the Info/Debug Window on a dedicated Thread
        if (info_thread == null) {
            info_thread = new Thread(this::launchInfoWindow);
            info_thread.start();
        }
    }


    /**
     * Create and initialize the Info/Debug Window
     */
    private void initInfoWindow() {
        Platform.runLater(() -> {
            AnchorPane layout = new AnchorPane();
            debug_canvas = new Canvas(info_width, info_height);
            layout.getChildren().add(debug_canvas);
            Scene debugScene = new Scene(layout, info_width, info_height);
            debug_stage = new Stage();
            debug_stage.setScene(debugScene);
            debug_stage.setOnCloseRequest(windowEvent -> glfwSetWindowShouldClose(game_window_id, true));
            debug_stage.setTitle("Debug Window");
            debug_stage.show();
        });
    }

    /**
     * Run the emulator
     * this is essentially where the emulation occur
     */
    @Override
    public void loopGameWindow() {
        long next_frame = 0, last_frame = 0;
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
            if ((emulation_running || redraw) && System.currentTimeMillis() > next_frame) {
                glClearColor(.6f, .6f, .6f, 0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                InputHandling();
                next_frame = System.currentTimeMillis() + FRAME_DURATION;
                if (emulation_running) {
                    //We compute an entire frame in one go and wait for the next one
                    //this isn't hardware accurate, but is close enough to have most game run properly
                    long start = System.currentTimeMillis();
                    do {
                        nes.clock();
                    } while (!nes.getPpu().frame_complete);
                    nes.getPpu().frame_complete = false;
                    frame_count++;
                    if (frame_count % 10 == 0) {
                        glfwSetWindowTitle(game_window_id, game_name + " | " + (System.currentTimeMillis() - start) + " ms (" + (int)(100000f/(System.currentTimeMillis() - last_frame)/10f) + "fps)");
                        last_frame = System.currentTimeMillis();
                    }
                }
                screen_texture.load(nes.getPpu().getScreenBuffer());
                renderGameScreen();
                glfwSwapBuffers(game_window_id);
                if (!emulation_running)
                    redraw = false;
            }
            glfwPollEvents();
        }
        cleanUp();
        System.exit(0);
    }


    /**
     * Initialize and run the Debug Window
     * Usually launched on a separated Thread
     */
    private void launchInfoWindow() {
        initInfoWindow();
        long next_frame = 0;
        while (!glfwWindowShouldClose(game_window_id)) {
            //Only redraw if the emulation is running or a debug step has been made
            if (emulation_running || redraw) {
                //Set the current OpenGL context
                //Wrap the ram page to avoid out of bounds
                if (ram_page < 0x00) ram_page += 0x100;
                if (ram_page > 0xFF) ram_page -= 0x100;
                //Set when the next frame should occur
                if (System.currentTimeMillis() >= next_frame) {
                    next_frame = System.currentTimeMillis() + FRAME_DURATION * 3;
                    //Compute and update the debug textures (CPU, OAM, PatternTables and Nametables)
                    Platform.runLater(this::updateCanvas);
                }

                //If it was a debug step, clear the flag
                if (!emulation_running)
                    redraw = false;
            }
        }
        glfwSetWindowShouldClose(game_window_id, true);
    }

    /**
     * Update and redraw the Debug View
     */
    private void updateCanvas() {
        //The code is decompiled around the current program counter address
        //we need to do it at runtime because ROM bank can be switched
        //and the code at a given address is subject to change and need to be re-fetched
        int pc = nes.getCpu().getProgramCounter();
        decompiled = nes.getCpu().disassemble(pc - 50, pc + 50, " ");
        // ================================= PPU Memory Visualization =================================
        nes.getPpu().getNametable(0, (WritableImage) nametable1_render_target);
        nes.getPpu().getNametable(1, (WritableImage) nametable2_render_target);
        nes.getPpu().getNametable(2, (WritableImage) nametable3_render_target);
        nes.getPpu().getNametable(3, (WritableImage) nametable4_render_target);
        nes.getPpu().getPatternTable(0, selected_palette, (WritableImage) patterntable1_render_target);
        nes.getPpu().getPatternTable(1, selected_palette, (WritableImage) patterntable2_render_target);
        // ================================= Status =================================
        GraphicsContext g = debug_canvas.getGraphicsContext2D();
        g.setFill(Color.GREY);
        g.fillRect(0, 0, debug_canvas.getWidth(), debug_canvas.getHeight());
        g.setFont(Font.font("monospaced", FontWeight.BOLD, 35));
        g.setFill(Color.LIMEGREEN);
        g.fillText("CPU - RAM", 102, 40);
        g.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
        g.setFill(Color.WHITE);
        g.fillText("STATUS:", 10, 60);
        if (nes.getCpu().getFlag(Flags.N)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("N", 67, 60);
        if (nes.getCpu().getFlag(Flags.V)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("V", 80, 60);
        if (nes.getCpu().getFlag(Flags.U)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("-", 93, 60);
        if (nes.getCpu().getFlag(Flags.B)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("B", 106, 60);
        if (nes.getCpu().getFlag(Flags.D)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("D", 119, 60);
        if (nes.getCpu().getFlag(Flags.I)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("I", 132, 60);
        if (nes.getCpu().getFlag(Flags.Z)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("Z", 145, 60);
        if (nes.getCpu().getFlag(Flags.C)) g.setFill(Color.LIMEGREEN);
        else g.setFill(Color.RED);
        g.fillText("C", 160, 60);
        g.setFill(Color.WHITE);
        g.fillText("Program C  : $" + String.format("%02X", pc), 10, 170);
        g.fillText("A Register : $" + String.format("%02X", nes.getCpu().getAccumulator()) + "[" + nes.getCpu().getAccumulator() + "]", 10, 185);
        g.fillText("X Register : $" + String.format("%02X", nes.getCpu().getXRegister()) + "[" + nes.getCpu().getXRegister() + "]", 10, 200);
        g.fillText("Y Register : $" + String.format("%02X", nes.getCpu().getYRegister()) + "[" + nes.getCpu().getYRegister() + "]", 10, 215);
        g.fillText("Stack Ptr  : $" + String.format("%04X", nes.getCpu().getStackPointer()), 10, 230);
        g.fillText("Ticks  : " + nes.getCpu().getCpuClock(), 10, 310);
        g.fillText("Frames : " + frame_count, 10, 325);

        // ================================= RAM =================================
        int nRamX = 5, nRamY = 450;
        g.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
        int nAddr = ram_page << 8;
        for (int row = 0; row < 16; row++) {
            StringBuilder sOffset = new StringBuilder(String.format("$%04X:", nAddr));
            for (int col = 0; col < 16; col++) {
                sOffset.append(" ").append(String.format("%02X", nes.cpuRead(nAddr, true)));
                nAddr += 1;
            }
            g.fillText(sOffset.toString(), nRamX, nRamY);
            nRamY += 12;
        }

        // ================================= Code =================================
        String currentLine = decompiled.get(pc);
        if (currentLine != null) {
            Queue<String> before = new LinkedList<>();
            Queue<String> after = new LinkedList<>();
            boolean currentLineFound = false;
            for (Map.Entry<Integer, String> line : decompiled.entrySet()) {
                if (!currentLineFound) {
                    if (line.getKey() == pc)
                        currentLineFound = true;
                    else
                        before.offer(line.getValue());
                    if (before.size() > 29 / 2)
                        before.poll();
                } else {
                    after.offer(line.getValue());
                    if (after.size() > 29 / 2)
                        break;
                }
            }
            int lineY = 60;
            g.setFill(Color.WHITE);
            for (String line : before) {
                g.fillText(line, 192, lineY);
                lineY += 12;
            }
            g.setFill(Color.CYAN);
            g.fillText(currentLine, 192, lineY);
            lineY += 12;
            g.setFill(Color.WHITE);
            for (String line : after) {
                g.fillText(line, 192, lineY);
                lineY += 12;
            }
        }

        // ================================= Object Attribute Memory =================================
        g.setFont(Font.font("monospaced", FontWeight.BOLD, 35));
        g.setFill(Color.LIMEGREEN);
        g.fillText("OAM Memory", 540 - 40, 40);
        g.setFont(Font.font("monospaced", FontWeight.BOLD, 12));
        g.setFill(Color.WHITE);
        synchronized (nes.getPpu()) {
            for (int i = 0; i < 32; i++) {
                String s = String.format("%02X:", i) + " (" + String.format("%03d", nes.getPpu().getOams()[i].getX()) + ", " + String.format("%03d", nes.getPpu().getOams()[i].getY()) + ") ID: " + String.format("%02X", nes.getPpu().getOams()[i].getId()) + " AT: " + String.format("%02X", nes.getPpu().getOams()[i].getAttribute());
                g.fillText(s, 410, 60 + 11 * i);
            }
            for (int i = 0; i < 32; i++) {
                String s = String.format("%02X:", i + 32) + " (" + String.format("%03d", nes.getPpu().getOams()[i + 32].getX()) + ", " + String.format("%03d", nes.getPpu().getOams()[i + 32].getY()) + ") ID: " + String.format("%02X", nes.getPpu().getOams()[i + 32].getId()) + " AT: " + String.format("%02X", nes.getPpu().getOams()[i + 32].getAttribute());
                g.fillText(s, 630, 60 + 11 * i);
            }
        }
        g.drawImage(patterntable1_render_target, 415, 440, 192, 192);
        g.drawImage(patterntable2_render_target, 637, 440, 192, 192);

        g.drawImage(nametable1_render_target, 855, 50);
        g.drawImage(nametable2_render_target, 1117, 50);
        g.drawImage(nametable3_render_target, 855, 296);
        g.drawImage(nametable4_render_target, 1117, 296);
        g.setFill(Color.RED);
        switch (nes.getCartridge().getMirror()) {
            case HORIZONTAL:
                g.fillRect(1111, 50, 6, 486);
                break;
            case VERTICAL:
                g.fillRect(855, 290, 518, 6);
                break;
        }

        int palette_size = 14;
        g.setFill(Color.RED);
        g.fillRect(855 + selected_palette * (4 * palette_size + 10) - 5, 551, palette_size * 4 + 10, 4 * palette_size + 10);
        for (int p = 0; p < 8; p++) {
            for (int s = 0; s < 4; s++) {
                g.setFill(nes.getPpu().getColorFromPalette(p, s));
                g.fillRect(855 + s * palette_size + p * (4 * palette_size + 10), 556, palette_size, 4 * palette_size);
            }
        }
    }

    /**
     * Set the current RAM Page to be the 16th previous one
     * (loop when overflow)
     */
    public synchronized void ramPageLeftPlusEvent() {
        ram_page -= 0x10;
        redraw = true;
    }

    /**
     * Set the current RAM Page to be the 16th next one
     * (loop when overflow)
     */
    public synchronized void ramPageRightPlusEvent() {
        ram_page += 0x10;
        redraw = true;
    }

    /**
     * Set the current RAM Page to be the previous one
     * (loop when overflow)
     */
    public synchronized void ramPageLeftEvent() {
        ram_page--;
        redraw = true;
    }

    /**
     * Set the current RAM Page to be the next one
     * (loop when overflow)
     */
    public synchronized void ramPageRightEvent() {
        ram_page++;
        redraw = true;
    }

    /**
     * Advance by one CPU Instruction
     */
    public synchronized void cpuStepEvent() {
        if (!emulation_running) {
            do {
                nes.clock();
            } while (!nes.getCpu().complete());
            do {
                nes.clock();
            } while (nes.getCpu().complete());
            if (nes.getPpu().frame_complete) {
                frame_count++;
                nes.getPpu().frame_complete = false;
            }
        }
        redraw = true;
    }

    /**
     * Swap the currently selected palette
     * (loop when overflow)
     */
    public synchronized void paletteSwapEvent() {
        selected_palette = (selected_palette + 1) & 0x7;
        redraw = true;
    }

    /**
     * Advance the emulation by one frame
     */
    public synchronized void frameStepEvent() {
        super.frameStepEvent();
        frame_count++;
        redraw = true;
    }

}
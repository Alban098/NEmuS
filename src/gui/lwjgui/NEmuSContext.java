package gui.lwjgui;

import core.NES;
import core.cartridge.Cartridge;
import core.ppu.PPU_2C02;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import gui.inputs.InputMapper;
import gui.inputs.NESInputs;
import javafx.application.Platform;
import lwjgui.gl.Renderer;
import lwjgui.scene.Context;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;
import openGL.Fbo;
import openGL.Quad;
import openGL.filters.Pipeline;
import openGL.shader.ShaderProgram;
import openGL.Texture;
import utils.Dialogs;
import java.io.EOFException;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class implement the core of the emulator
 * it is responsible for audio timing and NES sync
 */
public class NEmuSContext implements Renderer {

    private final NES nes;
    private final InputMapper inputMapper;
    private final Fbo fbo;
    private final Texture screen_texture;
    private final Quad screen_quad;
    private final AudioContext ac;

    private ShaderProgram default_shader;
    private Pipeline pipeline;
    private String requested_rom;
    private boolean load_rom_requested = false;
    private boolean reset_requested = false;
    private boolean emulation_running = false;
    private boolean redraw = false;
    private boolean started = false;

    /**
     * Create a new emulator context
     *
     * @param windowHandle the parent window of the context
     */
    NEmuSContext(long windowHandle) {
        nes = new NES();
        inputMapper = new InputMapper(windowHandle);

        //We select the 3rd Mixer
        //no idea why 1 and 2 don't work ??
        JavaSoundAudioIO jsaIO = new JavaSoundAudioIO();
        jsaIO.selectMixer(3);
        ac = new AudioContext(jsaIO);

        //We enable texture and create the quad, fbo and texture used to render
        glEnable(GL_TEXTURE_2D);
        screen_texture = new Texture(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT, nes.getPpu().getScreenBuffer());
        fbo = new Fbo(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        screen_quad = new Quad();

        //We initialize the post processing pipeline
        try {
            pipeline = new Pipeline(screen_quad);
            default_shader = new ShaderProgram("shaders/vertex.glsl", "shaders/filters/no_filter.glsl");
        } catch (Exception e) {
            Platform.runLater(() -> Dialogs.showException("Shader Error", "An error occur during Shader Compilation", e));
            cleanUp();
            System.exit(-1);
        }

        //We initialize the Audio Engine
        nes.setSampleFreq((int) ac.getSampleRate());
        Function function = new Function(new WaveShaper(ac)) {
            public float calculate() {
                if (emulation_running) {
                    boolean sample_ready = false;
                    while (!sample_ready)
                        sample_ready = nes.clock();
                }
                return emulation_running ? (float) nes.final_audio_sample : 0;
            }
        };

        ac.out.addInput(function);
        ac.start();
    }

    /**
     * Clean up the memory, kill the windows and stop the audio context
     */
    void cleanUp() {
        pipeline.cleanUp();
        default_shader.cleanUp();
        screen_texture.cleanUp();

        ac.stop();
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
    }

    /**
     * Get the current user inputs (Keyboard and Gamepad 1 and 2) and write it to NES
     */
    private void InputHandling() {
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

    @Override
    public void render(Context context) {
        //If a ROM load has been requested
        if (load_rom_requested) {
            load_rom_requested = false;
            synchronized (nes) {
                emulation_running = false;
                try {
                    initEmulator(requested_rom);
                    emulation_running = true;
                    started = true;
                } catch (EOFException | InvalidFileException | UnsupportedMapperException e) {
                    Platform.runLater(() -> Dialogs.showException("ROM Loading Error", "An error occur during ROM Loading", e));
                }
            }
        }

        //If a Reset has been requested
        if (reset_requested) {
            reset_requested = false;
            boolean running = emulation_running;
            emulation_running = false;
            nes.reset();
            emulation_running = running;
        }

        //If we need to render the screen
        if ((emulation_running && nes.getPpu().frame_complete) || redraw) {
            nes.getPpu().frame_complete = false;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            InputHandling();
            screen_texture.load(nes.getPpu().getScreenBuffer());
            if (redraw)
                redraw = false;
        }
        if (started)
            renderGameScreen();
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
        pipeline.postProcess(fbo.getTexture());
    }

    /**
     * Notify the emulator that it needs to load a ROM on the next game loop
     *
     * @param filename the file to load
     */
    void fireLoadROMEvent(String filename) {
        requested_rom = filename;
        load_rom_requested = true;
    }

    /**
     * Notify the emulator that it needs to reset on the next game loop
     */
    void fireResetEvent() {
        if (nes.getCartridge() != null)
            reset_requested = true;
    }

    /**
     * Return the current post processing pipeline
     *
     * @return the post processing pipeline
     */
    public Pipeline getPipeline() {
        return pipeline;
    }

    /**
     * Pause the emulation if it has started
     *
     */
    public void pause() {
        if (nes.getCartridge() != null) {
            emulation_running = !emulation_running;
        }
    }

    /**
     * Advance the emulation by one frame
     */
    public void frameStepEvent() {
        if (!emulation_running && started) {
            do {
                nes.clock();
            } while (!nes.getPpu().frame_complete);
            do {
                nes.clock();
            } while (nes.getCpu().complete());
            nes.getPpu().frame_complete = false;
            redraw = true;
        }
    }

    /**
     * Advance by one CPU Instruction
     */
    public void cpuStepEvent() {
        if (!emulation_running && started) {
            do {
                nes.clock();
            } while (!nes.getCpu().complete());
            do {
                nes.clock();
            } while (nes.getCpu().complete());
            if (nes.getPpu().frame_complete) {
                nes.getPpu().frame_complete = false;
            }
        }
        redraw = true;
    }

    /**
     * Return whether or not the emulation is currently running
     *
     * @return is the emulation running
     */
    public boolean isEmulationRunning() {
        return emulation_running;
    }

    /**
     * Enable or Disable sampling in the APU
     *
     * @param enabled should sampling be activated
     */
    public void fireAudioRenderingEvent(boolean enabled) {
        nes.enableSoundRendering(enabled);
    }

    /**
     * Enable or Disable RAW Audio mode in the APU
     *
     * @param raw should RAW Audio mode be activated
     */
    public void fireRawAudioEvent(boolean raw) {
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

    /**
     * Set the mixer emulation mode to linear approximation or non linear approximation
     *
     * @param linear should the mixer be linearly approximated
     */
    public void linearEvent(boolean linear) {
        nes.getApu().setLinear(linear);
    }

    /**
     * Return the current NES of the emulator
     *
     * @return the current NES
     */
    public NES getNes() {
        return nes;
    }

    /**
     * Is the emulation started
     * needed to ensure the cartridge has been initialized
     *
     * @return has the emulation started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Return whether or not sound rendering is enabled
     *
     * @return is sound rendering enabled
     */
    public boolean isAudioRenderingEnabled() {
        return nes.isSoundRenderingEnabled();
    }

    /**
     * Return whether or not RAW audio is enabled
     *
     * @return is RAW audio rendering enabled
     */
    public boolean isRAWAudioEnabled() {
        return nes.getApu().isRAWAudioEnabled();
    }

    /**
     * Return whether or not Pulse 1 is enabled
     *
     * @return is Pulse 1 rendering enabled
     */
    public boolean isPulse1Rendered() {
        return nes.getApu().isPulse1Rendered();
    }

    /**
     * Return whether or not Pulse 2 is enabled
     *
     * @return is Pulse 2 rendering enabled
     */
    public boolean isPulse2Rendered() {
        return nes.getApu().isPulse2Rendered();
    }

    /**
     * Return whether or not Triangle is enabled
     *
     * @return is Triangle rendering enabled
     */
    public boolean isTriangleRendered() {
        return nes.getApu().isTriangleRendered();
    }

    /**
     * Return whether or not Noise is enabled
     *
     * @return is Noise rendering enabled
     */
    public boolean isNoiseRendered() {
        return nes.getApu().isNoiseRendered();
    }

    /**
     * Return whether or not DMC is enabled
     *
     * @return is DMC rendering enabled
     */
    public boolean isDMCRendered() {
        return nes.getApu().isDMCRendered();
    }

    /**
     * Return whether or not the mixer is linearly approximated
     *
     * @return is the mixer linearly approximated
     */
    public boolean isLinear() {
        return nes.getApu().isLinear();
    }
}

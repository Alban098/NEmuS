package core;

import gui.lwjgui.NEmuSContext;
import gui.lwjgui.windows.APUViewer;
import gui.lwjgui.windows.AudioOutput;
import gui.lwjgui.windows.AudioSettings;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WaveShaper;
import utils.Dialogs;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

public class AudioEngine {

    private final AudioContext ac;
    private final JavaSoundAudioIO jsaIO;
    private final List<AudioOutput> validOutputs;
    private AudioOutput selectedOutput;

    public AudioEngine(NEmuSContext emulatorContext) {
        jsaIO = new JavaSoundAudioIO();
        validOutputs = new ArrayList<>();

        verifyValidOutputs();
        if (validOutputs.size() == 0)
            Dialogs.showError("No suitable Audio Output found", "NEmuS was unable to find a suitable Audio Output, and therefore can't run properly :(");

        selectIO(validOutputs.get(0));
        AudioSettings.linkSoundIO(this);

        ac = new AudioContext(jsaIO);
        emulatorContext.nes.setSampleFreq((int) ac.getSampleRate());
        Function audioProcessor = new Function(new WaveShaper(ac)) {
            public float calculate() {
                if (emulatorContext.emulation_running) {
                    boolean sample_ready = false;
                    while (!sample_ready)
                        sample_ready = emulatorContext.nes.clock(APUViewer.hasInstance());
                }
                return emulatorContext.emulation_running ? (float) emulatorContext.nes.final_audio_sample : 0;
            }
        };
        ac.out.addInput(audioProcessor);
        ac.start();
    }

    private void verifyValidOutputs() {
        AudioContext checker = new AudioContext(jsaIO);

        int index = 0;
        //For each Mixer, we check if the defined function is ran, if so it's a valid output and it's added to the list of valid outputs
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            try {
                jsaIO.selectMixer(index);
                checker = new AudioContext(jsaIO);
                final boolean[] valid = {false};
                //If this Function run, it will change the state of the valid array
                Function check = new Function(new WaveShaper(checker)) {
                    public float calculate() {
                        valid[0] = true;
                        return 0;
                    }
                };
                checker.out.addInput(check);
                checker.start();

                Thread.sleep(200);

                if (valid[0])
                    validOutputs.add(new AudioOutput(index, info));
                checker.stop();
            } catch (InterruptedException ignored) {}
            index++;
        }
    }

    public List<AudioOutput> getValidOutputs() {
        return validOutputs;
    }

    public void start() {
        ac.start();
    }

    public void stop() {
        ac.stop();
    }

    public AudioOutput getSelectedOutput() {
        return selectedOutput;
    }

    public void selectIO(AudioOutput io) {
        selectedOutput = io;
        jsaIO.selectMixer(io.getId());
    }
}

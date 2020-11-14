package gui.lwjgui.windows;

import javax.sound.sampled.Mixer;

public class AudioOutput {

    private final int id;
    private final Mixer.Info info;

    public AudioOutput(int id, Mixer.Info info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public Mixer.Info getInfo() {
        return info;
    }

    public String toString() {
        return info.getName();
    }
}

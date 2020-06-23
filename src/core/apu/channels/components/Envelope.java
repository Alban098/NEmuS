package core.apu.channels.components;

/**
 * This class represents an Envelope that can be used to control the volume of an Audio Channel
 */
public class Envelope {

    public boolean started = false;
    public boolean disabled = false;

    public int volume = 0;
    public int output = 0;

    private int divider_count = 0;
    private int decay_count = 0;

    /**
     * Update the state of the Envelope
     *
     * @param loop does the envelope needs to loop when reaching 0
     */
    public void clock(boolean loop) {
        if (!started) {
            if (divider_count == 0) {
                divider_count = volume;
                if (decay_count == 0) {
                    if (loop)
                        decay_count = 15;
                } else
                    decay_count--;
            } else
                divider_count--;
            divider_count &= 0xFFFF;
        } else {
            started = false;
            decay_count = 15;
            divider_count = volume;
        }
        if (disabled)
            output = volume;
        else
            output = decay_count;
    }
}

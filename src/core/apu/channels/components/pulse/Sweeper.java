package core.apu.channels.components.pulse;

import utils.IntegerWrapper;

/**
 * This class represents a sweeper used to change the frequency of the audio signal
 */
public class Sweeper {

    public boolean enabled = false;
    public boolean down = false;
    public boolean reload = false;
    public boolean muted = false;
    public int shift = 0x00;
    public int period = 0x00;

    private int change = 0;
    private int timer = 0x00;

    /**
     * Update the state of the frequency sweeper
     *
     * @param target the data to extract the from
     */
    public void track(IntegerWrapper target) {
        if (enabled) {
            change = target.value >> shift;
            muted = (target.value < 8) || (target.value > 0x7FF);
        }
    }

    /**
     * Compute a tick of the frequency sweeper
     *
     * @param target  the sequencer reload value (will be modified)
     * @param channel which channel is selected (false = 0, true = 1)
     */
    public void clock(IntegerWrapper target, boolean channel) {
        if (timer == 0 && enabled && shift > 0 && !muted) {
            if (target.value >= 8 && change < 0x07FF) {
                if (down)
                    target.value -= change - (channel ? 1 : 0);
                else
                    target.value += change;
            }
        }
        if (enabled) {
            if (timer == 0 || reload) {
                timer = period;
                reload = false;
            } else
                timer--;
            muted = (target.value < 8) || (target.value > 0x7FF);
        }
    }
}

package core.apu.channels.components.pulse;

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
     * @param reload the sequencer reload value
     */
    public void track(int reload) {
        if (enabled) {
            change = reload >> shift;
            muted = (reload < 8) || (reload > 0x7FF);
        }
    }

    /**
     * Compute a tick of the frequency sweeper
     *
     * @param reload  the sequencer reload value (will be modified)
     * @param channel which channel is selected (false = 0, true = 1)
     * @return the new reload value
     */
    public int clock(int reload, int channel) {
        if (timer == 0 && enabled && shift > 0 && !muted) {
            if (reload >= 8 && change < 0x07FF) {
                if (down)
                    reload -= change - (channel & 1);
                else
                    reload += change;
            }
        }
        if (enabled) {
            if (timer == 0 || this.reload) {
                timer = period;
                this.reload = false;
            } else
                timer--;
            muted = (reload < 8) || (reload > 0x7FF);
        }
        return reload;
    }
}

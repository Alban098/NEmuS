package core.apu.components;

import utils.IntegerWrapper;

public class Sweeper {

    public boolean enabled = false;
    public boolean down = false;
    public boolean reload = false;
    public boolean muted = false;
    public int shift = 0x00;
    public int period = 0x00;

    private int change = 0;
    private int timer = 0x00;


    public void track(IntegerWrapper target) {
        if (enabled) {
            change = target.value >> shift;
            muted = (target.value < 8) || (target.value > 0x7FF);
        }
    }

    public boolean clock(IntegerWrapper target, boolean channel) {
        boolean changed = false;
        if (timer == 0 && enabled && shift > 0 && !muted) {
            if (target.value >= 8 && change < 0x07FF) {
                if (down)
                    target.value -= change - (channel ? 1 : 0);
                else
                    target.value += change;
                changed = true;
            }
        }
        if (enabled) {
            if (timer == 0 || reload) {
                timer = period;
                reload = false;
            }
            else
                timer--;
            muted = (target.value < 8) || (target.value > 0x7FF);
        }
        return changed;
    }
}

package core.apu.components;

public class Envelope {


    public boolean started = false;
    public boolean disabled = false;
    private int divider_count = 0;
    public int volume = 0;
    public int output = 0;
    private int decay_count = 0;


    public void clock(boolean bLoop) {
        if (!started) {
            if (divider_count == 0) {
                divider_count = volume;
                if (decay_count == 0) {
                    if (bLoop)
                        decay_count = 15;
                }
                else
                    decay_count--;
            }
            else
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

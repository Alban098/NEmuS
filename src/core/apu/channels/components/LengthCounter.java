package core.apu.channels.components;

/**
 * This class represents a Length Counter that will be used to count the number of steps left for a note
 */
public class LengthCounter {

    public int counter = 0x00;

    /**
     * Update the counter
     *
     * @param enabled is the counter enabled
     * @param halted  is the counter halted
     */
    public void clock(boolean enabled, boolean halted) {
        if (!enabled)
            counter = 0;
        else {
            if (counter > 0 && !halted)
                counter--;
        }
    }
}

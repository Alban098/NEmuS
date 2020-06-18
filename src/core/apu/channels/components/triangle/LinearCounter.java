package core.apu.channels.components.triangle;

/**
 * This class represents a Linear Counter that will be used to count the number of steps left for a note
 * It is similar to a Length Counter but is clock twice as frequently
 */
public class LinearCounter {

    public int counter = 0x00;
    public boolean reload = false;
    public int reloadValue = 0x00;

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
            if (reload)
                counter = reloadValue;
            else if (counter > 0)
                counter--;
            if (!halted)
                reload = false;
        }
    }
}

package core.apu.channels.components;

import java.util.function.Function;

/**
 * This class represents a sequencer used to store information about the Audio Channel and how it should behave
 */
public class Sequencer {

    public int sequence = 0;
    public int timer = 0;
    public int output = 0;
    public int reload = 0;

    /**
     * Update the sequencer
     *
     * @param enabled is the sequencer enabled
     * @param func    the operation that the sequencer should execute
     */
    public void clock(boolean enabled, Function<Integer, Integer> func) {
        if (enabled) {
            timer--;
            if (timer == -1) {
                timer = reload + 1;
                sequence = func.apply(sequence);
                output = sequence & 0x1;
            }
        }
    }
}

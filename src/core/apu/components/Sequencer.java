package core.apu.components;

import utils.IntegerWrapper;

import java.util.function.Function;

/**
 * This class represents a sequencer used to store information about the Audio Channel and how it should behave
 */
public class Sequencer {

    public int sequence = 0;
    public int timer = 0;
    public int output = 0;
    public IntegerWrapper reload = new IntegerWrapper();

    /**
     * Update the sequencer
     *
     * @param enabled is the sequencer enabled
     * @param func    the operation that the sequencer should execute
     * @return the rw sample of the sequencer
     */
    public int clock(boolean enabled, Function<Integer, Integer> func) {
        if (enabled) {
            timer--;
            if (timer == -1) {
                timer = reload.value + 1;
                sequence = func.apply(sequence);
                output = sequence & 0x1;
            }
        }
        return output;
    }
}

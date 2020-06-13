package core.apu.components;

import utils.IntegerWrapper;

import java.util.function.Function;

/**
 * This class represents a sequencer used to store information about the Audio Channel and how it should behave
 */
public class TriangleSequencer {

    private int[] sequence = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private int sequenceIndex = 0;
    public int timer = 0;
    public int output = 0;
    public IntegerWrapper reload = new IntegerWrapper();

    /**
     * Update the sequencer
     *
     * @param enabled is the sequencer enabled
     * @return the raw sample of the sequencer
     */
    public int clock(boolean enabled) {
        if (enabled) {
            timer--;
            if (timer == -1) {
                timer = reload.value + 1;
                sequenceIndex = (sequenceIndex + 1) & 0x1F;
                output = sequence[sequenceIndex];
            }
        }
        return output;
    }
}

package core.apu.channels;

import core.apu.APU_2A03;
import core.apu.components.*;

/**
 * This class represent the Triangle Channel of the APU
 */
public class TriangleChannel {

    public double sample = 0;
    public double output = 0.0;

    public boolean enabled = false;
    public boolean halted = false;

    public TriangleSequencer sequencer;
    public TriangleOscillator oscillator;
    public LinearCounter linearCounter;
    public LengthCounter lengthCounter;

    /**
     * Create a new TriangleChannel
     */
    public TriangleChannel() {
        sequencer = new TriangleSequencer();
        oscillator = new TriangleOscillator();
        lengthCounter = new LengthCounter();
        linearCounter = new LinearCounter();
    }

    /**
     * Update the Linear Counter
     *
     * @param data control flag(1bit) reload value(7bit)
     */
    public void writeLinearCounter(int data) {
        halted = (data & 0x80) == 0x80;
        linearCounter.reloadValue = data & 0x7F;
    }

    /**
     * Set the 8 low bit of the sequencer reload value
     *
     * @param data the value of the 8 lsb of the sequencer reload value
     */
    public void writeTimerLow(int data) {
        sequencer.reload.value = (sequencer.reload.value & 0xFF00) | data;
    }

    /**
     * Set the 8 high bit of the sequencer reload value
     * also reset the sequencer timer to the complete reload value
     *
     * @param data the value of the 8 msb of the sequencer reload value
     */
    public void writeTimerHigh(int data) {
        sequencer.reload.value = (sequencer.reload.value & 0x00FF) | ((data & 0x7) << 8);
    }

    /**
     * Set the length counter to a specific value indexed from a length table
     *
     * @param data length tabled index(5bit) unused(3bit)
     */
    public void writeLengthCounterLoad(int data) {
        lengthCounter.counter = APU_2A03.length_table[(data & 0xF8) >> 3];
        linearCounter.reload = true;
    }

    /**
     * Compute the sample of the channel
     */
    public void compute(double time, boolean raw) {
        if (enabled && lengthCounter.counter > 0 && linearCounter.counter > 0) {
            sequencer.clock(enabled);
            if (sequencer.timer > 2) {
                if (raw) {
                    output = sequencer.output / 15.0;
                } else {
                    oscillator.frequency = 1789773.0f / (32.0f * (sequencer.reload.value + 1));
                    sample = oscillator.sample(time);
                    output = (sample + 1) * 0.5;
                }
            }
        } else
            output = 0;
    }
}
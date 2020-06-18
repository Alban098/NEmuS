package core.apu.channels;

import core.apu.APU_2A03;
import core.apu.channels.components.*;
import core.apu.channels.components.triangle.LinearCounter;
import core.apu.channels.components.triangle.TriangleSequencer;

/**
 * This class represent the Triangle Channel of the APU
 */
public class TriangleChannel {

    public double sample = 0.0;

    private TriangleSequencer sequencer;
    private LinearCounter linear_counter;
    private LengthCounter length_counter;

    private boolean enabled = false;
    private boolean halted = false;

    private double last_period = 0;
    private double last_sequencer_output = 0;

    /**
     * Create a new TriangleChannel
     */
    public TriangleChannel() {
        sequencer = new TriangleSequencer();
        length_counter = new LengthCounter();
        linear_counter = new LinearCounter();
    }

    /**
     * Update the Linear Counter
     *
     * @param data control flag(1bit) reload value(7bit)
     */
    public void writeLinearCounter(int data) {
        halted = (data & 0x80) == 0x80;
        linear_counter.reloadValue = data & 0x7F;
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
    public void loadLengthCounter(int data) {
        if (enabled) {
            length_counter.counter = APU_2A03.length_table[(data & 0xF8) >> 3];
            linear_counter.reload = true;
        }
    }

    /**
     * Compute the sample of the channel
     */
    public void computeSample(double timePerCycle, boolean raw) {
        last_period += timePerCycle;
        double period = (sequencer.reload.value + 1) * timePerCycle;
        double lastSample = sample;
        if (enabled && length_counter.counter > 0 && linear_counter.counter > 0) {
            sequencer.clock(true);
            if (sequencer.timer > 2) {
                last_sequencer_output = sequencer.output == 0 ? last_sequencer_output : sequencer.output - 7.5;
                if (raw) {
                    sample = sequencer.output / 15.0;
                } else {
                    int lower = sequencer.output;
                    int higher = sequencer.sequence[(sequencer.sequenceIndex + 1) & 0x1F];
                    double percent = last_period/ period;
                    if (lower == 15 && higher == 15) {
                        if (percent <= 0.5)
                            sample = ((16 - 15) * percent + 15 - 7.5) / 7.5;
                        else
                            sample = ((15 - 16) * percent + 16 - 7.5) / 7.5;
                    } else if (lower == 0 && higher == 0){
                        if (percent <= 0.5)
                            sample = (-percent - 7.5) / 7.5;
                        else
                            sample = (percent - 1 - 7.5) / 7.5;
                    } else
                        sample = ((higher - lower) * percent + lower - 7.5) / 7.5;
                }
            }
        } else
            sample = 0;

        //We smooth the output to avoid artifacts
        //if the channel is started we mute the sample until the cycle reaches a 0
        if (!raw && lastSample == 0 && (sample - lastSample > 0.02 || sample - lastSample < -0.02))
            sample = lastSample * 0.5;
        //if the channel is disabled when the sample isn't 0, we smooth the sound by finishing the cycle
        if (sample == 0 && (sample - lastSample > 0.02 || sample - lastSample < -0.02)) {
            double timeRem = period * Math.abs(last_sequencer_output);
            int nbStep = (int) (timeRem/timePerCycle);
            double smoothing_step = last_sequencer_output / 7.5 / nbStep;
            sample = lastSample - smoothing_step;
            if (sample < 0.05 && sample >= -0.05)
                sample *= 0.9;
            if (Math.signum(last_sequencer_output) != Math.signum(sample))
                sample *= 0.9;
        }
        if (sequencer.timer == 0)
            last_period = 0;
    }

    /**
     * Enable/Disable the channel
     *
     * @param enabled should the channel be enabled
     */
    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Set the Length Counter to 0
     */
    public void resetLengthCounter() {
        length_counter.counter = 0;
    }

    /**
     * Set the Linear Counter to 0
     */
    public void resetLinearCounter() {
        linear_counter.counter = 0;
    }

    /**
     * Clock the Length Counter
     */
    public void clockLengthCounter() {
        length_counter.clock(enabled, halted);
    }

    /**
     * Clock the Linear Counter
     */
    public void clockLinearCounter() {
        linear_counter.clock(enabled, halted);
    }

    /**
     * Return the Length Counter value
     *
     * @return the Length Counter value
     */
    public int getLengthCounter() {
        return length_counter.counter;
    }
}
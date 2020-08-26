package core.apu.channels;

import core.apu.APU_2A03;
import core.apu.channels.components.Envelope;
import core.apu.channels.components.LengthCounter;
import core.apu.channels.components.Sequencer;
import core.apu.channels.components.pulse.Oscillator;
import core.apu.channels.components.pulse.Sweeper;

/**
 * This class represent a Pulse Channel of the APU
 */
public class PulseChannel {

    private final Sequencer sequencer;
    private final Envelope envelope;
    private final LengthCounter length_counter;
    private final Sweeper sweeper;
    private final Oscillator oscillator;

    public double sample = 0.0;

    private boolean enabled = false;
    private boolean halted = false;

    /**
     * Create a new PulseChannel
     */
    public PulseChannel() {
        sequencer = new Sequencer();
        oscillator = new Oscillator();
        envelope = new Envelope();
        length_counter = new LengthCounter();
        sweeper = new Sweeper();
    }

    /**
     * Update the duty cycle, the volume envelope, and the state of the sequencer
     *
     * @param data duty cycle(2bit) sequencer halted(1bit) envelope enabled(1bit) volume envelope(4bit)
     */
    public void writeDutyCycle(int data) {
        switch ((data & 0xC0) >> 6) {
            case 0x00 -> {
                sequencer.sequence = 0b00000001;
                oscillator.duty_cycle = 0.125f;
            }
            case 0x01 -> {
                sequencer.sequence = 0b00000011;
                oscillator.duty_cycle = 0.250f;
            }
            case 0x02 -> {
                sequencer.sequence = 0b00001111;
                oscillator.duty_cycle = 0.500f;
            }
            case 0x03 -> {
                sequencer.sequence = 0b11111100;
                oscillator.duty_cycle = 0.750f;
            }
        }
        halted = (data & 0x20) == 0x20;
        envelope.volume = (data & 0x0F);
        envelope.disabled = (data & 0x10) == 0x10;
    }

    /**
     * Update the frequency sweeper of the channel
     *
     * @param data enabled(1bit) period(3bit) down(1bit) shift(3bit)
     */
    public void writeSweep(int data) {
        sweeper.enabled = (data & 0x80) == 0x80;
        sweeper.period = (data & 0x70) >> 4;
        sweeper.down = (data & 0x08) == 0x08;
        sweeper.shift = data & 0x07;
        sweeper.reload = true;
    }

    /**
     * Set the 8 low bit of the sequencer reload value
     *
     * @param data the value of the 8 lsb of the sequencer reload value
     */
    public void writeTimerLow(int data) {
        sequencer.reload = (sequencer.reload & 0xFF00) | data;
    }

    /**
     * Set the 8 high bit of the sequencer reload value
     * also reset the sequencer timer to the complete reload value
     *
     * @param data the value of the 8 msb of the sequencer reload value
     */
    public void writeTimerHigh(int data) {
        sequencer.reload = (sequencer.reload & 0x00FF) | ((data & 0x7) << 8);
        sequencer.timer = sequencer.reload;
    }

    /**
     * Set the length counter to a specific value indexed from a length table
     *
     * @param data length tabled index(5bit) unused(3bit)
     */
    public void writeLengthCounter(int data) {
        if (enabled) {
            length_counter.counter = APU_2A03.length_table[(data & 0xF8) >> 3];
            envelope.started = true;
        }
    }

    /**
     * Compute the sample of the channel
     */
    public void computeSample(double time, boolean raw) {
        if (enabled && length_counter.counter > 0 && !sweeper.muted && envelope.output > 2) {
            sequencer.clock(true, s -> (((s & 0x01) << 7) | ((s & 0xFE) >> 1)));
            if (sequencer.timer >= 8) {
                if (raw) {
                    sample = sequencer.output * ((envelope.output - 1) / 22.0);
                } else {
                    oscillator.frequency = 1789773.0f / (16.0f * (sequencer.reload + 1));
                    oscillator.amplitude = (envelope.output - 1) / 16.0f;
                    sample = oscillator.sample(time) / 2;
                }
            }
        } else
            sample = 0;
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
     * Clock the Length Counter
     */
    public void clockLengthCounter() {
        length_counter.clock(enabled, halted);
    }

    /**
     * Return the Length Counter value
     *
     * @return the Length Counter value
     */
    public int getLengthCounter() {
        return length_counter.counter;
    }

    /**
     * Clock the Envelope
     */
    public void clockEnvelope() {
        envelope.clock(halted);
    }

    /**
     * Clock the Frequency Sweeper
     */
    public void clockSweeper(int channel) {
        sequencer.reload = sweeper.clock(sequencer.reload, channel);
    }

    /**
     * Update the state of the Frequency Sweeper
     */
    public void trackSweeper() {
        sweeper.track(sequencer.reload);
    }
}
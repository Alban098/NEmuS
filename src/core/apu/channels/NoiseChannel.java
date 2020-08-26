package core.apu.channels;

import core.apu.APU_2A03;
import core.apu.channels.components.Envelope;
import core.apu.channels.components.LengthCounter;
import core.apu.channels.components.Sequencer;

/**
 * This class represent a Noise Channel of the APU
 */
public class NoiseChannel {

    public double sample = 0;

    private final Envelope envelope;
    private final LengthCounter length_counter;
    private final Sequencer sequencer;

    private boolean enabled = false;
    private boolean halted = false;
    private boolean mode = false;

    /**
     * Create a new NoiseChannel
     */
    public NoiseChannel() {
        envelope = new Envelope();
        length_counter = new LengthCounter();
        sequencer = new Sequencer();
    }

    /**
     * Set the Envelope's volume and disabled flag
     *
     * @param data chanel halted(1bit) envelope disabled(1bit) envelope volume(4bit)
     */
    public void writeEnvelope(int data) {
        envelope.volume = data & 0x0F;
        envelope.disabled = (data & 0x10) == 0x10;
        halted = (data & 0x20) == 0x20;
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
     * Update the reload value of the sequencer
     *
     * @param data represent which bit to set to the 16bit reload register
     */
    public void updateReload(int data) {
        if (enabled) {
            mode = (data & 0x80) == 0x80;
            switch (data & 0x0F) {
                case 0x00 -> sequencer.reload = 0;
                case 0x01 -> sequencer.reload = 4;
                case 0x02 -> sequencer.reload = 8;
                case 0x03 -> sequencer.reload = 16;
                case 0x04 -> sequencer.reload = 32;
                case 0x05 -> sequencer.reload = 64;
                case 0x06 -> sequencer.reload = 96;
                case 0x07 -> sequencer.reload = 128;
                case 0x08 -> sequencer.reload = 160;
                case 0x09 -> sequencer.reload = 202;
                case 0x0A -> sequencer.reload = 254;
                case 0x0B -> sequencer.reload = 380;
                case 0x0C -> sequencer.reload = 508;
                case 0x0D -> sequencer.reload = 1016;
                case 0x0E -> sequencer.reload = 2034;
                case 0x0F -> sequencer.reload = 4068;
            }
        }
    }

    /**
     * Compute the sample of the channel
     */
    public void computeSample() {
        sequencer.clock(enabled, s -> (((s & 0x0001) ^ ((s & (mode ? 0x0040 : 0x0002)) >> 1)) << 14) | ((s & 0x7FFF) >> 1));
        sample = 0;

        if (length_counter.counter > 0 && (sequencer.sequence & 0x01) != 0)
            sample = (double) sequencer.output * ((double) (envelope.output - 1) / 16.0) * 0.5;
        if (!enabled)
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
     * Set the Sequencer's sequence
     *
     * @param seq the new sequence
     */
    public void setSequence(int seq) {
        sequencer.sequence = seq;
    }

}

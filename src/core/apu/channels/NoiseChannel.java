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

    private Envelope envelope;
    private LengthCounter length_counter;
    private Sequencer sequencer;

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
                case 0x00:
                    sequencer.reload.value = 0;
                    break;
                case 0x01:
                    sequencer.reload.value = 4;
                    break;
                case 0x02:
                    sequencer.reload.value = 8;
                    break;
                case 0x03:
                    sequencer.reload.value = 16;
                    break;
                case 0x04:
                    sequencer.reload.value = 32;
                    break;
                case 0x05:
                    sequencer.reload.value = 64;
                    break;
                case 0x06:
                    sequencer.reload.value = 96;
                    break;
                case 0x07:
                    sequencer.reload.value = 128;
                    break;
                case 0x08:
                    sequencer.reload.value = 160;
                    break;
                case 0x09:
                    sequencer.reload.value = 202;
                    break;
                case 0x0A:
                    sequencer.reload.value = 254;
                    break;
                case 0x0B:
                    sequencer.reload.value = 380;
                    break;
                case 0x0C:
                    sequencer.reload.value = 508;
                    break;
                case 0x0D:
                    sequencer.reload.value = 1016;
                    break;
                case 0x0E:
                    sequencer.reload.value = 2034;
                    break;
                case 0x0F:
                    sequencer.reload.value = 4068;
                    break;
            }
        }
    }

    /**
     * Compute the sample of the channel
     */
    public void computeSample() {
        sequencer.clock(enabled, s -> (((s & 0x0001) ^ ((s & (mode ? 0x0040 : 0x0002)) >> 1)) << 14) | ((s & 0x7FFF) >> 1));
        sample = 0;

        if (length_counter.counter > 0 && (sequencer.sequence & 0x01) != 0) {
            sample = (double) sequencer.output * ((double) (envelope.output - 1) / 16.0);
        }
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

package core.apu.channels;

import core.apu.components.Envelope;
import core.apu.components.LengthCounter;
import core.apu.components.Sequencer;

/**
 * This class represent a Noise Channel of the APU
 */
public class NoiseChannel {

    public double output = 0;

    public boolean enabled = false;
    public boolean halted = false;
    public Envelope envelope;
    public LengthCounter lengthCounter;
    public Sequencer sequencer;

    /**
     * Create a new NoiseChannel
     */
    public NoiseChannel() {
        envelope = new Envelope();
        lengthCounter = new LengthCounter();
        sequencer = new Sequencer();
    }

    /**
     * Update the reload value of the sequencer
     *
     * @param data represent which bit to set to the 16bit reload register
     */
    public void updateReload(int data) {
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

    /**
     * Compute the sample of the channel
     */
    public void compute() {
        sequencer.clock(enabled, s -> (((s & 0x0001) ^ ((s & 0x0002) >> 1)) << 14) | ((s & 0x7FFF) >> 1));

        if (lengthCounter.counter > 0 && sequencer.timer >= 8) {
            output = (double) sequencer.output * ((double) (envelope.output - 1) / 16.0);
        }
    }
}

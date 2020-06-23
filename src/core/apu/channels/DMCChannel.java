package core.apu.channels;

import core.NES;
import core.apu.channels.components.dmc.MemoryReader;

/**
 * This class represent a DMC Channel of the APU
 */
public class DMCChannel {

    private static final int[] rate_table = {428, 380, 340, 320, 286, 254, 226, 214, 190, 160, 142, 128, 106, 84, 72, 54};

    private final NES nes;
    private final MemoryReader memoryReader;
    private final OutputUnit outputUnit;

    public double output = 0;

    private boolean interrupt = false;
    private boolean irqEnabled = false;
    private boolean loop = false;
    private int sample_address = 0x00;
    private int sample_length = 0x00;
    private int sample_buffer = 0x00;
    private int rate = 0x00;
    private int counter = 0x00;

    /**
     * Create a new DMC Channel
     *
     * @param nes the current NES used to read sample from memory
     */
    public DMCChannel(NES nes) {
        this.nes = nes;
        memoryReader = new MemoryReader(nes);
        outputUnit = new OutputUnit();
    }

    /**
     * Write the rate at which the sample must be played
     * also set the irq and loop flags
     * the actual rate is indexed using the provided data's 3 LSB
     *
     * @param data irq enabled(1bit) loop flag(1bit) Unused((2bit) rate index(4bit)
     */
    public void writeRate(int data) {
        irqEnabled = (data & 0x80) == 0x80;
        loop = (data & 0x40) == 0x40;
        rate = rate_table[data & 0xF];
        counter = rate - 1;
        if (!irqEnabled)
            interrupt = false;
    }

    /**
     * directly load a sample to the DAC
     * Effective sample = sample & 0x7F (7th bit discarded)
     *
     * @param data the sample
     */
    public void directLoad(int data) {
        outputUnit.output = data & 0x7F;
    }

    /**
     * Write the sample address
     * Effective sample address = 0xC000 + (data * 64)
     *
     * @param data the sample address
     */
    public void writeSampleAddr(int data) {
        sample_address = 0xC000 + (data << 6);
    }

    /**
     * Write the sample length
     * Effective sample length = data << 4 + 1
     *
     * @param data the sample length
     */
    public void writeSampleLength(int data) {
        sample_length = (data << 4) | 0x01;
    }

    /**
     * Clock the channel and decrement its timer
     */
    public void clock() {
        counter--;
    }

    /**
     * Compute the sample of the channel
     */
    public void computeSample() {
        //If the sub-sample is finished but the sample isn't we fetch the next one
        if (sample_buffer == 0x00 && memoryReader.bytes_remaining > 0) {
            //The CPU is halted for 4 cycles
            nes.haltCPU(4);
            sample_buffer = memoryReader.getSample();
            //If the sample is finished we either loop or trigger an interrupt if possible
            if (memoryReader.bytes_remaining == 0) {
                if (loop) {
                    memoryReader.current_address = sample_address;
                    memoryReader.bytes_remaining = sample_length;
                } else if (irqEnabled)
                    interrupt = true;
            }
        }

        //If the timer reaches 0, we shift the output register and update the current sample
        if (counter <= 0) {
            counter = rate - 1;
            if (!outputUnit.silence) {
                //The sample is incremented or decremented according to the shifted bit
                if ((outputUnit.shift_register & 0x1) == 0x1 && outputUnit.output <= 125)
                    outputUnit.output += 2;
                if ((outputUnit.shift_register & 0x1) == 0x0 && outputUnit.output >= 2)
                    outputUnit.output -= 2;
            }
            outputUnit.shift_register >>= 1;
            outputUnit.bits_remaining--;
        }

        //If the output register is empty we reload it with the sample buffer
        if (outputUnit.bits_remaining == 0) {
            outputUnit.bits_remaining = 8;
            //If the sample buffer is empty, the channel is silenced
            outputUnit.silence = sample_buffer == 0x00;
            //If not the sample buffer is emptied in the output register
            if (!outputUnit.silence) {
                outputUnit.shift_register = sample_buffer;
                sample_buffer = 0x00;
            }
        }
        //The output is pushed to the DAC
        output = (output + (outputUnit.output / 64.0)) / 4;
    }

    /**
     * Pull the IRQ line low
     */
    public void clearIrq() {
        interrupt = false;
    }

    /**
     * Make the Reader think the current sample is finished
     */
    public void clearReader() {
        memoryReader.bytes_remaining = 0;
    }

    /**
     * Return whether or not there is bytes left in the current sample
     *
     * @return Has the current sample finished playing
     */
    public boolean hasBytesLeft() {
        return memoryReader.bytes_remaining > 0;
    }

    /**
     * Return whether or not the channel has its IRQ pulled high
     *
     * @return Does the channel has its IRQ pulled high
     */
    public boolean hasInterruptTriggered() {
        return interrupt;
    }
}

/**
 * This class is just a wrapper for the OutputUnit
 */
class OutputUnit {

    int shift_register = 0x00;
    int bits_remaining = 0x00;
    int output = 0x00;
    boolean silence = false;
}

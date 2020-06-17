package core.apu.channels;

import core.NES;
import core.apu.components.dmc.MemoryReader;
import core.apu.components.dmc.OutputUnit;

/**
 * This class represent a Noise Channel of the APU
 */
public class DMCChannel {

    private static final int[] rate_table = {428, 380, 340, 320, 286, 254, 226, 214, 190, 160, 142, 128, 106, 84, 72, 54};

    private NES nes;

    public int output = 0;

    public MemoryReader memoryReader;
    public OutputUnit outputUnit;

    public boolean interrupt = false;
    public boolean irqEnabled = false;
    public boolean loop = false;
    public int sample_address = 0x00;
    public int sample_length = 0x00;
    public int sample_buffer = 0x00;
    public int timer = 0x00;
    public int rate = 0x00;

    public DMCChannel(NES nes) {
        this.nes = nes;
        memoryReader = new MemoryReader(nes);
        outputUnit = new OutputUnit();
    }

    public void writeRate(int data) {
        irqEnabled = (data & 0x80) == 0x80;
        loop = (data & 0x40) == 0x40;
        rate = rate_table[data & 0xF];
        if (!irqEnabled)
            interrupt = false;
    }

    public void directLoad(int data) {
       outputUnit.output = data & 0x7F;
    }

    public void writeSampleAddr(int data) {
        sample_address = 0xC000 + (data * 64);
    }

    public void writeSampleLength(int data) {
        sample_length = data * 16 + 1;
    }

    /**
     * Compute the sample of the channel
     */
    public void clock() {
        if (sample_buffer == 0x00 && memoryReader.bytes_remaining > 0) {
            nes.haltCPU(4);
            sample_buffer = memoryReader.getSample();
            if (memoryReader.bytes_remaining == 0) {
                if (loop) {
                    memoryReader.current_address = sample_address;
                    memoryReader.bytes_remaining = sample_length;
                } else if (irqEnabled)
                    interrupt = true;
            }
        }

        if (!outputUnit.silence) {
            if ((outputUnit.shift_register & 0x1) == 0x1 && outputUnit.output <= 125)
                outputUnit.output += 2;
            if ((outputUnit.shift_register & 0x1) == 0x0 && outputUnit.output >= 2)
                outputUnit.output -= 2;
        }
        outputUnit.shift_register >>= 1;
        outputUnit.bits_remaining--;

        if (outputUnit.bits_remaining == 0) {
            outputUnit.bits_remaining = 8;
            outputUnit.silence = sample_buffer == 0x00;
            if (!outputUnit.silence) {
                outputUnit.shift_register = sample_buffer;
                sample_buffer = 0x00;
            }
        }
        output = (outputUnit.output/128 + output) / 2;
    }
}

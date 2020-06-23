package core.apu.channels.components.dmc;

import core.NES;

/**
 * This class represents the interface between the DMC Channel and the RAM
 */
public class MemoryReader {

    private final NES bus;

    public int current_address = 0x00;
    public int bytes_remaining = 0x00;

    /**
     * Create a new MemoryReader connected to a NES
     *
     * @param bus the NES to read from
     */
    public MemoryReader(NES bus) {
        this.bus = bus;
    }

    /**
     * Get the next sample
     *
     * @return the next sample of the sequence, 0 if finished
     */
    public int getSample() {
        if (bytes_remaining > 0) {
            bytes_remaining--;
            int sample = bus.cpuRead(current_address, false);
            current_address = ((current_address + 1) & 0x0FFF) | 0x8000;
            return sample;
        }
        return 0;
    }

}

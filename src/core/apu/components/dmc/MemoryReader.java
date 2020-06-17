package core.apu.components.dmc;

import core.NES;

public class MemoryReader {

    public int current_address = 0x00;
    public int bytes_remaining = 0x00;
    private NES bus;

    public MemoryReader(NES bus) {
        this.bus = bus;
    }

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

package core.cartridge;

import utils.IntegerWrapper;

/**
 * This class implement the Mapped 000
 */
public class Mapper000 extends Mapper {

    /**
     * Create a new instance of Mapper000
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper000(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
    }

    /**
     * Map an Address the CPU want to read from to a Program Memory Address
     * if the Cartridge need to map it
     * @param addr the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    /**
     * Map an Address the CPU want to write to to a Program Memory Address
     * if the Cartridge need to map it
     * @param addr the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    /**
     * Map an Address the PPU want to read from to a Character Memory Address
     * if the Cartridge need to map it
     * @param addr the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            mapped.value = addr;
            return true;
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 000 the Character Memory is ROM
     * @param addr the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped) {
        return false;
    }
}

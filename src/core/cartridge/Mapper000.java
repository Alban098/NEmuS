package core.cartridge;

import utils.IntegerWrapper;

/**
 * This class implement the Mapped 000
 */
public class Mapper000 extends Mapper {

    /**
     * Create a new instance of Mapper000
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper000(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
    }

    /**
     * No mapping occur, the address is directly returned
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    /**
     * No mapping occur, the address is directly returned
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    /**
     * No mapping occur, the address is directly returned
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        if (addr <= 0x1FFF) {
            mapped.value = addr;
            return true;
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 000 the Character Memory is ROM
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        if (addr <= 0x1FFF) {
            if (nCHRBanks == 0) {
                mapped.value = addr;
                return true;
            }
        }
        return false;
    }
}

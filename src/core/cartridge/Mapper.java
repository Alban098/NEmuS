package core.cartridge;

import utils.IntegerWrapper;

/**
 * This class is an abstraction of the Mapper circuit present on the Cartridge
 */
abstract class Mapper {

    final int nPRGBanks;
    private final int nCHRBanks;

    /**
     * Create a new instance of Mapper
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    Mapper(int nPRGBanks, int nCHRBanks) {
        this.nPRGBanks = nPRGBanks;
        this.nCHRBanks = nCHRBanks;
    }

    /**
     * Map an Address the CPU want to read from to a Program Memory Address
     * if the Cartridge need to map it
     * @param addr the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean cpuMapRead(int addr, IntegerWrapper mapped);

    /**
     * Map an Address the CPU want to write to to a Program Memory Address
     * if the Cartridge need to map it
     * @param addr the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean cpuMapWrite(int addr, IntegerWrapper mapped);

    /**
     * Map an Address the PPU want to read from to a Character Memory Address
     * if the Cartridge need to map it
     * @param addr the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean ppuMapRead(int addr, IntegerWrapper mapped);

    /**
     * Map an Address the PPU want to write to to a Character Memory Address
     * if the Cartridge need to map it
     * @param addr the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean ppuMapWrite(int addr, IntegerWrapper mapped);
}

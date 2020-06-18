package core.cartridge.mappers;

import core.ppu.Mirror;
import utils.IntegerWrapper;

/**
 * This class is an abstraction of the Mapper circuit present on the Cartridge
 */
public abstract class Mapper {

    final int nb_PRG_banks;
    final int nb_CHR_banks;

    /**
     * Create a new instance of Mapper
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    Mapper(int nPRGBanks, int nCHRBanks) {
        this.nb_PRG_banks = nPRGBanks;
        this.nb_CHR_banks = nCHRBanks;
    }

    /**
     * Map an Address the CPU want to read from to a Program Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data);

    /**
     * Map an Address the CPU want to write to to a Program Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    public abstract boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data);

    /**
     * Map an Address the PPU want to read from to a Character Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @return Whether or not the Address was mapped
     */
    public abstract boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data);

    /**
     * Map an Address the PPU want to write to to a Character Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    public abstract boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data);

    /**
     * Interrogate the Mapper circuit for the mirroring mode
     *
     * @return the current mirroring mode
     */
    public Mirror mirror() {
        return Mirror.HARDWARE;
    }

    /**
     * Return whether or not the Mapper want to trigger an IRQ
     * can be used to switch CHR banks mid-frame
     *
     * @return false if not overridden
     */
    public boolean irqState() { return false; }

    /**
     * Notify the Mapper that the IRQ has been relayed to the CPU
     * Do nothing if not overridden
     */
    public void irqClear() {}

    /**
     * Notify the Mapper that one scanline has occurred
     * Do nothing if not overridden
     */
    public void scanline() {}

    /**
     * Reset the Mapper if it has processing capabilities
     */
    public void reset() {}

    /**
     * Return whether or not the Cartridge has internal RAM
     *
     * @return does the Cartridge has internal RAM, false if not overridden
     */
    public boolean hasRAM() {
        return false;
    }

    /**
     * Return the internal RAM if the Cartridge has one
     *
     * @return the internal RAM, null if not present
     */
    public byte[] getRAM() {
        return null;
    }
}

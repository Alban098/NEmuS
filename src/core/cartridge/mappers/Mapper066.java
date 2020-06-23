package core.cartridge.mappers;

import utils.IntegerWrapper;

/**
 * This class implements the iNES Mapper 066 (GxROM)
 */
public class Mapper066 extends Mapper {

    private int selected_PRG_bank = 0x00;
    private int selected_CHR_bank = 0x00;

    /**
     * Create a new instance of Mapper003
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper066(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
        reset();
    }

    /**
     * The Mapper map the lower 16Kb to the Bank selected by the PRGBankLow Register
     * and the upper 16Kb to the last PRG Bank
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        if (addr >= 0x8000) {
            mapped.value = (selected_PRG_bank * 0x8000) + (addr & 0x7FFF);
            return true;
        }
        return false;
    }

    /**
     * Map an Address the CPU want to write to to a Program Memory Address
     * if the Cartridge need to map it
     * if the address is in the upper 16Kb, the data is written to the Mapper Register
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        if (addr >= 0x8000) {
            selected_PRG_bank = (data & 0x30) >> 4;
            selected_CHR_bank = data & 0x03;
        }
        return false;
    }

    /**
     * Map an Address the PPU want to read from to a Character Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        if (addr <= 0x1FFF) {
            mapped.value = selected_CHR_bank * 0x2000 + addr;
            return true;
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 066 the Character Memory is ROM
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        return false;
    }


    /**
     * Reset the Mapper to the default state
     * (set the selected banks to the lower ones)
     */
    @Override
    public void reset() {
        selected_PRG_bank = 0x00;
        selected_CHR_bank = 0x00;
    }
}

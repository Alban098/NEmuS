package core.cartridge.mappers;

import utils.IntegerWrapper;

/**
 * This class implements the iNES Mapper 002 (UxROM)
 */
public class Mapper002 extends Mapper {

    private int selectedPRGBankLow = 0x00;
    private int selectedPRGBankHigh = 0x00;

    /**
     * Create a new instance of Mapper 002
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper002(int nPRGBanks, int nCHRBanks) {
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
        addr &= 0xFFFF;
        if (addr >= 0x8000 && addr <= 0xBFFF) {
            mapped.value = (selectedPRGBankLow * 0x4000) + (addr & 0x3FFF);
            return true;
        }
        if (addr >= 0xC000) {
            mapped.value = (selectedPRGBankHigh * 0x4000) + (addr & 0x3FFF);
            return true;
        }
        return false;
    }

    /**
     * If the address is in the upper 16Kb, the data is written to the Mapper Register
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        if (addr >= 0x8000) {
            selectedPRGBankLow = data & 0x0F;
        }
        return false;
    }

    /**
     * No mapping occur, the address is directly returned
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
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
     * The PPU never write, for Mapper 002 the Character Memory is ROM
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

    /**
     * Reset the lower PRG bank to the first and higher PRG bank to the last one
     */
    @Override
    public void reset() {
        selectedPRGBankLow = 0;
        selectedPRGBankHigh = nPRGBanks - 1;
    }
}

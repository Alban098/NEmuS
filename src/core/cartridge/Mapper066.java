package core.cartridge;

import core.ppu.Mirror;
import utils.ByteWrapper;
import utils.IntegerWrapper;

public class Mapper066 extends Mapper{

    private short selectedPGRBank = 0x00;
    private short selectedCHRBank = 0x00;

    /**
     * Create a new instance of Mapper003
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper066(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
    }

    /**
     * The Mapper map the lower 16Kb to the Bank selected by the PRGBankLow Register
     * and the upper 16Kb to the last PRG Bank
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, ByteWrapper data) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = selectedPGRBank * 0x8000 + (addr & 0x7FFF);
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
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, short data) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            selectedPGRBank = (short) ((data >> 4) & 0x03);
            selectedCHRBank = (short) (data & 0x03);
            mapped.value = addr;
        }
        return false;
    }

    /**
     * Map an Address the PPU want to read from to a Character Memory Address
     * if the Cartridge need to map it
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, ByteWrapper data) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            mapped.value = selectedCHRBank * 0x2000 + addr;
            return true;
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 003 the Character Memory is ROM
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, short data) {
        return false;
    }

    /**
     * The mirroring mode is hard wired inside the cartridge
     *
     * @return HARDWARE mirroring mode
     */
    @Override
    public Mirror mirror() {
        return Mirror.HARDWARE;
    }

    /**
     * There is nothing to reset here
     */
    @Override
    public void reset() {
        selectedPGRBank = 0x00;
        selectedCHRBank = 0x00;
    }
}

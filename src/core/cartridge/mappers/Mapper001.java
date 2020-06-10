package core.cartridge.mappers;

import core.ppu.Mirror;
import exceptions.InvalidFileException;
import utils.FileReader;
import utils.IntegerWrapper;

import java.io.EOFException;

/**
 * This class implements the iNES Mapper 001 (MCC1)
 */
public class Mapper001 extends Mapper {

    private int selectedCHRBank4Low = 0x00;
    private int selectedCHRBank4High = 0x00;
    private int selectedCHRBank8 = 0x00;

    private int selectedPRGBank16Low = 0x00;
    private int selectedPRGBank16High = 0x00;
    private int selectedPRGBank32 = 0x00;

    private int loadRegister = 0x00;
    private int loadRegisterCount = 0x00;
    private int controlRegister = 0X00;

    private Mirror mirroring_mode = Mirror.HORIZONTAL;

    private byte[] cartridgeRAM;

    /**
     * Create a new instance of Mapper001
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper001(int nPRGBanks, int nCHRBanks, String saveFile) {
        super(nPRGBanks, nCHRBanks);
        try {
            //The cartridge contains RAM, it can contains saves, we try to load it if it exist
            FileReader saveReader = new FileReader(saveFile);
            cartridgeRAM = saveReader.readBytes(32768);
        } catch (InvalidFileException | EOFException e) {
            cartridgeRAM = new byte[32768];
        }
        reset();
    }

    /**
     * Map the provided address from CPU space to the Cartridge Memory space if relevant
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        //The CPU try to read from internal RAM
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            data.value = cartridgeRAM[addr & 0x1FFF] & 0xFF;
            return true;
        }
        //The CPU load from PRG Memory
        if (addr >= 0x8000) {
            //We select the right Bank by reading the control register
            if ((controlRegister & 0b01000) == 0b01000) {
                if (addr <= 0xBFFF) {
                    mapped.value = (selectedPRGBank16Low * 0x4000) + (addr & 0x3FFF);
                    return true;
                }
                mapped.value = (selectedPRGBank16High * 0x4000) + (addr & 0x3FFF);
                return true;
            } else {
                mapped.value = (selectedPRGBank32 * 0x8000) + (addr & 0x7FFF);
                return true;
            }
        }
        return false;
    }

    /**
     * Map the provided address from CPU space to the Cartridge Memory space if relevant
     * Here the address is never mapped, a write only change the Mapper state
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        //The CPU try to write to internal RAM
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            cartridgeRAM[addr & 0x1FFF] = (byte) data;
            return true;
        }
        //The CPU try to configure the Mapper circuit
        if (addr >= 0x8000) {
            //If the data has LSB set, the shift register is cleared
            if ((data & 0x80) == 0x80) {
                loadRegister = 0x00;
                loadRegisterCount = 0;
                controlRegister = controlRegister | 0x0C;
            } else { //Otherwise we load the msb in BIT 4 and shift the register
                loadRegister >>= 1;
                loadRegister |= ((data & 0x01) << 4);
                loadRegisterCount++;
                loadRegisterCount &= 0xFF;

                //If the register is fully loaded (5 bits)
                if (loadRegisterCount == 5) {
                    //The address from the last write determine what the CPU want to do with the data in the shift register
                    int targetRegister = (addr >> 13) & 0x03;

                    if (targetRegister == 0) { // 0x8000 - 0x9FFF
                        //The mirroring mode is being changed
                        controlRegister = loadRegister & 0x1F;
                        switch (controlRegister & 0x03) {
                            case 0:
                                mirroring_mode = Mirror.ONESCREEN_LOW;
                                break;
                            case 1:
                                mirroring_mode = Mirror.ONESCREEN_HIGH;
                                break;
                            case 2:
                                mirroring_mode = Mirror.VERTICAL;
                                break;
                            case 3:
                                mirroring_mode = Mirror.HORIZONTAL;
                                break;
                        }
                    } else if (targetRegister == 1) { // 0xA000 - 0xBFFF
                        if ((controlRegister & 0b10000) == 0b10000) //We change the lower half of the CHR Memory range
                            selectedCHRBank4Low = loadRegister & 0x1F;
                        else //We change the entire CHR Memory range
                            selectedCHRBank8 = (loadRegister & 0x1E) >> 1;
                    } else if (targetRegister == 2) { // 0xC000 - 0xDFFF
                        if ((controlRegister & 0b10000) == 0b10000) //We change the lower half of the CHR Memory range
                            selectedCHRBank4High = loadRegister & 0x1F;
                    } else { // 0xE000 - 0xFFFF
                        //We extract the PRG Mode (2 16K Banks or 1 32K Bank
                        int prgMode = (controlRegister >> 2) & 0x03;
                        if (prgMode == 0 || prgMode == 1) { //32K mode, the selected bank is represented by bit 1 to 4, bit 0 is ignored
                            selectedPRGBank32 = (loadRegister & 0x0E) >> 1;
                        } else if (prgMode == 2) { //16K Mode with lower half fixed to the first bank
                            selectedPRGBank16Low = 0;
                            selectedPRGBank16High = loadRegister & 0x0F;
                        } else { //16K Mode with higher half fixed to the last bank
                            selectedPRGBank16Low = loadRegister & 0x0F;
                            selectedPRGBank16High = nPRGBanks - 1;
                        }
                    }
                    //The shift register is cleared
                    loadRegister = 0x00;
                    loadRegisterCount = 0;
                }
            }
        }
        return false;
    }

    /**
     * Map the provided address from PPU space to the Cartridge Memory space if relevant
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
            //If their is not banks we simply return the provided address
            if (nCHRBanks == 0) {
                mapped.value = addr;
                return true;
            } else {
                //We select the right Bank by reading the control register
                if ((controlRegister & 0b10000) == 0b10000) {
                    if (addr <= 0x0FFF) {
                        mapped.value = (selectedCHRBank4Low * 0x1000) + (addr & 0x0FFF);
                        return true;
                    }
                    mapped.value = (selectedCHRBank4High * 0x1000) + (addr & 0x0FFF);
                    return true;
                } else {
                    mapped.value = (selectedCHRBank8 * 0x2000) + (addr & 0x1FFF);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 001 the Character Memory is ROM
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        if (addr <= 0x1FFF) {
            if (nCHRBanks == 0) {
                mapped.value = addr;
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Return the current mirroring mode
     *
     * @return the current mirroring mode
     */
    @Override
    public Mirror mirror() {
        return mirroring_mode;
    }


    /**
     * Reset the Mapper to the default state
     */
    @Override
    public void reset() {
        controlRegister = 0x1C;
        loadRegister = 0x00;
        loadRegisterCount = 0x00;

        selectedCHRBank4Low = 0;
        selectedCHRBank4High = 0;
        selectedCHRBank8 = 0;

        selectedPRGBank16Low = 0;
        selectedPRGBank16High = nPRGBanks - 1;
        selectedPRGBank32 = 0;
    }

    /**
     * The Cartridge contains RAM
     *
     * @return always true
     */
    @Override
    public boolean hasRAM() {
        return true;
    }

    /**
     * Return the internal RAM
     *
     * @return the internal RAM
     */
    @Override
    public byte[] getRAM() {
        return cartridgeRAM;
    }
}

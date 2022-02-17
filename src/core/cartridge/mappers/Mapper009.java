package core.cartridge.mappers;

import core.ppu.Mirror;
import exceptions.InvalidFileException;
import utils.FileReader;
import utils.IntegerWrapper;

import java.io.EOFException;

/**
 * This class implements the iNES Mapper 009 (MMC2) used by Mike Tyson's Ounch Out!!!
 */
public class Mapper009 extends Mapper {

    private int selected_PRG_bank = 0x00;
    private int selected_CHR_bank_l0_FD = 0x00;
    private int selected_CHR_bank_l0_FE = 0x00;
    private int selected_CHR_bank_l1_FD = 0x00;
    private int selected_CHR_bank_l1_FE = 0x00;

    private int latch_0 = 0x00;
    private int latch_1 = 0x00;

    private Mirror mirroring_mode = Mirror.HORIZONTAL;

    private byte[] internal_ram;

    /**
     * Create a new instance of Mapper000
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     * @param saveFile  the name of the RAM dump file
     */
    public Mapper009(int nPRGBanks, int nCHRBanks, String saveFile) {
        super(nPRGBanks, nCHRBanks);
        try {
            //The cartridge contains RAM, it can contains saves, we try to load it if it exist
            FileReader saveReader = new FileReader(saveFile);
            internal_ram = saveReader.readBytes(32768);
        } catch (InvalidFileException | EOFException e) {
            internal_ram = new byte[32768];
        }
        reset();
    }

    /**
     * The address is mapped as follows
     * 0x6000 - 0x7FFF : Cartridge RAM
     * 0x8000 - 0x9FFF : The selected 8Kb PRG Bank
     * 0xA000 - 0xFFFF : The last 3 PRG Banks of the Cartridge
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        //The CPU try to read from internal RAM
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            data.value = internal_ram[addr & 0x1FFF] & 0xFF;
            return true;
        }
        //The CPU load from PRG Memory
        if (addr >= 0x8000 && addr <= 0x9FFF) {
            mapped.value = (selected_PRG_bank * 0x2000) + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xA000 && addr <= 0xFFFF) {
            mapped.value = 0x10000 | addr;
            return true;
        }
        return false;
    }

    /**
     * No mapping occur, the address is directly returned
     * But the Mapper's state is updated
     *
     * @param addr   the CPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        if (addr >= 0x6000 && addr <= 0x7FFF) { // Cartridge RAM
            mapped.value = -1;
            internal_ram[addr & 0x1FFF] = (byte) data;
            return true;
        } else if (addr >= 0xA000 && addr <= 0xAFFF) {
            selected_PRG_bank = data & 0x0F;
            return true;
        } else if (addr >= 0xB000 && addr <= 0xBFFF) {
            selected_CHR_bank_l0_FD = data & 0x1F;
            return true;
        } else if (addr >= 0xC000 && addr <= 0xCFFF) {
            selected_CHR_bank_l0_FE = data & 0x1F;
            return true;
        } else if (addr >= 0xD000 && addr <= 0xDFFF) {
            selected_CHR_bank_l1_FD = data & 0x1F;
            return true;
        } else if (addr >= 0xE000 && addr <= 0xEFFF) {
            selected_CHR_bank_l1_FE = data & 0x1F;
            return true;
        } else if (addr >= 0xF000 && addr <= 0xFFFF) {
            switch (data & 0x01) {
                case 0 -> mirroring_mode = Mirror.VERTICAL;
                case 1 -> mirroring_mode = Mirror.HORIZONTAL;
            }
            return true;
        }
        return false;
    }

    /**
     * The address is mapped as follows
     * 0x0000 - 0x0FFF : CHR Bank selected according to latch 0
     * 0x1000 - 0x1FFF : CHR Bank selected according to latch 1
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   if there is data to be read, it will be written there
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        if (addr <= 0x0FFF) {
            if (latch_0 == 0xFD) {
                mapped.value = (selected_CHR_bank_l0_FD * 0x1000) + (addr & 0x0FFF);
                return true;
            } else if (latch_0 == 0xFE) {
                mapped.value = (selected_CHR_bank_l0_FE * 0x1000) + (addr & 0x0FFF);
                return true;
            }
        } else if (addr <= 0x1FFF) {
            if (latch_1 == 0xFD) {
                mapped.value = (selected_CHR_bank_l1_FD * 0x1000) + (addr & 0x0FFF);
                return true;
            } else if (latch_1 == 0xFE) {
                mapped.value = (selected_CHR_bank_l1_FE * 0x1000) + (addr & 0x0FFF);
                return true;
            }
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 009 the Character Memory is ROM
     *
     * @param addr   the PPU Address to map
     * @param mapped the Wrapper where to store the Mapped Address
     * @param data   the data to write
     * @return Whether or not the Address was mapped
     */
    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        if (addr <= 0x1FFF) {
            if (nb_CHR_banks == 0) {
                mapped.value = addr;
                return true;
            }
        }
        return false;
    }

    /**
     * Update the latch if the mapper has some
     *
     * @param addr the address the PPU has read from
     */
    @Override
    public void updateLatch(int addr) {
        if (addr == 0x0FD8)
            latch_0 = 0xFD;
        else if (addr == 0x0FE8)
            latch_0 = 0xFE;
        else if (addr >= 0x1FD8 && addr <= 0x1FDF)
            latch_1 = 0xFD;
        else if (addr >= 0x1FE8 && addr <= 0x1FEF)
            latch_1 = 0xFE;
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
        selected_PRG_bank = 0x00;
        selected_CHR_bank_l0_FD = 0x00;
        selected_CHR_bank_l0_FE = 0x00;
        selected_CHR_bank_l1_FD = 0x00;
        selected_CHR_bank_l1_FE = 0x00;
        latch_0 = 0x00;
        latch_1 = 0x00;
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
        return internal_ram;
    }
}

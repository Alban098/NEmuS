package core.cartridge.mappers;

import core.ppu.Mirror;
import exceptions.InvalidFileException;
import utils.FileReader;
import utils.IntegerWrapper;

import java.io.EOFException;

/**
 * This class implements the iNES Mapper 004 (MMC3)
 */
public class Mapper004 extends Mapper {

    private int target_register = 0x00;
    private boolean flag_PRG_bank_mode = false;
    private boolean flag_CHR_inversion = false;

    private final int[] register;
    private final int[] chr_banks;
    private final int[] prg_banks;

    private boolean flag_IRQ_active = false;
    private boolean flag_IRQ_enabled = false;
    private int irq_counter = 0x0000;
    private int irq_reload = 0x0000;

    private byte[] internal_ram;

    private Mirror mirror = Mirror.HORIZONTAL;


    /**
     * Create a new instance of Mapper
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    public Mapper004(int nPRGBanks, int nCHRBanks, String saveFile) {
        super(nPRGBanks, nCHRBanks);
        register = new int[8];
        chr_banks = new int[8];
        prg_banks = new int[4];
        try {
            //The cartridge contains RAM, it can contains saves, we try to load it if it exist
            FileReader saveReader = new FileReader(saveFile);
            internal_ram = saveReader.readBytes(32 * 1024);
        } catch (InvalidFileException | EOFException e) {
            internal_ram = new byte[32 * 1024];
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
        //The CPU try to read from internal RAM
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            data.value = internal_ram[addr & 0x1FFF] & 0xFF;
            return true;
        }
        //Otherwise we select the appropriate PRG bank using the provided address
        if (addr >= 0x8000 && addr <= 0x9FFF) {
            mapped.value = prg_banks[0] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xA000 && addr <= 0xBFFF) {
            mapped.value = prg_banks[1] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xC000 && addr <= 0xDFFF) {
            mapped.value = prg_banks[2] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xE000) {
            mapped.value = prg_banks[3] + (addr & 0x1FFF);
            return true;
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
        //The CPU try to write to internal RAM
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            internal_ram[addr & 0x1FFF] = (byte) data;
            return true;
        }

        //The CPU try to configure the Mapper circuit
        if (addr >= 0x8000 && addr <= 0x9FFF) {
            if ((addr & 0x1) != 0x1) { //If the address if even the data represent which register we want to edit and the PRG and CHR modes
                target_register = data & 0x7;
                flag_PRG_bank_mode = (data & 0x40) == 0x40;
                flag_CHR_inversion = (data & 0x80) == 0x80;
            } else { //If the address is odd we edit the selected register
                register[target_register] = data;
                //We set the CHR Banks according to the state of the registers and the Inversion mode
                if (flag_CHR_inversion) {
                    chr_banks[0] = register[2] * 0x0400;
                    chr_banks[1] = register[3] * 0x0400;
                    chr_banks[2] = register[4] * 0x0400;
                    chr_banks[3] = register[5] * 0x0400;
                    chr_banks[4] = (register[0] & 0xFE) * 0x0400;
                    chr_banks[5] = register[0] * 0x0400 + 0x0400;
                    chr_banks[6] = (register[1] & 0xFE) * 0x0400;
                    chr_banks[7] = register[1] * 0x0400 + 0x0400;
                } else {
                    chr_banks[0] = (register[0] & 0xFE) * 0x0400;
                    chr_banks[1] = register[0] * 0x0400 + 0x0400;
                    chr_banks[2] = (register[1] & 0xFE) * 0x0400;
                    chr_banks[3] = register[1] * 0x0400 + 0x0400;
                    chr_banks[4] = register[2] * 0x0400;
                    chr_banks[5] = register[3] * 0x0400;
                    chr_banks[6] = register[4] * 0x0400;
                    chr_banks[7] = register[5] * 0x0400;
                }

                //We set the PRG Banks according to the state of the registers and the PRG mode
                if (flag_PRG_bank_mode) {
                    prg_banks[2] = (register[6] & 0x3F) * 0x2000;
                    prg_banks[0] = (nb_PRG_banks * 2 - 2) * 0x2000;
                } else {
                    prg_banks[0] = (register[6] & 0x3F) * 0x2000;
                    prg_banks[2] = (nb_PRG_banks * 2 - 2) * 0x2000;
                }
                prg_banks[1] = (register[7] & 0x3F) * 0x2000;
                prg_banks[3] = (nb_PRG_banks * 2 - 1) * 0x2000;
            }
            return false;
        }

        //The CPU try to set the mirroring mode
        if (addr >= 0xA000 && addr <= 0xBFFF) {
            if ((addr & 0x1) != 0x1) {
                if ((data & 0x1) == 0x1)
                    mirror = Mirror.HORIZONTAL;
                else
                    mirror = Mirror.VERTICAL;
            }
            return false;
        }

        //The CPU try to set the scanline that should trigger an IRQ
        if (addr >= 0xC000 && addr <= 0xDFFF) {
            if ((addr & 0x1) != 0x1)
                irq_reload = data;
            else
                irq_counter = 0;
            return false;
        }

        //The CPU try to activate/deactivate the IRQ triggering
        if (addr >= 0xE000) {
            if ((addr & 0x1) != 0x1) {
                flag_IRQ_enabled = false;
                flag_IRQ_active = false;
            } else {
                flag_IRQ_enabled = true;
            }
            return false;
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
        //We select the appropriate CHR bank using the provided address
        if (addr <= 0x03FF) {
            mapped.value = chr_banks[0] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x07FF) {
            mapped.value = chr_banks[1] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x0BFF) {
            mapped.value = chr_banks[2] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x0FFF) {
            mapped.value = chr_banks[3] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x13FF) {
            mapped.value = chr_banks[4] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x17FF) {
            mapped.value = chr_banks[5] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x1BFF) {
            mapped.value = chr_banks[6] + (addr & 0x03FF);
            return true;
        }
        if (addr <= 0x1FFF) {
            mapped.value = chr_banks[7] + (addr & 0x03FF);
            return true;
        }
        return false;
    }

    /**
     * The PPU never write, for Mapper 004 the Character Memory is ROM
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
     * Interrogate the Mapper circuit for the mirroring mode
     *
     * @return the current mirroring mode
     */
    @Override
    public Mirror mirror() {
        return mirror;
    }

    /**
     * Return whether or not the Mapper want to trigger an IRQ
     * can be used to switch CHR banks mid-frame
     *
     * @return does the Mapper need to trigger an IRQ
     */
    @Override
    public boolean irqState() {
        return flag_IRQ_active;
    }

    /**
     * Notify the Mapper that the IRQ has been relayed to the CPU
     */
    @Override
    public void irqClear() {
        flag_IRQ_active = false;
    }

    /**
     * Notify the Mapper that one scanline has occurred
     */
    @Override
    public void notifyScanline() {
        if (irq_counter == 0) {
            irq_counter = irq_reload;
        } else {
            irq_counter--;
        }
        if (irq_counter == 0 && flag_IRQ_enabled)
            flag_IRQ_active = true;
    }

    /**
     * Reset the Mapper to its default state
     */
    @Override
    public void reset() {
        target_register = 0;
        flag_PRG_bank_mode = false;
        flag_CHR_inversion = false;
        mirror = Mirror.HORIZONTAL;
        flag_IRQ_active = false;
        flag_IRQ_enabled = false;
        irq_counter = 0;
        irq_reload = 0;

        for (int i = 0; i < 4; i++)
            prg_banks[i] = 0;
        for (int i = 0; i < 8; i++) {
            chr_banks[i] = 0;
            register[i] = 0;
        }

        prg_banks[0] = 0;
        prg_banks[1] = 0x2000;
        prg_banks[2] = (nb_PRG_banks * 2 - 2) * 0x2000;
        prg_banks[3] = (nb_PRG_banks * 2 - 1) * 0x2000;
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

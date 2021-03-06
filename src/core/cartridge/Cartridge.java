package core.cartridge;

import core.cartridge.mappers.*;
import core.ppu.Mirror;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
import javafx.application.Platform;
import utils.Dialogs;
import utils.FileReader;
import utils.IntegerWrapper;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This class represent a physical Cartridge
 */
public class Cartridge {

    private final String filename;

    private int nb_PRG_banks;
    private int nb_CHR_banks;
    private byte[] prg_memory;
    private byte[] chr_memory;

    private final Mapper mapper;
    private final Mirror mirror;

    /**
     * Create a Cartridge and load a ROM into the emulator
     *
     * @param filename the path to the ROM
     */
    public Cartridge(String filename) throws InvalidFileException, UnsupportedMapperException, EOFException {
        int fileType = 1;
        this.filename = filename;

        //Initialize the file reader
        FileReader reader = new FileReader(filename);
        //Read the Header
        Header header = new Header(reader);

        //Extract the Mapper ID and Mirroring mode
        int mapperId = ((header.flag_7 >> 4) << 4) | (header.flag_6 >> 4);
        mirror = (header.flag_6 & 0x01) == 0x01 ? Mirror.VERTICAL : Mirror.HORIZONTAL;

        //Discard padding if necessary
        if ((header.flag_6 & 0x04) == 0x04)
            reader.readBytes(512);

        if ((header.flag_7 & 0x0C) == 0x08)
            fileType = 2;

        //If it's a iNES 1 file
        if (fileType == 1) {
            //Read Program Memory
            nb_PRG_banks = header.prg_rom_chunks;
            prg_memory = reader.readBytes(nb_PRG_banks * 16384);
            //Read Character Memory
            nb_CHR_banks = header.chr_rom_chunks;
            chr_memory = reader.readBytes(nb_CHR_banks * 8192);
            if (nb_CHR_banks == 0)
                chr_memory = new byte[8192];
        }
        //If it's a iNES 2 file
        if (fileType == 2) {
            //Read Program Memory
            nb_PRG_banks = (header.rom_msb & 0x0F) << 8 | (header.prg_rom_chunks & 0xFF);
            prg_memory = reader.readBytes(nb_PRG_banks * 16384);
            //Read Character Memory
            nb_CHR_banks = (header.rom_msb & 0xF0) | (header.chr_rom_chunks & 0xFF);
            chr_memory = reader.readBytes(nb_CHR_banks * 8192);
            if (nb_CHR_banks == 0)
                chr_memory = reader.readBytes(8192);
        }
        //Initialize the right Mapper
        switch (mapperId) {
            case 0 -> mapper = new Mapper000(nb_PRG_banks, nb_CHR_banks);
            case 1 -> mapper = new Mapper001(nb_PRG_banks, nb_CHR_banks, filename + ".sav");
            case 2 -> mapper = new Mapper002(nb_PRG_banks, nb_CHR_banks);
            case 3 -> mapper = new Mapper003(nb_PRG_banks, nb_CHR_banks);
            case 4 -> mapper = new Mapper004(nb_PRG_banks, nb_CHR_banks, filename + ".sav");
            case 9 -> mapper = new Mapper009(nb_PRG_banks, nb_CHR_banks, filename + ".sav");
            case 66 -> mapper = new Mapper066(nb_PRG_banks, nb_CHR_banks);
            default -> throw new UnsupportedMapperException("Mapper " + (mapperId & 0xFF) + " not implemented yet");
        }
    }

    /**
     * Will read a value from Program Memory if the Mapper allow it
     *
     * @param addr the address to read from
     * @param data the Wrapper where to store the read data
     * @return was the data searched in the Cartridge
     */
    public boolean cpuRead(int addr, IntegerWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapRead(addr, mapped, data)) {
            if (mapped.value == -1) return true;
            data.value = prg_memory[mapped.value] & 0xFF;
            return true;
        }
        return false;
    }

    /**
     * Will write the data into Program Memory if the Mapper allows it
     * and return whether or not the data was for the Cartridge
     *
     * @param addr the address to write
     * @param data the data to write
     * @return was the data for the Cartridge
     */
    public boolean cpuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapWrite(addr, mapped, data)) {
            if (mapped.value == -1) return true;
            prg_memory[mapped.value] = (byte) data;
            return true;
        }
        return false;
    }

    /**
     * Will read a value from Character Memory if the Mapper allow it
     *
     * @param addr the address to read from
     * @param data the Wrapper where to store the read data
     * @return was the data searched in the Cartridge
     */
    public boolean ppuRead(int addr, IntegerWrapper data) {
        addr &= 0xFFFF;
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapRead(addr, mapped, data)) {
            data.value = chr_memory[mapped.value] & 0xFF;
            return true;
        }
        return false;
    }

    /**
     * Will write the data into Character Memory if the Mapper allows it
     * and return whether or not the data was for the Cartridge
     *
     * @param addr the address to write
     * @param data the data to write
     * @return was the data for the Cartridge
     */
    public boolean ppuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapWrite(addr, mapped, data)) {
            chr_memory[mapped.value] = (byte) data;
            return true;
        }
        return false;
    }

    /**
     * Return the mirroring mode of the Game
     *
     * @return the game's mirroring mode
     */
    public Mirror getMirror() {
        Mirror mirroring_mode = mapper.mirror();
        if (mirroring_mode == Mirror.HARDWARE)
            return mirror;
        return mirroring_mode;
    }

    /**
     * Reset the Mapper if it has processing capabilities
     */
    public void reset() {
        if (mapper != null)
            mapper.reset();
    }

    /**
     * Return the Mapper used by the Cartridge
     *
     * @return the current Mapper
     */
    public Mapper getMapper() {
        return mapper;
    }

    /**
     * Save the current Cartridge RAM to a file (filename.sav)
     */
    public void save() {
        if (mapper.hasRAM()) {
            try {
                Files.write(Paths.get(filename + ".sav"), mapper.getRAM(), new StandardOpenOption[]{StandardOpenOption.CREATE});
            } catch (IOException e) {
                if (Platform.isAccessibilityActive())
                    Dialogs.showException("ROM Save Error", "An error occur during ROM Saving", e);
                else
                    JOptionPane.showMessageDialog(null, "An Error occur while saving\n" + e.getMessage(), "ROM Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

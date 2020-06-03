package core.cartridge;

import core.cartridge.mappers.*;
import core.ppu.Mirror;
import exceptions.InvalidFileException;
import exceptions.UnsupportedMapperException;
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

    private int nPRGBanks;
    private int nCHRBanks;
    private byte[] sPRGMemory;
    private byte[] sCHRMemory;

    private Mapper mapper;
    private Mirror mirror;

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
        int mapperId = ((header.mapper2 >> 4) << 4) | header.mapper1 >> 4;
        mirror = (header.mapper1 & 0x01) == 0x01 ? Mirror.VERTICAL : Mirror.HORIZONTAL;
        System.out.println("Mapper " + mapperId);
        System.out.println("Mirroring mode : " + mirror.name());
        //Discard padding if necessary
        if ((header.mapper1 & 0x04) == 0x04)
            reader.readBytes(512);

        if ((header.mapper2 & 0x0C) == 0x08)
            fileType = 2;

        //If it's a iNES 1 file
        if (fileType == 1) {
            //Read Program Memory
            nPRGBanks = header.prg_rom_chunks;
            sPRGMemory = reader.readBytes(nPRGBanks * 16384);
            //Read Character Memory
            nCHRBanks = header.chr_rom_chunks;
            sCHRMemory = reader.readBytes(nCHRBanks * 8192);
            if (nCHRBanks == 0)
                sCHRMemory = new byte[8192];
        }
        if (fileType == 2) {
            //Read Program Memory
            nPRGBanks = (header.prg_ram_size & 0x07) << 8 | (header.prg_rom_chunks & 0xFF);
            sPRGMemory = reader.readBytes(nPRGBanks * 16384);
            //Read Character Memory
            nCHRBanks = (header.prg_ram_size & 0x38) << 8 | (header.chr_rom_chunks & 0xFF);
            sCHRMemory = reader.readBytes(nCHRBanks * 8192);
            if (nCHRBanks == 0)
                sCHRMemory = reader.readBytes(8192);
        }
        //Initialize the right Mapper
        switch (mapperId) {
            case 0:
                mapper = new Mapper000(nPRGBanks, nCHRBanks);
                break;
            case 1:
                mapper = new Mapper001(nPRGBanks, nCHRBanks, filename + ".sav");
                break;
            case 2:
                mapper = new Mapper002(nPRGBanks, nCHRBanks);
                break;
            case 3:
                mapper = new Mapper003(nPRGBanks, nCHRBanks);
                break;
            case 4:
                mapper = new Mapper004(nPRGBanks, nCHRBanks, filename + ".sav");
                break;
            case 66:
                mapper = new Mapper066(nPRGBanks, nCHRBanks);
                break;
            default:
                throw new UnsupportedMapperException("Mapper " + mapperId + " not implemented yet");
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
        addr &= 0xFFFF;
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapRead(addr, mapped, data)) {
            if (mapped.value == -1) return true;
            data.value = sPRGMemory[mapped.value] & 0xFF;
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
        addr &= 0xFFFF;
        data &= 0xFF;
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapWrite(addr, mapped, data)) {
            if (mapped.value == -1) return true;
            sPRGMemory[mapped.value] = (byte) data;
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
            data.value = sCHRMemory[mapped.value] & 0xFF;
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
    public boolean ppuWrite(int addr, int  data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapWrite(addr, mapped, data)) {
            sCHRMemory[mapped.value] = (byte) data;
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
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error saving game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

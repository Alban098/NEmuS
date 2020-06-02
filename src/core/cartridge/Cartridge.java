package core.cartridge;

import core.ppu.Mirror;
import exceptions.UnsupportedMapperException;
import utils.FileReader;
import utils.IntegerWrapper;

import java.io.IOException;

/**
 * This class represent a physical Cartridge
 */
public class Cartridge {

    private int nPRGBanks;
    private int nCHRBanks;
    private int[] sPRGMemory;
    private int[] sCHRMemory;

    private Mapper mapper;
    private Mirror mirror;

    /**
     * Create a Cartridge and load a ROM into the emulator
     *
     * @param filename the path to the ROM
     */
    public Cartridge(String filename) throws IOException, UnsupportedMapperException {
        int fileType = 1;

        //Initialize the file reader
        FileReader reader = new FileReader(filename);
        //Read the Header
        Header header = new Header(reader);
        //Extract the Mapper ID and Mirroring mode
        int mapperId = ((header.mapper2 >> 4) << 4) | header.mapper1 >> 4;
        mirror = (header.mapper1 & 0x01) == 0x01 ? Mirror.VERTICAL : Mirror.HORIZONTAL;
        System.out.println("mapper " + mapperId);
        //Discard padding if necessary
        if ((header.mapper1 & 0x04) == 0x04)
            reader.readBytes(512);

        if ((header.mapper2 & 0x0C) == 0x08)
            fileType = 2;

        //If it's a iNES 1 file
        if (fileType == 1) {
            //Read Program Memory
            nPRGBanks = header.prg_rom_chunks;
            sPRGMemory = reader.readBytesI(nPRGBanks * 16384);
            //Read Character Memory
            nCHRBanks = header.chr_rom_chunks;
            sCHRMemory = reader.readBytesI(nCHRBanks * 8192);
            if (nCHRBanks == 0)
                sCHRMemory = new int[8192];
            //Initialize the right Mapper
        }
        if (fileType == 2) {
            //Read Program Memory
            nPRGBanks = (header.prg_ram_size & 0x07) << 8 | (header.prg_rom_chunks & 0xFF);
            sPRGMemory = reader.readBytesI(nPRGBanks * 16384);
            //Read Character Memory
            nCHRBanks = (header.prg_ram_size & 0x38) << 8 | (header.chr_rom_chunks & 0xFF);
            sCHRMemory = reader.readBytesI(nCHRBanks * 8192);
            if (nCHRBanks == 0)
                sCHRMemory = new int[8192];
            //Initialize the right Mapper
        }
        switch (mapperId) {
            case 0:
                mapper = new Mapper000(nPRGBanks, nCHRBanks);
                break;
            case 1:
                mapper = new Mapper001(nPRGBanks, nCHRBanks);
                break;
            case 2:
                mapper = new Mapper002(nPRGBanks, nCHRBanks);
                break;
            case 3:
                mapper = new Mapper003(nPRGBanks, nCHRBanks);
                break;
            case 4:
                mapper = new Mapper004(nPRGBanks, nCHRBanks);
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
            sPRGMemory[mapped.value] = data;
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
            sCHRMemory[mapped.value] = data;
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

    public Mapper getMapper() {
        return mapper;
    }
}

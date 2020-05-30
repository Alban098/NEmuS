package core.cartridge;

import core.ppu.Mirror;
import utils.FileReader;
import utils.IntegerWrapper;

/**
 * This class represent a physical Cartridge
 */
public class Cartridge {

    private int[] sPRGMemory;
    private int[] sCHRMemory;

    private Mapper mapper;
    private Mirror mirror;

    /**
     * Create a Cartridge and load a ROM into the emulator
     * @param filename the path to the ROM
     */
    public Cartridge(String filename)  {
        int fileType = 1;

        //Initialize the file reader
        FileReader reader = new FileReader(filename);
        //Read the Header
        Header header = new Header(reader);
        //Extract the Mapper ID and Mirroring mode
        int mapperId = ((header.mapper2 >> 4) << 4) | header.mapper1 >> 4;
        mirror = (header.mapper1 & 0x01) == 0x01 ? Mirror.VERTICAL : Mirror.HORIZONTAL;
        //Discard padding if necessary
        if ((header.mapper1 & 0x04) == 0x04)
            reader.readBytes(512);

        //If it's a iNES 1 file
        if (fileType == 1) {
            //Read Program Memory
            int nPRGBanks = header.prg_rom_chunks;
            sPRGMemory = reader.readBytes(nPRGBanks * 16384);
            //Read Character Memory
            int nCHRBanks = header.chr_rom_chunks;
            sCHRMemory = reader.readBytes(nCHRBanks * 8192);

            //Initialize the right Mapper
            switch (mapperId) {
                case 0:
                    mapper = new Mapper000(nPRGBanks, nCHRBanks);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Will read a value from Program Memory if the Mapper allow it
     * @param addr the address to read from
     * @param data the Wrapper where to store the read data
     * @return was the data searched in the Cartridge
     */
    public boolean cpuRead(int addr, IntegerWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapRead(addr, mapped)) {
            data.value = sPRGMemory[mapped.value] & 0x00FF;
            return true;
        }
        return false;
    }

    /**
     * Will write the data into Program Memory if the Mapper allows it
     * and return whether or not the data was for the Cartridge
     * @param addr the address to write
     * @param data the data to write
     * @return was the data for the Cartridge
     */
    public boolean cpuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapWrite(addr, mapped)) {
             sPRGMemory[mapped.value] = data & 0x00FF;
            return true;
        }
        return false;
    }

    /**
     * Will read a value from Character Memory if the Mapper allow it
     * @param addr the address to read from
     * @param data the Wrapper where to store the read data
     * @return was the data searched in the Cartridge
     */
    public boolean ppuRead(int addr, IntegerWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapRead(addr, mapped)) {
            data.value = sCHRMemory[mapped.value] & 0x00FF;
            return true;
        }
        return false;
    }

    /**
     * Will write the data into Character Memory if the Mapper allows it
     * and return whether or not the data was for the Cartridge
     * @param addr the address to write
     * @param data the data to write
     * @return was the data for the Cartridge
     */
    public boolean ppuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapWrite(addr, mapped)) {
            sCHRMemory[mapped.value] = data & 0x00FF;
            return true;
        }
        return false;
    }

    /**
     * Return the mirroring mode of the Game
     * @return the game's mirroring mode
     */
    public Mirror getMirror() {
        return mirror;
    }
}

/**
 * This class represent the header of a iNES file
 */
class Header {

    char[] name;
    int prg_rom_chunks;
    int chr_rom_chunks;
    int mapper1;
    int mapper2;
    int prg_ram_size;
    int tv_system1;
    int tv_system2;

    /**
     * Load the header from the FileReader
     * after the FileReader is ready to read useful data
     * @param reader the FileReader of the iNES file
     */
    public Header(FileReader reader) {
        name = new char[] {(char)reader.nextByte(), (char)reader.nextByte(), (char)reader.nextByte(), (char)reader.nextByte()};
        prg_rom_chunks = (reader.nextByte() & 0x00FF);
        chr_rom_chunks = (reader.nextByte() & 0x00FF);
        mapper1 = (reader.nextByte() & 0x00FF);
        mapper2 = (reader.nextByte() & 0x00FF);
        prg_ram_size = (reader.nextByte() & 0x00FF);
        tv_system1 = (reader.nextByte() & 0x00FF);
        tv_system2 = (reader.nextByte() & 0x00FF);
        reader.readBytes(5);
    }
}

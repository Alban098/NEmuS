package core.cartridge;

import core.ppu.Mirror;
import utils.ByteWrapper;
import utils.FileReader;
import utils.IntegerWrapper;

import javax.swing.*;
import java.io.IOException;

/**
 * This class represent a physical Cartridge
 */
public class Cartridge {

    private byte[] sPRGMemory;
    private byte[] sCHRMemory;

    private Mapper mapper;
    private Mirror mirror;

    /**
     * Create a Cartridge and load a ROM into the emulator
     * @param filename the path to the ROM
     */
    public Cartridge(String filename) throws IOException {
        int fileType = 1;

        //Initialize the file reader
        FileReader reader = new FileReader(filename);
        //Read the Header
        Header header = new Header(reader);
        //Extract the Mapper ID and Mirroring mode
        byte mapperId = (byte) (((header.mapper2 >> 4) << 4) | header.mapper1 >> 4);
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
                case 1:
                    JOptionPane.showMessageDialog(null, "Mapper not implemented yet");
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
    public boolean cpuRead(int addr, ByteWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapRead(addr, mapped)) {
            data.value = (byte) (sPRGMemory[mapped.value] & 0xFF);
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
             sPRGMemory[mapped.value] = (byte) (data & 0xFF);
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
    public boolean ppuRead(int addr, ByteWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapRead(addr, mapped)) {
            data.value = (byte) (sCHRMemory[mapped.value] & 0xFF);
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
            sCHRMemory[mapped.value] = (byte) (data & 0xFF);
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

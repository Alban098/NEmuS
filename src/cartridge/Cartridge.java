package cartridge;

import graphics.Mirror;
import utils.FileReader;
import utils.IntegerWrapper;

import java.io.IOException;

public class Cartridge {

    private int[] sPRGMemory;
    private int[] sCHRMemory;

    private Mapper mapper;

    private int mapperId;
    private int nPRGBanks;
    private int nCHRBanks;

    private Mirror mirror;

    public Cartridge(String filename) throws IOException {
        int fileType = 1;

        FileReader reader = new FileReader(filename);
        Header header = new Header(reader);
        mapperId = ((header.mapper2 >> 4) << 4) | header.mapper1 >> 4;
        mirror = (header.mapper1 & 0x01) == 0x01 ? Mirror.VERTICAL : Mirror.HORIZONTAL;
        if ((header.mapper1 & 0x04) == 0x04)
            reader.readBytes(512);

        if (fileType == 1) {
            nPRGBanks = header.prg_rom_chunks;
            sPRGMemory = reader.readBytes(nPRGBanks * 16384);

            nCHRBanks = header.chr_rom_chunks;
            sCHRMemory = reader.readBytes(nCHRBanks * 8192);

            switch (mapperId) {
                case 0:
                    mapper = new Mapper000(nPRGBanks, nCHRBanks);
                    break;
                default:
                    break;
            }
        }

    }

    public boolean cpuRead(int addr, IntegerWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapRead(addr, mapped)) {
            data.value = sPRGMemory[mapped.value] & 0x00FF;
            return true;
        }
        return false;
    }

    public boolean cpuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.cpuMapWrite(addr, mapped)) {
             sPRGMemory[mapped.value] = data & 0x00FF;
            return true;
        }
        return false;
    }

    public boolean ppuRead(int addr, IntegerWrapper data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapRead(addr, mapped)) {
            data.value = sCHRMemory[mapped.value] & 0x00FF;
            return true;
        }
        return false;
    }

    public boolean ppuWrite(int addr, int data) {
        IntegerWrapper mapped = new IntegerWrapper();
        if (mapper.ppuMapWrite(addr, mapped)) {
            sCHRMemory[mapped.value] = data & 0x00FF;
            return true;
        }
        return false;
    }

    public Mirror getMirror() {
        return mirror;
    }
}

class Header {
    char[] name;
    int prg_rom_chunks;
    int chr_rom_chunks;
    int mapper1;
    int mapper2;
    int prg_ram_size;
    int tv_system1;
    int tv_system2;

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

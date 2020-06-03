package core.cartridge;

import exceptions.InvalidFileException;
import utils.FileReader;

import java.io.EOFException;
import java.io.IOException;

/**
 * This class represent the header of a iNES file
 */
class Header {

    final int prg_rom_chunks;
    final int chr_rom_chunks;
    final int mapper1;
    final int mapper2;
    final char[] name;
    final int prg_ram_size;
    final int tv_system1;
    final int tv_system2;

    /**
     * Load the header from the FileReader
     * after the FileReader is ready to read useful data
     *
     * @param reader the FileReader of the iNES file
     */
    public Header(FileReader reader) throws InvalidFileException {
        try {
            name = new char[]{(char) reader.nextByte(), (char) reader.nextByte(), (char) reader.nextByte(), (char) reader.nextByte()};
            prg_rom_chunks = reader.nextByte();
            chr_rom_chunks = reader.nextByte();
            mapper1 = reader.nextByte();
            mapper2 = reader.nextByte();
            prg_ram_size = reader.nextByte();
            tv_system1 = reader.nextByte();
            tv_system2 = reader.nextByte();
            reader.readBytes(5);
            if (name[0] == 'N' && name[1] == 'E' && name[2] == 'S' && name[3] == 'M')
                throw new InvalidFileException("NSF file not supported");
            else if (name[3] != 0x1A)
                throw new InvalidFileException("Not a valid iNES 1.0 File");
        } catch (EOFException e) {
            throw new InvalidFileException("Invalid file size (incomplete header)");
        }
    }
}
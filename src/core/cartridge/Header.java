package core.cartridge;

import exceptions.InvalidFileException;
import utils.FileReader;

import java.io.EOFException;

/**
 * This class represent the header of a iNES file
 */
class Header {

    final int prg_rom_chunks;
    final int chr_rom_chunks;
    final int flag_6;
    final int flag_7;
    final int rom_msb;

    /**
     * Load the header from the FileReader
     * after the FileReader is ready to read useful data
     *
     * @param reader the FileReader of the iNES file
     */
    Header(FileReader reader) throws InvalidFileException {
        try {
            char[] name = new char[]{(char) reader.nextByte(), (char) reader.nextByte(), (char) reader.nextByte(), (char) reader.nextByte()};
            prg_rom_chunks = reader.nextByte() & 0xFF;
            chr_rom_chunks = reader.nextByte() & 0xFF;
            flag_6 = reader.nextByte() & 0xFF;
            flag_7 = reader.nextByte() & 0xFF;
            reader.nextByte();
            rom_msb = reader.nextByte() & 0xFF;
            reader.readBytes(6);
            if (name[0] == 'N' && name[1] == 'E' && name[2] == 'S' && name[3] == 'M')
                throw new InvalidFileException("NSF file not supported");
            else if (name[3] != 0x1A)
                throw new InvalidFileException("Not a valid iNES File");
        } catch (EOFException e) {
            throw new InvalidFileException("Invalid file size (incomplete header)");
        }
    }
}
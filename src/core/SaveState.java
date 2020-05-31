package core;

import exceptions.DumpException;
import exceptions.InvalidFileException;
import utils.FileReader;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This class represent a Savestate
 * that can be created from the current Emulator state or loaded from a previously saved file
 */
public class SaveState {

    //TODO add Header information and Mapper support
    private byte[] cpu;
    private byte[] ppu;
    private byte[] ram;
    private byte[] vram;
    private byte[] mapperState;
    private byte[] mapperVRAM;

    /**
     * Create a Savestate from the current Emulator state
     *
     * @param nes the Bus to dump
     */
    public SaveState(Bus nes) {
        cpu = nes.dumpCPU();
        ppu = nes.dumpPPU();
        ram = nes.dumpRAM();
        vram = nes.dumpVRAM();
    }

    /**
     * Load a Savestate from a file
     *
     * @param file the file to load
     * @throws InvalidFileException If the file isn't exactly 12594 bytes
     */
    public SaveState(String file) throws InvalidFileException {
        FileReader reader = new FileReader(file);
        try {
            cpu = reader.readBytes(7);
            ppu = reader.readBytes(11);
            ram = reader.readBytes(2048);
            vram = reader.readBytes(10528);
            if (reader.hasDataLeft())
                throw new InvalidFileException("Invalid file size (" + reader.getFileSize() + ") must be 12594 bytes");
        } catch (EOFException e) {
            throw new InvalidFileException("Invalid file size (" + reader.getFileSize() + ") must be 12594 bytes");
        }
    }

    /**
     * Restore the state of the Emulator to the dumped one
     *
     * @param nes the Emulator to set the state of
     */
    public void restore(Bus nes) {
        try {
            nes.restoreCPUDump(cpu);
            nes.restorePPUDump(ppu);
            nes.restoreRAMDump(ram);
            nes.restoreVRAMDump(vram);
        } catch (DumpException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading Savestate", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save the Savestate into a file as a stream of bytes
     *
     * @param file the file to save to
     */
    public void saveToFile(String file) {
        byte[] fileAsBytes = new byte[12594];
        System.arraycopy(cpu, 0, fileAsBytes, 0, 7);
        System.arraycopy(ppu, 0, fileAsBytes, 7, 11);
        System.arraycopy(ram, 0, fileAsBytes, 18, 2048);
        System.arraycopy(vram, 0, fileAsBytes, 2066, 10528);
        try {
            Files.write(Paths.get(file), fileAsBytes, new StandardOpenOption[]{StandardOpenOption.CREATE});
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), " Error writing savestate", JOptionPane.ERROR_MESSAGE);
        }
    }
}

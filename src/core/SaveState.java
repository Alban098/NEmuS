package core;

import utils.FileReader;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SaveState {

    private final byte[] cpu;
    private final byte[] ppu;
    private final byte[] ram;
    private final byte[] vram;

    public SaveState(Bus nes) {
        cpu = nes.dumpCPU();
        ppu = nes.dumpPPU();
        ram = nes.dumpRAM();
        vram = nes.dumpVRAM();
    }

    public SaveState(String file) {
        FileReader reader = new FileReader(file);
        cpu = reader.readBytes(7);
        ppu = reader.readBytes(11);
        ram = reader.readBytes(2048);
        vram = reader.readBytes(10528);
    }

    public void restore(Bus nes) {
        nes.restoreCPUDump(cpu);
        nes.restorePPUDump(ppu);
        nes.restoreRAMDump(ram);
        nes.restoreVRAMDump(vram);
    }

    public void saveToFile(String file) {
        byte[] fileAsBytes = new byte[12594];
        System.arraycopy(cpu, 0, fileAsBytes, 0, 7);
        System.arraycopy(ppu, 0, fileAsBytes, 7, 11);
        System.arraycopy(ram, 0, fileAsBytes, 18, 2048);
        System.arraycopy(vram, 0, fileAsBytes, 2066, 10528);
        try {
            Files.write(Paths.get(file), fileAsBytes, new StandardOpenOption[]{StandardOpenOption.CREATE});
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing the save state");
        }
    }
}

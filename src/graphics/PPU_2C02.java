package graphics;

import cartridge.Cartridge;
import utils.IntegerWrapper;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PPU_2C02 {

    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 240;

    private Color[] palScreen;
    private BufferedImage screen;
    private BufferedImage[] nameTable;
    private BufferedImage[] patternTable;

    public boolean frameComplete;

    private Cartridge cartridge;
    int[][] tblName;
    int[] tblPalette;

    private int scanline;
    private int cycle;

    public PPU_2C02() {
        tblName = new int[2][1024];
        tblPalette = new int[32];
        palScreen = new Color[0x40];
        screen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        nameTable = new BufferedImage[]{new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR), new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR)};
        patternTable = new BufferedImage[]{new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR), new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR)};
        frameComplete = false;
        scanline = 0;
        cycle = 0;

        palScreen[0x00] = new Color(84, 84, 84);
        palScreen[0x01] = new Color(0, 30, 116);
        palScreen[0x02] = new Color(8, 16, 144);
        palScreen[0x03] = new Color(48, 0, 136);
        palScreen[0x04] = new Color(68, 0, 100);
        palScreen[0x05] = new Color(92, 0, 48);
        palScreen[0x06] = new Color(84, 4, 0);
        palScreen[0x07] = new Color(60, 24, 0);
        palScreen[0x08] = new Color(32, 42, 0);
        palScreen[0x09] = new Color(8, 58, 0);
        palScreen[0x0A] = new Color(0, 64, 0);
        palScreen[0x0B] = new Color(0, 60, 0);
        palScreen[0x0C] = new Color(0, 50, 60);
        palScreen[0x0D] = new Color(0, 0, 0);
        palScreen[0x0E] = new Color(0, 0, 0);
        palScreen[0x0F] = new Color(0, 0, 0);
        palScreen[0x10] = new Color(152, 150, 152);
        palScreen[0x11] = new Color(8, 76, 196);
        palScreen[0x12] = new Color(48, 50, 236);
        palScreen[0x13] = new Color(92, 30, 228);
        palScreen[0x14] = new Color(136, 20, 176);
        palScreen[0x15] = new Color(160, 20, 100);
        palScreen[0x16] = new Color(152, 34, 32);
        palScreen[0x17] = new Color(120, 60, 0);
        palScreen[0x18] = new Color(84, 90, 0);
        palScreen[0x19] = new Color(40, 114, 0);
        palScreen[0x1A] = new Color(8, 124, 0);
        palScreen[0x1B] = new Color(0, 118, 40);
        palScreen[0x1C] = new Color(0, 102, 120);
        palScreen[0x1D] = new Color(0, 0, 0);
        palScreen[0x1E] = new Color(0, 0, 0);
        palScreen[0x1F] = new Color(0, 0, 0);
        palScreen[0x20] = new Color(236, 238, 236);
        palScreen[0x21] = new Color(76, 154, 236);
        palScreen[0x22] = new Color(120, 124, 236);
        palScreen[0x23] = new Color(176, 98, 236);
        palScreen[0x24] = new Color(228, 84, 236);
        palScreen[0x25] = new Color(236, 88, 180);
        palScreen[0x26] = new Color(236, 106, 100);
        palScreen[0x27] = new Color(212, 136, 32);
        palScreen[0x28] = new Color(160, 170, 0);
        palScreen[0x29] = new Color(116, 196, 0);
        palScreen[0x2A] = new Color(76, 208, 32);
        palScreen[0x2B] = new Color(56, 204, 108);
        palScreen[0x2C] = new Color(56, 180, 204);
        palScreen[0x2D] = new Color(60, 60, 60);
        palScreen[0x2E] = new Color(0, 0, 0);
        palScreen[0x2F] = new Color(0, 0, 0);
        palScreen[0x30] = new Color(236, 238, 236);
        palScreen[0x31] = new Color(168, 204, 236);
        palScreen[0x32] = new Color(188, 188, 236);
        palScreen[0x33] = new Color(212, 178, 236);
        palScreen[0x34] = new Color(236, 174, 236);
        palScreen[0x35] = new Color(236, 174, 212);
        palScreen[0x36] = new Color(236, 180, 176);
        palScreen[0x37] = new Color(228, 196, 144);
        palScreen[0x38] = new Color(204, 210, 120);
        palScreen[0x39] = new Color(180, 222, 120);
        palScreen[0x3A] = new Color(168, 226, 144);
        palScreen[0x3B] = new Color(152, 226, 180);
        palScreen[0x3C] = new Color(160, 214, 228);
        palScreen[0x3D] = new Color(160, 162, 160);
        palScreen[0x3E] = new Color(0, 0, 0);
        palScreen[0x3F] = new Color(0, 0, 0);
    }

    public int cpuRead(int addr) {
        return cpuRead(addr, false);
    }

    public int cpuRead(int addr, boolean readOnly) {
        int data = 0x00;

        switch(addr) {
            case 0x0000: // Control
                break;
            case 0x0001: // Mask
                break;
            case 0x0002: // Status
                break;
            case 0x0003: // OAM Address
                break;
            case 0x0004: // OAM Data
                break;
            case 0x0005: // Scroll
                break;
            case 0x0006: // PPU Address
                break;
            case 0x0007: // PPU Data
                break;
        }
        return data;
    }

    public void cpuWrite(int addr, int data) {
        switch(addr) {
            case 0x0000: // Control
                break;
            case 0x0001: // Mask
                break;
            case 0x0002: // Status
                break;
            case 0x0003: // OAM Address
                break;
            case 0x0004: // OAM Data
                break;
            case 0x0005: // Scroll
                break;
            case 0x0006: // PPU Address
                break;
            case 0x0007: // PPU Data
                break;
        }
    }

    public int ppuRead(int addr) {
        return cpuRead(addr, false);
    }

    public int ppuRead(int addr, boolean readOnly) {
        addr &= 0x03FF;
        IntegerWrapper data = new IntegerWrapper();
        if (cartridge.ppuRead(addr, data)) {

        }
        return 0;
    }

    public void ppuWrite(int addr, int data) {
        addr &= 0x03FF;
        if (cartridge.ppuWrite(addr, data)) {

        }
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public void clock() {
        if (cycle > 0 && cycle < SCREEN_WIDTH && scanline >= 0 && scanline < SCREEN_HEIGHT) {
            screen.setRGB(cycle - 1, scanline, palScreen[(int) (Math.random() * palScreen.length)].getRGB());
        }
        cycle++;
        if (cycle >= 341) {
            cycle = 0;
            scanline++;
            if (scanline >= 261) {
                scanline = -1;
                frameComplete = true;
            }
        }
    }

    public BufferedImage getScreen() {
        return screen;
    }

    public BufferedImage getNameTable(int i) {
        return nameTable[i % 2];
    }

    public BufferedImage getPatternTable(int i) {
        return patternTable[i % 2];
    }

}

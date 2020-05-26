package graphics;

import cartridge.Cartridge;
import graphics.registers.ControlRegister;
import graphics.registers.MaskRegister;
import graphics.registers.StatusRegister;
import utils.IntegerWrapper;

import java.awt.*;

public class PPU_2C02 {

    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 240;

    private Color[] palScreen;
    private Sprite screen;
    private Sprite[] nameTable;
    private Sprite[] patternTable;

    public boolean frameComplete;

    private Cartridge cartridge;
    private int[][] tblName;
    private int[] tblPalette;
    private int[][] tblPattern;

    private MaskRegister maskRegister;
    private ControlRegister controlRegister;
    private StatusRegister statusRegister;

    private int address_latch = 0x00;
    private int ppu_data_buffer = 0x00;
    private int ppu_address = 0x0000;

    private int scanline;
    private int cycle;
    private boolean nmi;

    public PPU_2C02() {
        tblName = new int[2][1024];
        tblPattern = new int[2][4096];
        tblPalette = new int[32];
        palScreen = new Color[0x40];
        screen = new Sprite(SCREEN_WIDTH, SCREEN_HEIGHT);
        nameTable = new Sprite[]{new Sprite(SCREEN_WIDTH, SCREEN_HEIGHT), new Sprite(SCREEN_WIDTH, SCREEN_HEIGHT)};
        patternTable = new Sprite[]{new Sprite(128, 128), new Sprite(128, 128)};
        frameComplete = false;
        scanline = 0;
        cycle = 0;
        maskRegister = new MaskRegister();
        controlRegister = new ControlRegister();
        statusRegister = new StatusRegister();

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
                data = statusRegister.get() & 0xE0 | (ppu_data_buffer & 0x1F);
                statusRegister.setVertical_blank(false);
                address_latch = 0;
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
                data = ppu_data_buffer;
                ppu_data_buffer = ppuRead(ppu_address);
                if (ppu_address > 0x3F00) data = ppu_data_buffer;
                ppu_address++;
                break;
        }
        return data & 0x00FF;
    }

    public void cpuWrite(int addr, int data) {
        data &= 0x00FF;
        switch(addr) {
            case 0x0000: // Control
                controlRegister.set(data);
                break;
            case 0x0001: // Mask
                maskRegister.set(data);
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
                if (address_latch == 0) {
                    ppu_address = (ppu_address & 0x00FF) | ((data & 0x3F) << 8);
                    address_latch = 1;
                } else {
                    ppu_address = (ppu_address & 0xFF00) | data;
                    address_latch = 0;
                }
                break;
            case 0x0007: // PPU Data
                ppuWrite(ppu_address, data);
                ppu_address++;
                break;
        }
    }

    public int ppuRead(int addr) {
        return ppuRead(addr, false);
    }

    public int ppuRead(int addr, boolean readOnly) {
        addr &= 0x3FFF;
        IntegerWrapper data = new IntegerWrapper();
        if (cartridge.ppuRead(addr, data)) {

        } else if (addr >= 0x0000 && addr <= 0x1FFF) {
            data.value = tblPattern[(addr & 0x1000) >> 12][addr & 0x0FFF];
        } else if (addr >= 0x2000 && addr <= 0x3EFF) {

        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            addr &= 0x001F;
            if (addr == 0x0010) addr = 0x0000;
            if (addr == 0x0014) addr = 0x0004;
            if (addr == 0x0018) addr = 0x0008;
            if (addr == 0x001C) addr = 0x000C;
            data.value = tblPalette[addr];
        }
        return data.value & 0x00FF;
    }

    public void ppuWrite(int addr, int data) {
        addr &= 0x3FFF;
        data&= 0x00FF;
        if (cartridge.ppuWrite(addr, data)) {

        } else if (addr >= 0x0000 && addr <= 0x1FFF) {
            tblPattern[(addr & 0x1000) >> 12][addr & 0x0FFF] = data;
        } else if (addr >= 0x2000 && addr <= 0x3EFF) {

        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            addr &= 0x001F;
            if (addr == 0x0010) addr = 0x0000;
            if (addr == 0x0014) addr = 0x0004;
            if (addr == 0x0018) addr = 0x0008;
            if (addr == 0x001C) addr = 0x000C;
            tblPalette[addr] = data;
        }
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public void clock() {

        if (scanline == -1 && cycle == 1) {
            statusRegister.setVertical_blank(false);
        }
        if (scanline == 241 && cycle == 1) {
            statusRegister.setVertical_blank(true);
            if (controlRegister.isEnable_nmi())
                nmi = true;
        }

        screen.setPixel(cycle - 1, scanline, palScreen[(int) (Math.random() * palScreen.length)]);
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

    public Sprite getScreen() {
        return screen;
    }

    public Sprite getNameTable(int i) {
        return nameTable[i % 2];
    }

    public Sprite getPatternTable(int i, int paletteId) {
        for (int tileY = 0; tileY < 16; tileY++) {
            for (int tileX = 0; tileX < 16; tileX++) {
                int offset = tileY * 256 + tileX * 16;
                for (int row = 0; row < 8; row++) {
                    int tile_lsb = ppuRead(i * 0x1000 + offset + row);
                    int tile_msb = ppuRead(i * 0x1000 + offset + row + 8);
                    for (int col = 0; col < 8; col++) {
                        int pixel = (tile_lsb & 0x01) + (tile_msb & 0x01);
                        tile_lsb >>= 1;
                        tile_msb >>= 1;
                        patternTable[i].setPixel(tileX * 8 + (7 - col), tileY * 8 + row, getColorFromPalette(paletteId, pixel));
                    }
                }
            }
        }
        return patternTable[i];
    }

    public Color getColorFromPalette(int paletteId, int pixel) {
        return palScreen[ppuRead(0x3F00 + (paletteId << 2) + pixel)];
    }

    public boolean nmi() {
        if (nmi) {
            nmi = false;
            return true;
        }
        return false;
    }

}

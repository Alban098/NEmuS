package graphics;

import cartridge.Cartridge;
import graphics.registers.*;
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
    public int[][] tblName;
    int[] tblPalette;
    int[][] tblPattern;

    private MaskRegister maskRegister;
    private ControlRegister controlRegister;
    private StatusRegister statusRegister;
    private ScrollRegister scrollRegister;
    private ObjectAttribute[] oams;

    private int address_latch = 0x00;
    private int ppu_data_buffer = 0x00;
    private int oam_addr = 0x00;

    private LoopyRegister vram_addr;
    private LoopyRegister tram_addr;
    private int fine_x = 0x00;

    private int bg_next_tile_id = 0x00;
    private int bg_next_tile_attrib = 0x00;
    private int bg_next_tile_lsb = 0x00;
    private int bg_next_tile_msb = 0x00;

    private int bg_shift_pattern_low = 0x0000;
    private int bg_shift_pattern_high = 0x0000;
    private int bg_shift_attrib_low = 0x0000;
    private int bg_shift_attrib_high = 0x0000;

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
        vram_addr = new LoopyRegister();
        tram_addr = new LoopyRegister();
        oams = new ObjectAttribute[64];
        for (int i = 0; i < oams.length; i++)
            oams[i] = new ObjectAttribute();

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

        if (readOnly) {
            switch (addr)
            {
                case 0x0000: // Control
                    data = controlRegister.get();
                    break;
                case 0x0001: // Mask
                    data = maskRegister.get();
                    break;
                case 0x0002: // Status
                    data = statusRegister.get();
                    break;
                case 0x0003: // OAM Address
                    break;
                case 0x0004: // OAM Data
                    data = (oams[oam_addr >> 2].get() >> (oam_addr & 0x03)) & 0x00FF;
                    break;
                case 0x0005: // Scroll
                    break;
                case 0x0006: // PPU Address
                    break;
                case 0x0007: // PPU Data
                    break;
            }
        }

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
                data = (oams[oam_addr >> 2].get() >> (oam_addr & 0x03)) & 0x00FF;
                break;
            case 0x0005: // Scroll
                break;
            case 0x0006: // PPU Address
                break;
            case 0x0007: // PPU Data
                data = ppu_data_buffer;
                ppu_data_buffer = ppuRead(vram_addr.get());
                if (vram_addr.get() > 0x3F00) data = ppu_data_buffer;
                int vram = vram_addr.get() & 0xFFFF;
                vram_addr.set(vram + (controlRegister.isIncrement_mode() ? 32 : 1));
                break;
        }
        return data & 0x00FF;
    }

    public void cpuWrite(int addr, int data) {
        data &= 0x00FF;
        switch(addr) {
            case 0x0000: // Control
                controlRegister.set(data);
                tram_addr.setNametable_x(controlRegister.isNametable_x());
                tram_addr.setNametable_y(controlRegister.isNametable_y());
                break;
            case 0x0001: // Mask
                maskRegister.set(data);
                break;
            case 0x0002: // Status
                break;
            case 0x0003: // OAM Address
                oam_addr = data;
                break;
            case 0x0004: // OAM Data
                int oam = oams[oam_addr >> 2].get() & (~(0xFF000000 >> ((oam_addr & 0x03) << 3)));
                oams[oam_addr >> 2].set(oam | ((data << 24) >> ((oam_addr & 0x03) << 3)));
                break;
            case 0x0005: // Scroll
                if (address_latch == 0) {
                    fine_x = data & 0x07;
                    tram_addr.setCoarse_x(data >> 3);
                    address_latch = 1;
                } else {
                    tram_addr.setFine_y(data & 0x07);
                    tram_addr.setCoarse_y(data >> 3);
                    address_latch = 0;
                }
                break;
            case 0x0006: // PPU Address
                if (address_latch == 0) {
                    int tram = tram_addr.get() & 0xFFFF;
                    tram_addr.set((tram & 0x00FF) | ((data & 0x3F) << 8));
                    address_latch = 1;
                } else {
                    int tram = tram_addr.get() & 0xFFFF;
                    tram_addr.set((tram & 0xFF00) | data);
                    vram_addr.set(tram_addr.get());
                    address_latch = 0;
                }
                break;
            case 0x0007: // PPU Data
                ppuWrite(vram_addr.get(), data);
                int vram = vram_addr.get() & 0xFFFF;
                vram_addr.set(vram + (controlRegister.isIncrement_mode() ? 32 : 1));
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
            addr &= 0x0FFF;
            if (cartridge.getMirror() == Mirror.VERTICAL) {
                if (addr >= 0x0000 && addr <= 0x03FF)
                    data.value = tblName[0][addr & 0x03FF];
                if (addr >= 0x0400 && addr <= 0x07FF)
                    data.value = tblName[1][addr & 0x03FF];
                if (addr >= 0x0800 && addr <= 0x0BFF)
                    data.value = tblName[0][addr & 0x03FF];
                if (addr >= 0x0C00 && addr <= 0x0FFF)
                    data.value = tblName[1][addr & 0x03FF];
            } else if (cartridge.getMirror() == Mirror.HORIZONTAL) {
                if (addr >= 0x0000 && addr <= 0x03FF)
                    data.value = tblName[0][addr & 0x03FF];
                if (addr >= 0x0400 && addr <= 0x07FF)
                    data.value = tblName[0][addr & 0x03FF];
                if (addr >= 0x0800 && addr <= 0x0BFF)
                    data.value = tblName[1][addr & 0x03FF];
                if (addr >= 0x0C00 && addr <= 0x0FFF)
                    data.value = tblName[1][addr & 0x03FF];
            }
        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            addr &= 0x001F;
            if (addr == 0x0010) addr = 0x0000;
            if (addr == 0x0014) addr = 0x0004;
            if (addr == 0x0018) addr = 0x0008;
            if (addr == 0x001C) addr = 0x000C;
            data.value = tblPalette[addr] ;//& (maskRegister.isGrayscale() ? 0x30 : 0x3F);
        }
        return data.value & 0x00FF;
    }

    public void ppuWrite(int addr, int data) {
        addr &= 0x3FFF;
        data &= 0x00FF;
        if (cartridge.ppuWrite(addr, data)) {

        } else if (addr >= 0x0000 && addr <= 0x1FFF) {
            tblPattern[(addr & 0x1000) >> 12][addr & 0x0FFF] = data;
        } else if (addr >= 0x2000 && addr <= 0x3EFF) {
            addr &= 0x0FFF;
            if (cartridge.getMirror() == Mirror.VERTICAL) {
                if (addr >= 0x0000 && addr <= 0x03FF)
                    tblName[0][addr & 0x03FF] = data;
                if (addr >= 0x0400 && addr <= 0x07FF)
                    tblName[1][addr & 0x03FF] = data;
                if (addr >= 0x0800 && addr <= 0x0BFF)
                    tblName[0][addr & 0x03FF] = data;
                if (addr >= 0x0C00 && addr <= 0x0FFF)
                    tblName[1][addr & 0x03FF] = data;
            } else if (cartridge.getMirror() == Mirror.HORIZONTAL) {
                if (addr >= 0x0000 && addr <= 0x03FF)
                    tblName[0][addr & 0x03FF] = data;
                if (addr >= 0x0400 && addr <= 0x07FF)
                    tblName[0][addr & 0x03FF] = data;
                if (addr >= 0x0800 && addr <= 0x0BFF)
                    tblName[1][addr & 0x03FF] = data;
                if (addr >= 0x0C00 && addr <= 0x0FFF)
                    tblName[1][addr & 0x03FF] = data;
            }
        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            addr &= 0x001F;
            if (addr == 0x0010) addr = 0x0000;
            if (addr == 0x0014) addr = 0x0004;
            if (addr == 0x0018) addr = 0x0008;
            if (addr == 0x001C) addr = 0x000C;
            tblPalette[addr] = data;
        }
    }

    public void reset() {
        fine_x = 0x00;
        address_latch = 0x00;
        ppu_data_buffer = 0x00;
        scanline = 0;
        cycle = 0;
        bg_next_tile_id = 0x00;
        bg_next_tile_attrib = 0x00;
        bg_next_tile_lsb = 0x00;
        bg_next_tile_msb = 0x00;
        bg_shift_pattern_low = 0x0000;
        bg_shift_pattern_high = 0x0000;
        bg_shift_attrib_low = 0x0000;
        bg_shift_attrib_high = 0x0000;
        statusRegister.set(0x00);
        maskRegister.set(0x00);
        controlRegister.set(0x00);
        vram_addr.set(0x0000);
        tram_addr.set(0x0000);
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public void clock() {

        Runnable incrementScrollX = () -> {
            if (maskRegister.isRender_background() || maskRegister.isRender_sprites()) {
                if (vram_addr.getCoarse_x() == 31) {
                    vram_addr.setCoarse_x(0);
                    vram_addr.setNametable_x(!vram_addr.isNametable_x());
                } else {
                    vram_addr.setCoarse_x((vram_addr.getCoarse_x() + 1) & 0x001F);
                }
            }
        };
        Runnable incrementScrollY = () -> {
            if (maskRegister.isRender_background() || maskRegister.isRender_sprites()) {
                if (vram_addr.getFine_y() < 7) {
                    vram_addr.setFine_y((vram_addr.getFine_y() + 1) & 0x000F);
                } else {
                    vram_addr.setFine_y(0);
                    if (vram_addr.getCoarse_y() == 29) {
                        vram_addr.setCoarse_y(0);
                        vram_addr.setNametable_y(!vram_addr.isNametable_y());
                    } else if (vram_addr.getCoarse_y() == 31) {
                        vram_addr.setCoarse_y(0);
                    } else {
                        vram_addr.setCoarse_y((vram_addr.getCoarse_y() + 1) & 0x001F);
                    }
                }
            }
        };
        Runnable transferAddressX = () -> {
            if (maskRegister.isRender_background() || maskRegister.isRender_sprites()) {
                vram_addr.setNametable_x(tram_addr.isNametable_x());
                vram_addr.setCoarse_x(tram_addr.getCoarse_x() & 0x001F);
            }
        };
        Runnable transferAddressY = () -> {
            if (maskRegister.isRender_background() || maskRegister.isRender_sprites()) {
                vram_addr.setNametable_y(tram_addr.isNametable_y());
                vram_addr.setCoarse_y(tram_addr.getCoarse_y() & 0x00FF);
                vram_addr.setFine_y(tram_addr.getFine_y() & 0x000F);
            }
        };
        Runnable loadBackgroundShifter = () -> {
            bg_shift_pattern_low = ((bg_shift_pattern_low & 0xFF00) | bg_next_tile_lsb) & 0xFFFF;
            bg_shift_pattern_high = ((bg_shift_pattern_high & 0xFF00) | bg_next_tile_msb) & 0xFFFF;
            bg_shift_attrib_low = ((bg_shift_attrib_low & 0xFF00) | (((bg_next_tile_attrib & 0b01) == 0b01) ? 0xFF : 0x00)) & 0xFFFF;
            bg_shift_attrib_high = ((bg_shift_attrib_high & 0xFF00) | (((bg_next_tile_attrib & 0b10) == 0b10) ? 0xFF : 0x00)) & 0xFFFF;
        };
        Runnable updateShifter = () -> {
            if (maskRegister.isRender_background()) {
                bg_shift_pattern_low = (bg_shift_pattern_low << 1) & 0xFFFF;
                bg_shift_pattern_high = (bg_shift_pattern_high << 1) & 0xFFFF;
                bg_shift_attrib_low = (bg_shift_attrib_low << 1) & 0xFFFF;
                bg_shift_attrib_high = (bg_shift_attrib_high << 1) & 0xFFFF;
            }
        };

        if (scanline >= -1 && scanline < 240) {
            if (scanline == 0 && cycle == 0) {
                cycle = 1;
            }
            if (scanline == -1 && cycle == 1) {
                statusRegister.setVertical_blank(false);
            }
            if ((cycle >= 2 && cycle < 258) || (cycle >= 321 && cycle < 338)) {
                updateShifter.run();
                switch ((cycle - 1) % 8) {
                    case 0:
                        loadBackgroundShifter.run();
                        bg_next_tile_id = ppuRead(0x2000 | (vram_addr.get() & 0x0FFF));
                        break;
                    case 2:
                        bg_next_tile_attrib = ppuRead(0x23C0 | (vram_addr.isNametable_y() ? 0x1 << 11 : 0x0) | (vram_addr.isNametable_x() ? 0x1 << 10 : 0x0) | ((vram_addr.getCoarse_y() >> 2) << 3) | (vram_addr.getCoarse_x() >> 2));
                        if ((vram_addr.getCoarse_y() & 0x02) == 0x02) bg_next_tile_attrib = (bg_next_tile_attrib >> 4) & 0x00FF;
                        if ((vram_addr.getCoarse_x() & 0x02) == 0x02) bg_next_tile_attrib = (bg_next_tile_attrib >> 2) & 0x00FF;;
                        bg_next_tile_attrib &= 0x03;
                        break;
                    case 4:
                        bg_next_tile_lsb = ppuRead((controlRegister.isPattern_background() ? 0x1 << 12 : 0) + (bg_next_tile_id << 4) + vram_addr.getFine_y()) & 0x00FF;
                        break;
                    case 6:
                        bg_next_tile_msb = ppuRead((controlRegister.isPattern_background() ? 0x1 << 12 : 0) + (bg_next_tile_id << 4) + vram_addr.getFine_y() + 8) & 0x00FF;
                        break;
                    case 7:
                        incrementScrollX.run();
                        break;
                }
            }
            if (cycle == 256) {
                incrementScrollY.run();
            }
            if (cycle == 257) {
                loadBackgroundShifter.run();
                transferAddressX.run();
            }
            if (cycle == 338 || cycle == 340)
            {
                bg_next_tile_id = ppuRead(0x2000 | (vram_addr.get() & 0x0FFF));
            }
            if (scanline == -1 && cycle >= 280 && cycle < 305) {
                transferAddressY.run();
            }
        }

        if (scanline == 240) {

        }

        if (scanline == 241 && cycle == 1) {
            statusRegister.setVertical_blank(true);
            if (controlRegister.isEnable_nmi())
                nmi = true;
        }

        int bg_pixel = 0x00;
        int bg_palette = 0x00;

        if (maskRegister.isRender_background()) {
            int bit_mux = (0x8000 >> fine_x) & 0xFFFF;

            int p0_pixel = (bg_shift_pattern_low & bit_mux) > 0 ? 0x1 : 0x0;
            int p1_pixel = (bg_shift_pattern_high & bit_mux) > 0 ? 0x1 : 0x0;
            bg_pixel = ((p1_pixel << 1) | p0_pixel) & 0x000F;

            int bg_pal0 = (bg_shift_attrib_low & bit_mux) > 0 ? 0x1 : 0x0;
            int bg_pal1 = (bg_shift_attrib_high & bit_mux) > 0 ? 0x1 : 0x0;
            bg_palette = ((bg_pal1 << 1) | bg_pal0) & 0x000F;
        }

        screen.setPixel(cycle - 1, scanline, getColorFromPalette(bg_palette, bg_pixel));

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
                    int tile_lsb = ppuRead(i * 0x1000 + offset + row) & 0x00FF;
                    int tile_msb = ppuRead(i * 0x1000 + offset + row + 8) & 0x00FF;
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
        return palScreen[ppuRead(0x3F00 + ((paletteId << 2) & 0x00FF) + (pixel & 0x00FF))];
    }

    public boolean nmi() {
        if (nmi) {
            nmi = false;
            return true;
        }
        return false;
    }

}

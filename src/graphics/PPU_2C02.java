package graphics;

import cartridge.Cartridge;
import graphics.registers.*;
import utils.IntegerWrapper;
import utils.NumberUtils;

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

    public ObjectAttribute[] oams;
    private ObjectAttribute[] visible_oams;
    private int sprite_count;
    private int[] sprite_shift_pattern_low;
    private int[] sprite_shift_pattern_high;

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

    private boolean spriteZeroHitPossible = false;
    private boolean spriteZeroBeingRendered = false;

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
        visible_oams = new ObjectAttribute[8];
        for (int i = 0; i < visible_oams.length; i++)
            visible_oams[i] = new ObjectAttribute();
        sprite_shift_pattern_low = new int[8];
        sprite_shift_pattern_high = new int[8];

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
                    data = getOamData();
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
                data = getOamData();
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

    private int getOamData() {
        switch(oam_addr & 0x03) {
            case 0x0:
                return oams[oam_addr >> 2].getY() & 0x00FF;
            case 0x1:
                 return oams[oam_addr >> 2].getId() & 0x00FF;
            case 0x2:
                return oams[oam_addr >> 2].getAttribute() & 0x00FF;
            case 0x3:
                return oams[oam_addr >> 2].getX() & 0x00FF;
        }
        return 0x00;
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
                switch(oam_addr & 0x03) {
                    case 0x0:
                        oams[oam_addr >> 2].setY(data);
                    case 0x1:
                        oams[oam_addr >> 2].setId(data);
                    case 0x2:
                        oams[oam_addr >> 2].setAttribute(data);
                    case 0x3:
                        oams[oam_addr >> 2].setX(data);
                }
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
        if (cartridge.ppuRead(addr, data)) {}
        else if (addr >= 0x0000 && addr <= 0x1FFF) {
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
        if (cartridge.ppuWrite(addr, data)) {}
        else if (addr >= 0x0000 && addr <= 0x1FFF) {
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
            if (maskRegister.isRender_sprites() && cycle >= 1 && cycle < 258) {
                for (int i = 0; i < sprite_count; i++) {
                    if (visible_oams[i].getX() > 0)
                        visible_oams[i].setX(visible_oams[i].getX() - 1);
                    else {
                        sprite_shift_pattern_low[i] = (sprite_shift_pattern_low[i] << 1) & 0x00FF;
                        sprite_shift_pattern_high[i] = (sprite_shift_pattern_high[i] << 1) & 0x00FF;
                    }
                }
            }
        };

        if (scanline >= -1 && scanline < 240) {
            if (scanline == 0 && cycle == 0) {
                cycle = 1;
            }
            if (scanline == -1 && cycle == 1) {
                statusRegister.setVertical_blank(false);
                statusRegister.setSprite_overflow(false);
                statusRegister.setSprite_zero_hit(false);
                for (int i = 0; i < 8; i++) {
                    sprite_shift_pattern_low[i] = 0x00;
                    sprite_shift_pattern_high[i] = 0x00;
                }
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
            if (scanline == -1 && cycle >= 280 && cycle < 305) {
                transferAddressY.run();
            }
            if (cycle == 338 || cycle == 340)
            {
                bg_next_tile_id = ppuRead(0x2000 | (vram_addr.get() & 0x0FFF));
            }

            // Foregorund
            if (cycle == 257 && scanline >= 0) {
                for (int i = 0; i < visible_oams.length; i++)
                    visible_oams[i].clear(0xFF);
                sprite_count = 0;

                int oam_entry = 0;
                spriteZeroHitPossible = false;
                while (oam_entry < 64 && sprite_count < 9) {
                    int diff = scanline - oams[oam_entry].getY();
                    if (diff >= 0 && diff < (controlRegister.isSprite_size() ? 16 : 8)) {
                        if (sprite_count < 8) {
                            if (oam_entry == 0) {
                                spriteZeroHitPossible = true;
                            }
                            visible_oams[sprite_count].set(oams[oam_entry]);
                            sprite_count++;
                        }
                    }
                    oam_entry++;
                }
                statusRegister.setSprite_overflow(sprite_count > 8);
            }
            if (cycle == 330) {
                for (int i = 0; i < sprite_count; i++) {
                    int sprite_pattern_low, sprite_pattern_high;
                    int sprite_pattern_addr_low, sprite_pattern_addr_high;
                    if (!controlRegister.isSprite_size()) {
                        //8x8
                        if (!((visible_oams[i].getAttribute() & 0x80) == 0x80)) {
                            sprite_pattern_addr_low = (controlRegister.isPattern_sprite() ? 0x1 << 12 : 0x0) | ((visible_oams[i].getId() & 0x00FF) << 4) | (scanline - (visible_oams[i].getY() & 0x00FF));
                        } else {
                            //flipped vertically
                            sprite_pattern_addr_low = (controlRegister.isPattern_sprite() ? 0x1 << 12 : 0x0) | ((visible_oams[i].getId() & 0x00FF) << 4) | (7 - (scanline - (visible_oams[i].getY() & 0x00FF)));
                        }
                    } else {
                        //8x16
                        if (!((visible_oams[i].getAttribute() & 0x80) == 0x80)) {
                            if (scanline - (visible_oams[i].getY() & 0x00FF) < 8) {
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | ((visible_oams[i].getId() & 0x00FE) << 4) | ((scanline - (visible_oams[i].getY() & 0x00FF)) & 0x07);
                            } else {
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | (((visible_oams[i].getId() & 0x00FE) + 1) << 4) | ((scanline - (visible_oams[i].getY() & 0x00FF)) & 0x07);
                            }
                        } else {
                            //flipped vertically
                            if (scanline - (visible_oams[i].getY() & 0x00FF) < 8) {
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | (((visible_oams[i].getId() & 0x00FE) + 1) << 4) | (7 - (scanline - (visible_oams[i].getY() & 0x00FF)) & 0x07);
                            } else {
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | ((visible_oams[i].getId() & 0x00FE) << 4) | (7 - (scanline - (visible_oams[i].getY() & 0x00FF)) & 0x07);
                            }
                        }
                    }
                    sprite_pattern_addr_high = (sprite_pattern_addr_low + 8) & 0xFFFF;
                    sprite_pattern_low = ppuRead(sprite_pattern_addr_low);
                    sprite_pattern_high = ppuRead(sprite_pattern_addr_high);

                    if ((visible_oams[i].getAttribute() & 0x40) == 0x40) {
                        sprite_pattern_low = NumberUtils.byteFlip(sprite_pattern_low);
                        sprite_pattern_high = NumberUtils.byteFlip(sprite_pattern_high);
                    }

                    sprite_shift_pattern_low[i] = sprite_pattern_low;
                    sprite_shift_pattern_high[i] = sprite_pattern_high;

                }
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

        int fg_pixel = 0x00;
        int fg_palette = 0x00;
        boolean fg_priority = false;

        if (maskRegister.isRender_sprites()) {
            spriteZeroBeingRendered = false;
            for (int i = 0; i < sprite_count; i++) {
                if (visible_oams[i].getX() == 0) {
                    int fg_pixel_low = (sprite_shift_pattern_low[i] & 0x80) == 0x80 ? 0x1 : 0x0;
                    int fg_pixel_high = (sprite_shift_pattern_high[i] & 0x80) == 0x80 ? 0x1 : 0x0;
                    fg_pixel = ((fg_pixel_high << 1) | fg_pixel_low) & 0x03;
                    fg_palette = (visible_oams[i].getAttribute() & 0x03) + 0x04;
                    fg_priority = (visible_oams[i].getAttribute() & 0x20) == 0;

                    if (fg_pixel != 0) {
                        if (i == 0)
                            spriteZeroBeingRendered = true;
                        break;
                    }
                }
            }
        }

        int pixel = 0x00;
        int palette = 0x00;

        if (bg_pixel == 0 && fg_pixel == 0) {
            pixel = 0x00;
            palette = 0x00;
        }
        if (bg_pixel == 0 && fg_pixel > 0) {
            pixel = fg_pixel;
            palette = fg_palette;
        }
        if (bg_pixel > 0 && fg_pixel == 0) {
            pixel = bg_pixel;
            palette = bg_palette;
        }
        if (bg_pixel > 0 && fg_pixel > 0) {
            if (fg_priority) {
                pixel = fg_pixel;
                palette = fg_palette;
            } else {
                pixel = bg_pixel;
                palette = bg_palette;
            }
            if (spriteZeroBeingRendered && spriteZeroHitPossible)
                if (maskRegister.isRender_background() && maskRegister.isRender_sprites())
                    if (!(maskRegister.isRender_background_left() || maskRegister.isRender_sprite_left())) {
                        if (cycle >= 9 && cycle < 258)
                            statusRegister.setSprite_zero_hit(true);
                    } else
                        if (cycle >= 1 && cycle < 258)
                            statusRegister.setSprite_zero_hit(true);
        }

        screen.setPixel(cycle - 1, scanline, getColorFromPalette(palette, pixel));

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
                        int pixel = ((tile_lsb & 0x01) << 1) | (tile_msb & 0x01);
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

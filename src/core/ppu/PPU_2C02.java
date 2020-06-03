package core.ppu;

import core.cartridge.Cartridge;
import core.ppu.registers.*;
import org.lwjgl.BufferUtils;
import utils.IntegerWrapper;
import utils.NumberUtils;

import java.awt.*;
import java.nio.ByteBuffer;

/**
 * This class represent the PPU of the NES
 * it Handle everything graphics related
 */
public class PPU_2C02 {

    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 240;

    private final Color[] palScreen;
    private final ByteBuffer screen_buffer;
    private final ByteBuffer[] patterntables;
    private final ByteBuffer[] nametables;

    private final byte[][] tblName;
    private final byte[] tblPalette;
    private final byte[][] tblPattern;

    private final MaskRegister maskRegister;
    private final ControlRegister controlRegister;
    private final StatusRegister statusRegister;
    private final LoopyRegister vram_addr;
    private final LoopyRegister tram_addr;

    private final ObjectAttribute[] oams;
    private final ObjectAttribute[] visible_oams;

    private final int[] sprite_shift_pattern_low;
    private final int[] sprite_shift_pattern_high;

    public boolean frameComplete;
    private Cartridge cartridge;

    private int sprite_count;
    private int address_latch = 0x00;
    private int ppu_data_buffer = 0x00;
    private int oam_addr = 0x00;
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
    private boolean odd_frame = false;
    private boolean nmi;

    /**
     * Create a new PPU, instantiate its components and fill up the palettes
     */
    public PPU_2C02() {
        tblName = new byte[2][1024];
        tblPattern = new byte[2][4096];
        tblPalette = new byte[32];
        palScreen = new Color[0x40];
        screen_buffer = BufferUtils.createByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4);
        patterntables = new ByteBuffer[]{
            BufferUtils.createByteBuffer(128 * 128 * 4),
            BufferUtils.createByteBuffer(128 * 128 * 4)
        };
        nametables = new ByteBuffer[]{
            BufferUtils.createByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4),
            BufferUtils.createByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4),
            BufferUtils.createByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4),
            BufferUtils.createByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4)
        };
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

    /**
     * Return the screen buffer
     *
     * @return a ByteBuffer that can be loaded into a texture to be displayed on the screen
     */
    public ByteBuffer getScreenBuffer() {
        return screen_buffer;
    }

    /**
     * Connect a Cartridge to the CPU
     *
     * @param cartridge the Cartridge to connect
     */
    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    /**
     * Called when the CPU wants to read from the PPU Memory (Registers)
     *
     * @param addr     the address to read from (8 locations mirrored through the addressable range)
     * @param readOnly is the access allowed to alter the PPU state
     * @return the read data as an 8bit unsigned value
     */
    public int cpuRead(int addr, boolean readOnly) {
        int data = 0x00;
        addr &= 0xFFFF;
        if (readOnly) {
            //If in read only, the data access is Thread safe and don't alter the PPU state used for debug purposes
            synchronized (this) {
                switch (addr) {
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
                return data & 0xFF;
            }
        }
        switch (addr) {
            case 0x0000: // Control
                break;
            case 0x0001: // Mask
                data = oam_addr;
                break;
            case 0x0002: // Status
                //When reading the Status Register, the unused bits are filled with le last data that was read
                data = (statusRegister.get() & 0xE0) | (ppu_data_buffer & 0x1F);
                //The Vertical Blank Flag is reset
                statusRegister.setVerticalBlank(false);
                //The address_latch is also reset to ensure proper write for the next time
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
                //Nametable reads are delayed by one cycle
                //When reading the last fetched data is returned and the next is fetched
                data = ppu_data_buffer;
                ppu_data_buffer = ppuRead(vram_addr.get());
                //Except palette, here their is no delay
                if (vram_addr.get() >= 0x3F00) data = ppu_data_buffer;
                //The vram address is incremented (horizontally or vertically depending on the Control Register)
                vram_addr.set(vram_addr.get() + (controlRegister.isIncrementModeSet() ? 32 : 1));
                break;
        }
        return data & 0xFF;
    }

    /**
     * Called when the CPU wants to write to the PPU Memory (Registers)
     *
     * @param addr the address to write to (8 locations mirrored through the addressable range)
     * @param data the data to write
     */
    public void cpuWrite(int addr, int data) {
        data &= 0xFF;
        addr &= 0xFFFF;
        switch (addr) {
            case 0x0000: // Control
                controlRegister.set(data);
                //When writing to the Control Register, one of the Loopy Register need to be updated (in case the nametable has changed)
                tram_addr.setNametableX(controlRegister.isNametableXSet());
                tram_addr.setNametableY(controlRegister.isNametableYSet());
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
                switch (oam_addr & 0x03) {
                    case 0x0:
                        oams[oam_addr >> 2].setY(data);
                    case 0x1:
                        oams[oam_addr >> 2].setId(data);
                    case 0x2:
                        oams[oam_addr >> 2].setAttribute(data);
                    case 0x3:
                        oams[oam_addr >> 2].setX(data);
                }
                oam_addr++;
                oam_addr &= 0xFF;
                break;
            case 0x0005: // Scroll
                //When writing to the Scroll Register, we first write the X offset
                if (address_latch == 0) {
                    //The offset is spliced into coarseX and fineX
                    fine_x = data & 0x07;
                    tram_addr.setCoarseX(data >> 3);
                    address_latch = 1;
                    //The second write is the Y offset
                } else {
                    //The offset is spliced into coarseY and fineY
                    tram_addr.setFineY(data & 0x07);
                    tram_addr.setCoarseY(data >> 3);
                    address_latch = 0;
                }
                break;
            case 0x0006: // PPU Address
                //An address is 16bit, therefor we need 2 write cycle to load a full address
                //The first write is the 8 MSB of the address
                if (address_latch == 0) {
                    tram_addr.set((tram_addr.get() & 0x00FF) | ((data & 0x3F) << 8));
                    address_latch = 1;
                    //The second write is the 8 LSB of the address
                } else {
                    tram_addr.set((tram_addr.get() & 0xFF00) | data);
                    //When the address has been fully fetched, it is store into the main Loopy Register
                    vram_addr.set(tram_addr.get());
                    address_latch = 0;
                }
                break;
            case 0x0007: // PPU Data
                //The data is written to the VRAM address
                ppuWrite(vram_addr.get(), data);
                //The vram address is incremented (horizontally or vertically depending on the Control Register)
                vram_addr.set(vram_addr.get() + (controlRegister.isIncrementModeSet() ? 32 : 1));
                break;
        }
    }

    /**
     * Called when the PPU wants to read from its Memory
     *
     * @param addr the address to read from
     * @return the read data
     */
    private int ppuRead(int addr) {
        addr &= 0x3FFF;
        //A Wrapper used to store the data gathered by the Cartridge
        IntegerWrapper data = new IntegerWrapper();
        //If the address is mapped by the cartridge, let it handle and return read value
        if (!cartridge.ppuRead(addr, data)) {
            if (addr <= 0x1FFF) { //Read from pattern table
                data.value = tblPattern[(addr & 0x1000) >> 12][addr & 0x0FFF];
            } else if (addr <= 0x3EFF) { //Read from nametable
                addr &= 0x0FFF;
                if (cartridge.getMirror() == Mirror.VERTICAL) {
                    if (addr <= 0x03FF)
                        data.value = tblName[0][addr & 0x03FF];
                    if (addr >= 0x0400 && addr <= 0x07FF)
                        data.value = tblName[1][addr & 0x03FF];
                    if (addr >= 0x0800 && addr <= 0x0BFF)
                        data.value = tblName[0][addr & 0x03FF];
                    if (addr >= 0x0C00)
                        data.value = tblName[1][addr & 0x03FF];
                } else if (cartridge.getMirror() == Mirror.HORIZONTAL) {
                    if (addr <= 0x03FF)
                        data.value = tblName[0][addr & 0x03FF];
                    if (addr >= 0x0400 && addr <= 0x07FF)
                        data.value = tblName[0][addr & 0x03FF];
                    if (addr >= 0x0800 && addr <= 0x0BFF)
                        data.value = tblName[1][addr & 0x03FF];
                    if (addr >= 0x0C00)
                        data.value = tblName[1][addr & 0x03FF];
                }
            } else { //Read from palette memory
                addr &= 0x1F;
                if (addr == 0x0010) addr = 0x0000;
                if (addr == 0x0014) addr = 0x0004;
                if (addr == 0x0018) addr = 0x0008;
                if (addr == 0x001C) addr = 0x000C;
                data.value = tblPalette[addr] & (maskRegister.isGrayscaleSet() ? 0x30 : 0x3F);
            }
        }
        return data.value & 0xFF;
    }

    /**
     * Called when the PPU wants to write to its Memory
     *
     * @param addr the address to write to
     * @param data the data to write
     */
    private void ppuWrite(int addr, int data) {
        addr &= 0x3FFF;
        data &= 0xFF;
        //If the address is mapped by the cartridge, let it handle and return
        if (!cartridge.ppuWrite(addr, data)) {
            if (addr <= 0x1FFF) { //Write to pattern table
                tblPattern[(addr & 0x1000) >> 12][addr & 0x0FFF] = (byte) data;

            } else if (addr <= 0x3EFF) { //Write to nametable
                addr &= 0x0FFF;
                if (cartridge.getMirror() == Mirror.VERTICAL) {
                    if (addr <= 0x03FF)
                        tblName[0][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0400 && addr <= 0x07FF)
                        tblName[1][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0800 && addr <= 0x0BFF)
                        tblName[0][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0C00)
                        tblName[1][addr & 0x03FF] = (byte) data;
                } else if (cartridge.getMirror() == Mirror.HORIZONTAL) {
                    if (addr <= 0x03FF)
                        tblName[0][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0400 && addr <= 0x07FF)
                        tblName[0][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0800 && addr <= 0x0BFF)
                        tblName[1][addr & 0x03FF] = (byte) data;
                    if (addr >= 0x0C00)
                        tblName[1][addr & 0x03FF] = (byte) data;
                }
            } else { //Writting to palette memory
                addr &= 0x001F;
                if (addr == 0x0010) addr = 0x0000;
                if (addr == 0x0014) addr = 0x0004;
                if (addr == 0x0018) addr = 0x0008;
                if (addr == 0x001C) addr = 0x000C;
                tblPalette[addr] = (byte) data;
            }
        }
    }

    /**
     * Return the an 8bit value from the Object Attribute Memory pointed by the current OAM address
     *
     * @return an 8bit unsigned value pointed by the current OAM address
     */
    private int getOamData() {
        switch (oam_addr & 0x03) {
            case 0x0:
                return oams[oam_addr >> 2].getY() & 0xFF;
            case 0x1:
                return oams[oam_addr >> 2].getId() & 0xFF;
            case 0x2:
                return oams[oam_addr >> 2].getAttribute() & 0xFF;
            case 0x3:
                return oams[oam_addr >> 2].getX() & 0xFF;
        }
        return 0x00;
    }

    /**
     * Return a palette color given a palette ID and a pixel ID
     *
     * @param paletteId the palette ID
     * @param pixel     the pixel ID
     * @return the corresponding Color
     */
    private Color getColorFromPalette(int paletteId, int pixel) {
        return palScreen[ppuRead(0x3F00 + ((paletteId << 2) & 0x00FF) + (pixel & 0x00FF))];
    }

    /**
     * Return whether or not a Non Maskable Interrupt should be fired to the CPU
     *
     * @return do we have to fire a NMI
     */
    public boolean nmi() {
        //If we fire a NMI, the flag is reset to avoid multiple NMI in chain
        if (nmi) {
            nmi = false;
            return true;
        }
        return false;
    }

    /**
     * Reset the PPU to its default state
     */
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
        statusRegister.set(0xA0);
        maskRegister.set(0x00);
        controlRegister.set(0x00);
        vram_addr.set(0x0000);
        tram_addr.set(0x0000);
        screen_buffer.clear();
    }



    /**
     * Execute one tick of the PPU
     */
    public void clock() {
        Runnable incrementScrollX = () -> {
            //If we are rendering sprites or background
            if (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet()) {
                //If we cross a nametable boundary we invert the nametableX bit to fetch from the other nametable
                if (vram_addr.getCoarseX() == 31) {
                    vram_addr.setCoarseX(0);
                    vram_addr.setNametableX(!vram_addr.isNametableXSet());
                    //Or we just continue in the same one
                } else {
                    vram_addr.setCoarseX(vram_addr.getCoarseX() + 1);
                }
            }
        };
        Runnable incrementScrollY = () -> {
            //If we are rendering sprites or background
            if (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet()) {
                //If we are still in the same tile row
                if (vram_addr.getFineY() < 7) {
                    vram_addr.setFineY(vram_addr.getFineY() + 1);
                    //If we have passed to the next tile row
                } else {
                    //reset the offset inside the row to 0
                    vram_addr.setFineY(0);
                    //If we are at le last tile row, we skip the OAM and switch to the next nametable
                    if (vram_addr.getCoarseY() == 29) {
                        vram_addr.setCoarseY(0);
                        vram_addr.setNametableY(!vram_addr.isNametableYSet());
                        //Just in case we've gone behond the nametable
                    } else if (vram_addr.getCoarseY() == 31) {
                        vram_addr.setCoarseY(0);
                        //Or we simply switch to the next tile row
                    } else {
                        vram_addr.setCoarseY(vram_addr.getCoarseY() + 1);
                    }
                }
            }
        };
        Runnable transferAddressX = () -> {
            if (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet()) {
                vram_addr.setNametableX(tram_addr.isNametableXSet());
                vram_addr.setCoarseX(tram_addr.getCoarseX());
            }
        };
        Runnable transferAddressY = () -> {
            if (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet()) {
                vram_addr.setNametableY(tram_addr.isNametableYSet());
                vram_addr.setCoarseY(tram_addr.getCoarseY());
                vram_addr.setFineY(tram_addr.getFineY());
            }
        };
        Runnable loadBackgroundShifter = () -> {
            bg_shift_pattern_low = ((bg_shift_pattern_low & 0xFF00) | bg_next_tile_lsb) & 0xFFFF;
            bg_shift_pattern_high = ((bg_shift_pattern_high & 0xFF00) | bg_next_tile_msb) & 0xFFFF;
            bg_shift_attrib_low = ((bg_shift_attrib_low & 0xFF00) | (((bg_next_tile_attrib & 0b01) == 0b01) ? 0xFF : 0x00)) & 0xFFFF;
            bg_shift_attrib_high = ((bg_shift_attrib_high & 0xFF00) | (((bg_next_tile_attrib & 0b10) == 0b10) ? 0xFF : 0x00)) & 0xFFFF;
        };
        Runnable updateShifter = () -> {
            if (maskRegister.isRenderBackgroundSet()) {
                bg_shift_pattern_low = (bg_shift_pattern_low << 1) & 0xFFFF;
                bg_shift_pattern_high = (bg_shift_pattern_high << 1) & 0xFFFF;
                bg_shift_attrib_low = (bg_shift_attrib_low << 1) & 0xFFFF;
                bg_shift_attrib_high = (bg_shift_attrib_high << 1) & 0xFFFF;
            }
            if (maskRegister.isRenderSpritesSet() && cycle >= 1 && cycle < 258) {
                for (int i = 0; i < sprite_count; i++) {
                    //for all visible sprites, we decrement the position by one until it is time to render it
                    if (visible_oams[i].getX() > 0)
                        visible_oams[i].setX(visible_oams[i].getX() - 1);
                    else {
                        sprite_shift_pattern_low[i] = (sprite_shift_pattern_low[i] << 1) & 0xFF;
                        sprite_shift_pattern_high[i] = (sprite_shift_pattern_high[i] << 1) & 0xFF;
                    }
                }
            }
        };

        //If we are in the visible screen (regarding scanlines and omitting horizontal blank)
        if (scanline >= -1 && scanline < 240) {
            if (scanline == -1 && cycle == 0)
                screen_buffer.clear();
            //If we are on the top left we increment the cycle count and clear the screen buffer
            if (scanline == 0 && cycle == 0 && odd_frame && (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet())) {
                cycle = 1;
            }
            //If we are before the first scanline, we reset the Status Register and Shift Registers
            if (scanline == -1 && cycle == 1) {
                statusRegister.setVerticalBlank(false);
                statusRegister.setSpriteOverflow(false);
                statusRegister.setSpriteZeroHit(false);
                for (int i = 0; i < 8; i++) {
                    sprite_shift_pattern_low[i] = 0x00;
                    sprite_shift_pattern_high[i] = 0x00;
                }
            }
            //If we need to compute a pixel color
            if ((cycle >= 2 && cycle < 258) || (cycle >= 321 && cycle < 338)) {
                //We shift all the Shift Registers by 1
                updateShifter.run();
                //All of the following action will be executed once and in order for each tile
                //We are fetching the information required for the next tile (8 pixels)
                switch ((cycle - 1) % 8) {
                    case 0:
                        //At the beginning of a tile we load the Background Shifters with the previously fetched tile ID and tile attribute
                        loadBackgroundShifter.run();
                        //We fetch the next tile ID
                        bg_next_tile_id = ppuRead(0x2000 | (vram_addr.get() & 0x0FFF));
                        break;
                    case 2:
                        //We then fetch the next tile attribute
                        bg_next_tile_attrib = ppuRead(0x23C0 | (vram_addr.isNametableYSet() ? 0x1 << 11 : 0x0) | (vram_addr.isNametableXSet() ? 0x1 << 10 : 0x0) | ((vram_addr.getCoarseY() >> 2) << 3) | (vram_addr.getCoarseX() >> 2));
                        //We use the Coarses 2 lsb to get select the correct 2 bits of the attribute depending on the position of the tile in the 4*4 grid
                        if ((vram_addr.getCoarseY() & 0x02) == 0x02)
                            bg_next_tile_attrib = (bg_next_tile_attrib >> 4) & 0xFF;
                        if ((vram_addr.getCoarseX() & 0x02) == 0x02)
                            bg_next_tile_attrib = (bg_next_tile_attrib >> 2) & 0xFF;
                        //We only keep the 2 lsb of the attribute
                        bg_next_tile_attrib &= 0x03;
                        break;
                    case 4:
                        //We use the next tile ID and row index (fineY) to fetch the next 8 pixels lsb
                        bg_next_tile_lsb = ppuRead((controlRegister.isPatternBackgroundSet() ? 0x1 << 12 : 0) + (bg_next_tile_id << 4) + vram_addr.getFineY());
                        break;
                    case 6:
                        //Same but we fetch the msb
                        bg_next_tile_msb = ppuRead((controlRegister.isPatternBackgroundSet() ? 0x1 << 12 : 0) + (bg_next_tile_id << 4) + vram_addr.getFineY() + 8);
                        break;
                    case 7:
                        //We pass to next tile rendering
                        incrementScrollX.run();
                        break;
                }
            }
            //If we are at the end of a visible scanline we pass to the next one
            if (cycle == 256) {
                incrementScrollY.run();
            }
            //If we are at the first pixel of the horizontal blank we reset the X coordinates to the start of a line
            if (cycle == 257) {
                loadBackgroundShifter.run();
                transferAddressX.run();
            }

            if (cycle == 338 || cycle == 340) {
                bg_next_tile_id = ppuRead(0x2000 | (vram_addr.get() & 0x0FFF));
            }
            //At the start of a new frame we reset the Y coordinates to the top of the screen
            if (scanline == -1 && cycle >= 280 && cycle < 305) {
                transferAddressY.run();
            }

            //At the end of a scanline, we fetch the sprite that will be visible on the next scanline
            if (cycle == 257 && scanline >= 0) {
                //We clear all visible Object Attribute
                for (ObjectAttribute visible_oam : visible_oams) visible_oam.clear(0xFF);
                //And reset the scripte count
                sprite_count = 0;

                for (int i = 0; i < 8; i++) {
                    sprite_shift_pattern_low[i] = 0x00;
                    sprite_shift_pattern_high[i] = 0x00;
                }

                //We reset the oam entry index and sprite zero hit possible flag
                int oam_entry = 0;
                spriteZeroHitPossible = false;

                //We read all OAM and break if we hit the max number of sprite for one scanline
                while (oam_entry < 64 && sprite_count < 9) {
                    //We compute if the sprite is in the current scanline
                    int diff = scanline - oams[oam_entry].getY();
                    if (diff >= 0 && diff < (controlRegister.isSpriteSizeSet() ? 16 : 8)) {
                        //If their is room left for another sprite, we add it to the rendered sprite
                        if (sprite_count < 8) {
                            //If this is the first sprite, a sprite zero hit is possible, we update the flag
                            if (oam_entry == 0) {
                                spriteZeroHitPossible = true;
                            }
                            //Instead of instantiating new OAM, we fill it with the data of the other one
                            visible_oams[sprite_count].set(oams[oam_entry]);
                        }
                        sprite_count++;
                    }
                    oam_entry++;
                }
                //If we hit a 9th sprite on the scanline, we set the sprite overflow flag to 1
                statusRegister.setSpriteOverflow(sprite_count >= 8);
                if (sprite_count >= 8) sprite_count = 8;
            }
            //At the end of the horizontal blank, we fetch all the relevant sprite data for the next scanline
            //This is really done one multiple cycles, but it's easier to do it all in one go and doesn't change the overall behaviour of the rendering process
            if (cycle == 340) {
                //For each sprite
                for (int i = 0; i < sprite_count; i++) {
                    int sprite_pattern_low, sprite_pattern_high;
                    int sprite_pattern_addr_low, sprite_pattern_addr_high;
                    if (!controlRegister.isSpriteSizeSet()) { //If the sprites are 8x8
                        if (!((visible_oams[i].getAttribute() & 0x80) == 0x80)) //If the sprite normally oriented
                            sprite_pattern_addr_low = (controlRegister.isPatternSpriteSet() ? 0x1 << 12 : 0x0) | (visible_oams[i].getId() << 4) | (scanline - visible_oams[i].getY());
                        else //If the sprite is flipped vertically
                            sprite_pattern_addr_low = (controlRegister.isPatternSpriteSet() ? 0x1 << 12 : 0x0) | (visible_oams[i].getId() << 4) | (7 - (scanline - visible_oams[i].getY()));
                    } else { //If the sprites are 8x16
                        if (!((visible_oams[i].getAttribute() & 0x80) == 0x80)) { //If the sprite normally oriented
                            if (scanline - visible_oams[i].getY() < 8) //Reading top half
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | ((visible_oams[i].getId() & 0xFE) << 4) | ((scanline - visible_oams[i].getY()) & 0x07);
                            else //Reading bottom half
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | (((visible_oams[i].getId() & 0xFE) + 1) << 4) | ((scanline - visible_oams[i].getY()) & 0x07);
                        } else {  //If the sprite is flipped vertically
                            if (scanline - visible_oams[i].getY() < 8) //Reading top half
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | (((visible_oams[i].getId() & 0xFE) + 1) << 4) | (7 - (scanline - visible_oams[i].getY()) & 0x07);
                            else //Reading bottom half
                                sprite_pattern_addr_low = ((visible_oams[i].getId() & 0x01) << 12) | ((visible_oams[i].getId() & 0xFE) << 4) | (7 - (scanline - visible_oams[i].getY()) & 0x07);
                        }
                    }
                    //We compute the complete address and fetch the the sprite's bitplane
                    sprite_pattern_addr_high = (sprite_pattern_addr_low + 8) & 0xFFFF;
                    sprite_pattern_low = ppuRead(sprite_pattern_addr_low);
                    sprite_pattern_high = ppuRead(sprite_pattern_addr_high);

                    //If the sprite is flipped horizontally, the sprite bitplane are flipped
                    if ((visible_oams[i].getAttribute() & 0x40) == 0x40) {
                        sprite_pattern_low = NumberUtils.byteFlip(sprite_pattern_low);
                        sprite_pattern_high = NumberUtils.byteFlip(sprite_pattern_high);
                    }

                    //We load the sprites to the Shift Registers
                    sprite_shift_pattern_low[i] = sprite_pattern_low;
                    sprite_shift_pattern_high[i] = sprite_pattern_high;
                }
            }
        }

        //If we exit the visible screen, we set the vertical blank flag and eventually fire a Non Maskable Interrupt
        if (scanline >= 241 && scanline < 261) {
            if (scanline == 241 && cycle == 1) {
                statusRegister.setVerticalBlank(true);
                if (controlRegister.isEnableNmiSet())
                    nmi = true;
            }
        }

        int bg_pixel = 0x00;
        int bg_palette = 0x00;

        //If background rendering is enabled
        if (maskRegister.isRenderBackgroundSet()) {
            //We select the current pixels offset using the scroll information
            if (maskRegister.isRenderBackgroundLeftSet() || cycle >= 9) {
                int bit_mux = (0x8000 >> fine_x) & 0xFFFF;
                //We compute the pixel ID by getting the right bit from the 2 shift registers
                int p0_pixel = (bg_shift_pattern_low & bit_mux) > 0 ? 0x1 : 0x0;
                int p1_pixel = (bg_shift_pattern_high & bit_mux) > 0 ? 0x1 : 0x0;
                bg_pixel = ((p1_pixel << 1) | p0_pixel) & 0x0F;
                //Same for the palette ID
                int bg_pal0 = (bg_shift_attrib_low & bit_mux) > 0 ? 0x1 : 0x0;
                int bg_pal1 = (bg_shift_attrib_high & bit_mux) > 0 ? 0x1 : 0x0;
                bg_palette = ((bg_pal1 << 1) | bg_pal0) & 0x0F;
            }
        }

        int fg_pixel = 0x00;
        int fg_palette = 0x00;
        boolean fg_priority = false;

        //If sprite rendering is enabled
        if (maskRegister.isRenderSpritesSet()) {
            //The 0th sprite being rendered flag is reset
            if (maskRegister.isRenderSpriteLeftSet() || cycle >= 9) {
                spriteZeroBeingRendered = false;
                //For each sprite in order of priority
                for (int i = 0; i < sprite_count; i++) {
                    //If we are at the sprite X location
                    if (visible_oams[i].getX() == 0) {
                        //We get the foreground pixel lsb and msb
                        int fg_pixel_low = (sprite_shift_pattern_low[i] & 0x80) == 0x80 ? 0x1 : 0x0;
                        int fg_pixel_high = (sprite_shift_pattern_high[i] & 0x80) == 0x80 ? 0x1 : 0x0;
                        //We combine them into a 2bit ID
                        fg_pixel = ((fg_pixel_high << 1) | fg_pixel_low) & 0x03;
                        //We get the sprite palette and if it has priority over the background
                        fg_palette = (visible_oams[i].getAttribute() & 0x03) + 0x04;
                        fg_priority = (visible_oams[i].getAttribute() & 0x20) == 0;

                        //If the pixel isn't transparent and we are rendering sprite 0, we set the 0th sprite being rendered to true
                        if (fg_pixel != 0) {
                            if (i == 0)
                                spriteZeroBeingRendered = true;
                            break;
                        }
                    }
                }
            }
        }

        int pixel = 0x00;
        int palette = 0x00;
        //If the background pixel is transparent the final color is the foreground one
        if (bg_pixel == 0 && fg_pixel > 0) {
            pixel = fg_pixel;
            palette = fg_palette;
        }
        //If the foreground color is transparent the final color is the background one
        if (bg_pixel > 0 && fg_pixel == 0) {
            pixel = bg_pixel;
            palette = bg_palette;
        }
        //If neither of the pixels are transparent
        if (bg_pixel > 0 && fg_pixel > 0) {
            //If the foreground has priority over the background, the final color is the foreground one
            if (fg_priority) {
                pixel = fg_pixel;
                palette = fg_palette;
                //Otherwise the final color is the background one
            } else {
                pixel = bg_pixel;
                palette = bg_palette;
            }
            //If we are rendering the 0th sprite and a sprite zero hit is possible then a sprite zero hit may have occur
            if (spriteZeroBeingRendered && spriteZeroHitPossible) {
                //If we are rendering background and sprites
                if (maskRegister.isRenderBackgroundSet() && maskRegister.isRenderSpritesSet()) {
                    //If we are in the valid test.state space (if we don't render the first columns we don't test.state for hit in it)
                    if (!(maskRegister.isRenderBackgroundLeftSet() || maskRegister.isRenderSpriteLeftSet())) {
                        if (cycle >= 9 && cycle < 258)
                            statusRegister.setSpriteZeroHit(true);
                    } else if (cycle >= 1 && cycle < 258)
                        statusRegister.setSpriteZeroHit(true);
                }
            }
        }

        //If we are in the visible area we push a pixel into the screen buffer
        if (cycle - 1 >= 0 && cycle - 1 < SCREEN_WIDTH && scanline >= 0 && scanline < SCREEN_HEIGHT) {
            int rgba = getColorFromPalette(palette, pixel).getRGB();
            screen_buffer.put((byte) ((rgba >> 16) & 0xFF));
            screen_buffer.put((byte) ((rgba >> 8) & 0xFF));
            screen_buffer.put((byte) ((rgba) & 0xFF));
            screen_buffer.put((byte) ((rgba >> 24) & 0xFF));
        }

        cycle++;
        if (maskRegister.isRenderBackgroundSet() || maskRegister.isRenderSpritesSet()) {
            if (cycle == 260 && scanline < 240) {
                cartridge.getMapper().scanline();
            }
        }
        //If we are at the end of a scanline
        if (cycle >= 341) {
            cycle = 0;
            scanline++;
            //If we are a the bottom of the screen
            if (scanline >= 261) {
                //We reset the scanline to the top, set the frameComplete flag and flip the screen buffer to prepare rendering
                scanline = -1;
                frameComplete = true;
                odd_frame = !odd_frame;
                screen_buffer.flip();
            }
        }
    }

    // ========================================================== Debug Methods ========================================================== //

    /**
     * Return the Object Attribute Memory as an array of ObjectAttribute
     *
     * @return an array of ObjectAttribute containing all the OAM
     */
    public synchronized ObjectAttribute[] getOams() {
        return oams;
    }

    /**
     * Return a ByteBuffer that can be loaded in a texture to display the pattern table
     *
     * @param i         the pattern table index
     * @param paletteId the paletteId to be used
     * @return a ByteBuffer containing the pixel values of the pattern table already flipped and ready to be fed to OpenGL
     */
    public synchronized ByteBuffer getPatternTable(int i, int paletteId) {
        //Clear the patterntable buffer
        patterntables[i].clear();
        //Create a temporary buffer because the pixels are not calculated in screen order
        int[] tmp = new int[128 * 128];
        //For each row of tiles starting at the top
        for (int tileY = 0; tileY < 16; tileY++) {
            //For each tile starting at the left
            for (int tileX = 0; tileX < 16; tileX++) {
                //We compute the tile offset inside the Pattern Memory
                int offset = tileY * 256 + tileX * 16;
                //For each row of the tile
                for (byte row = 0; row < 8; row++) {
                    //We get the lsb of the pixels of the row
                    int tile_lsb = ppuRead(i * 0x1000 + offset + row);
                    //We get the msb of the pixels of the row
                    int tile_msb = ppuRead(i * 0x1000 + offset + row + 8);
                    //for each pixel of the row
                    for (int col = 0; col < 8; col++) {
                        //We compute the pixel id
                        int pixel = ((tile_lsb & 0x01) << 1) | (tile_msb & 0x01);
                        //We shift the tile registers to get the next pixel id
                        tile_lsb >>= 1;
                        tile_msb >>= 1;
                        //We populate the buffer by getting the right color from the palette using the palette and pixel IDs
                        tmp[(tileX * 8 + (7 - col)) + 128 * (tileY * 8 + row)] = threadSafeGetColorFromPalette(paletteId, pixel).getRGB();
                    }
                }
            }
        }
        //We populate the pixels buffer in the right order
        fillBuffer(tmp, patterntables[i]);
        return patterntables[i];
    }

    /**
     * Fill a pixel ByteBuffer with an indexed array op RGBA values
     *
     * @param tmp    the RGBA array
     * @param buffer the buffer to fill
     */
    private void fillBuffer(int[] tmp, ByteBuffer buffer) {
        for (int rgba : tmp) {
            buffer.put((byte) ((rgba >> 16) & 0xFF)); //Red
            buffer.put((byte) ((rgba >> 8) & 0xFF)); //Green
            buffer.put((byte) ((rgba) & 0xFF)); //Blue
            buffer.put((byte) ((rgba >> 24) & 0xFF)); //Alpha
        }
        //The buffer is then flipped to be read by OpenGL
        buffer.flip();
    }

    /**
     * Return a ByteBuffer that can be loaded in a texture to display the nametable
     *
     * @param i the nametable index
     * @return a ByteBuffer containing the pixel values of the nametable already flipped and ready to be fed to OpenGL
     */
    public synchronized ByteBuffer getNametable(int i) {

        //Clear the nametable buffer
        nametables[i].clear();
        //Create a temporary buffer because the pixels are not calculated in screen order
        int[] tmp = new int[256 * 240];
        //For each row of tiles starting at the top
        for (int y = 0; y < 30; y++) {
            //For each tile starting at the left
            for (int x = 0; x < 32; x++) {
                //For each row of the tile
                for (int row = 0; row < 8; row++) {
                    //We read the tile ID by selecting the correct nametable using the mirroring mode
                    int offset = 0x0400 * (i & 0x3);
                    int tile_id = ppuRead(0x2000 | offset | (y << 5) | x);
                    //We read the tile attribute starting at offset 0x03C0 of the selected nametable, the attribute offset is calculated using the tile pos divided by 4
                    int tile_attrib = ppuRead(0x23C0 | offset | ((y >> 2) << 3) | (x >> 2));
                    //We select the right attribute depending on the tile pos inside the current 4x4 tile grid
                    if ((y & 0x02) == 0x02)
                        tile_attrib = (tile_attrib >> 4) & 0x00FF;
                    if ((x & 0x02) == 0x02)
                        tile_attrib = (tile_attrib >> 2) & 0x00FF;
                    //We only keep the 2 lsb of the attribute
                    tile_attrib &= 0x03;
                    //We use the tile id and the current row index to get the lsb of the 8 pixel IDs of the row (low bitplane)
                    int tile_lsb = ppuRead((controlRegister.isPatternBackgroundSet() ? 0x1 << 12 : 0) + (tile_id << 4) + row);
                    //We use the tile id and the current row index to get the msb of the 8 pixels of the row (high bitplane)
                    int tile_msb = ppuRead((controlRegister.isPatternBackgroundSet() ? 0x1 << 12 : 0) + (tile_id << 4) + row + 8);
                    //We use the attribute to determinate the tile palette
                    int palette = tile_attrib & 0b11;
                    int pid;
                    //For each pixel of the row
                    for (int col = 0; col < 8; col++) {
                        //We get the correct pixel ID by reading the 2 bitplanes
                        int p0_pixel = (tile_lsb & 0x80) > 0 ? 0x1 : 0x0;
                        int p1_pixel = (tile_msb & 0x80) > 0 ? 0x1 : 0x0;
                        int pixel = ((p1_pixel << 1) | p0_pixel) & 0x000F;
                        pid = palette;
                        //If the pixel ID is 0, then it's transparent so we use pixel 0 of palette 0
                        if (pixel == 0x00) pid = 0x00;
                        //We shift the tile registers to get the next pixel id
                        tile_lsb = (tile_lsb << 1) & 0xFFFF;
                        tile_msb = (tile_msb << 1) & 0xFFFF;
                        //We populate the buffer by getting the right color from the palette using the palette and pixel IDs
                        tmp[(x * 8 + (col)) + 256 * (y * 8 + row)] = threadSafeGetColorFromPalette(pid, pixel).getRGB();
                    }
                }
            }
        }
        //We populate the pixels buffer in the right order
        fillBuffer(tmp, nametables[i]);
        return nametables[i];
    }

    /**
     * Return a palette color given a palette ID and a pixel ID
     * Thread safe, used by the Debug Window
     *
     * @param paletteId the palette ID
     * @param pixel     the pixel ID
     * @return the corresponding Color
     */
    public synchronized Color threadSafeGetColorFromPalette(int paletteId, int pixel) {
        return palScreen[ppuRead(0x3F00 + ((paletteId << 2) & 0xFF) + (pixel & 0xFF)) & 0x3F];
    }
}

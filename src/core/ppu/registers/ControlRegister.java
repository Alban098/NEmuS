package core.ppu.registers;

/**
 * This class represent the Control Register of the 2C02 PPU
 */
public class ControlRegister {

    private boolean nametable_x = false;
    private boolean nametable_y = false;
    private boolean increment_mode = false;
    private boolean pattern_sprite = false;
    private boolean pattern_background = false;
    private boolean sprite_size = false;
    private boolean slave_mode = false;
    private boolean enable_nmi = false;

    /**
     * Load the Register with an 8bit value
     *
     * @param val the value to set (only the 8lsb are considered)
     */
    public void set(int val) {
        nametable_x = (val & 0x01) == 0x01;
        nametable_y = (val & 0x02) == 0x02;
        increment_mode = (val & 0x04) == 0x04;
        pattern_sprite = (val & 0x08) == 0x08;
        pattern_background = (val & 0x10) == 0x10;
        sprite_size = (val & 0x20) == 0x20;
        slave_mode = (val & 0x40) == 0x40;
        enable_nmi = (val & 0x80) == 0x80;
    }

    /**
     * Return the value of the Register as an 8bit value
     *
     * @return an int with the 8 lsb set accordingly
     */
    public int get() {
        int val = 0x00;
        val |= nametable_x ? 0x01 : 0x00;
        val |= nametable_y ? 0x02 : 0x00;
        val |= increment_mode ? 0x04 : 0x00;
        val |= pattern_sprite ? 0x08 : 0x00;
        val |= pattern_background ? 0x10 : 0x00;
        val |= sprite_size ? 0x20 : 0x00;
        val |= slave_mode ? 0x40 : 0x00;
        val |= enable_nmi ? 0x80 : 0x00;
        return val & 0xFF;
    }

    /**
     * Return whether or not the nametable_x bit set
     *
     * @return is the nametable_x bit set
     */
    public boolean isNametableXSet() {
        return nametable_x;
    }

    /**
     * Return whether or not the nametable_y bit set
     *
     * @return is the nametable_y bit set
     */
    public boolean isNametableYSet() {
        return nametable_y;
    }

    /**
     * Return whether or not the increment_mode bit set
     *
     * @return is the increment_mode bit set
     */
    public boolean isIncrementModeSet() {
        return increment_mode;
    }

    /**
     * Return whether or not the pattern_sprite bit set
     *
     * @return is the pattern_sprite bit set
     */
    public boolean isPatternSpriteSet() {
        return pattern_sprite;
    }

    /**
     * Return whether or not the pattern_background bit set
     *
     * @return is the pattern_background bit set
     */
    public boolean isPatternBackgroundSet() {
        return pattern_background;
    }
    /**
     * Return whether or not the sprite_size bit set
     *
     * @return is the sprite_size bit set
     */
    public boolean isSpriteSizeSet() {
        return sprite_size;
    }

    /**
     * Return whether or not the enable_nmi bit set
     *
     * @return is the enable_nmi bit set
     */
    public boolean isEnableNmiSet() {
        return enable_nmi;
    }

    @Override
    public String toString() {
        return String.format("%02X ", get()) + "[" + Integer.toBinaryString(get()) + "]";
    }
}

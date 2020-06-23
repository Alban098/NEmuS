package core.ppu.registers;

/**
 * This class represent the Status Register of the 2C02 PPU
 */
public class StatusRegister {

    private boolean sprite_overflow = false;
    private boolean sprite_zero_hit = false;
    private boolean vertical_blank = false;

    /**
     * Load the Register with an 8bit value
     *
     * @param val the value to set (only the 8lsb are considered)
     */
    public void set(int val) {
        vertical_blank = (val & 0x80) == 0x80;
        sprite_zero_hit = (val & 0x40) == 0x40;
        sprite_overflow = (val & 0x20) == 0x20;
    }

    /**
     * Return the value of the Register as an 8bit value
     *
     * @return an int with the 8 lsb set accordingly
     */
    public int get() {
        int val = 0x00;
        val |= vertical_blank ? 0x80 : 0x00;
        val |= sprite_zero_hit ? 0x40 : 0x00;
        val |= sprite_overflow ? 0x20 : 0x00;
        return val & 0xFF;
    }

    /**
     * Set the sprite_overflow bit high or low
     *
     * @param sprite_overflow is the sprite_overflow bit high
     */
    public void setSpriteOverflow(boolean sprite_overflow) {
        this.sprite_overflow = sprite_overflow;
    }

    /**
     * Set the sprite_zero_hit bit high or low
     *
     * @param sprite_zero_hit is the sprite_zero_hit bit high
     */
    public void setSpriteZeroHit(boolean sprite_zero_hit) {
        this.sprite_zero_hit = sprite_zero_hit;
    }

    /**
     * Set the vertical_blank bit high or low
     *
     * @param vertical_blank is the vertical_blank bit high
     */
    public void setVerticalBlank(boolean vertical_blank) {
        this.vertical_blank = vertical_blank;
    }

    @Override
    public String toString() {
        return String.format("%02X ", get()) + "[" + Integer.toBinaryString(get()) + "]";
    }
}

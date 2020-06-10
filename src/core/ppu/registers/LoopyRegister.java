package core.ppu.registers;

/**
 * This class represent an abstraction of a PPU address used to render background
 * Credit for this goes to Loopy from the NESDev wiki
 */
public class LoopyRegister {

    private int coarse_x = 0x00;
    private int coarse_y = 0x00;
    private boolean nametable_x = false;
    private boolean nametable_y = false;
    private int fine_y = 0x00;

    /**
     * Load the Register with an 16bit value
     *
     * @param val the value to set (only the 16lsb are considered)
     */
    public void set(int val) {
        coarse_x = val & 0b0000000000011111;
        coarse_y = ((val & 0b0000001111100000) >> 5) & 0x1F;
        nametable_x = (val & 0b0000010000000000) == 0b0000010000000000;
        nametable_y = (val & 0b0000100000000000) == 0b0000100000000000;
        fine_y = ((val & 0b0111000000000000) >> 12) & 0x07;

    }

    /**
     * Return the value of the Register as a 16bit value
     *
     * @return an int with the 16 lsb set accordingly
     */
    public int get() {
        int val = 0x0000;
        val |= coarse_x & 0xFF;
        val |= (coarse_y & 0xFF) << 5;
        val |= nametable_x ? 0b0000010000000000 : 0x0000;
        val |= nametable_y ? 0b0000100000000000 : 0x0000;
        val |= (fine_y & 0x0F) << 12;
        return val & 0xFFFF;
    }

    /**
     * Return the coarse_x value as a 5bit value
     *
     * @return the coarse_x value (5 lsb)
     */
    public int getCoarseX() {
        return coarse_x;
    }

    /**
     * Set the coarse_x value of the register
     *
     * @param coarse_x the coarse_x value (only the 5 lsb are considered)
     */
    public void setCoarseX(int coarse_x) {
        this.coarse_x = coarse_x & 0xFF;
    }

    /**
     * Return the coarse_y value as a 5bit value
     *
     * @return the coarse_y value (5 lsb)
     */
    public int getCoarseY() {
        return coarse_y;
    }

    /**
     * Set the coarse_y value of the register
     *
     * @param coarse_y the coarse_y value (only the 5 lsb are considered)
     */
    public void setCoarseY(int coarse_y) {
        this.coarse_y = coarse_y & 0xFF;
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
     * Set the nametable_x bit of the register
     *
     * @param nametable_x is the nametable_x bit high
     */
    public void setNametableX(boolean nametable_x) {
        this.nametable_x = nametable_x;
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
     * Set the nametable_y bit of the register
     *
     * @param nametable_y is the nametable_y bit high
     */
    public void setNametableY(boolean nametable_y) {
        this.nametable_y = nametable_y;
    }

    /**
     * Return the fine_y value as a 3bit value
     *
     * @return the fine_y value (3 lsb)
     */
    public int getFineY() {
        return fine_y;
    }

    /**
     * Set the fine_y value of the register
     *
     * @param fine_y the fine_y value
     */
    public void setFineY(int fine_y) {
        this.fine_y = fine_y & 0x0F;
    }

    @Override
    public String toString() {
        return String.format("%04X ", get()) + "[" + Integer.toBinaryString(get()) + "]";
    }
}

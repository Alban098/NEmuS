package core.ppu.registers;

/**
 * This class represent the Mask Register of the 2C02 PPU
 */
public class MaskRegister {

    private boolean grayscale = false;
    private boolean render_background_left = false;
    private boolean render_sprite_left = false;
    private boolean render_background = false;
    private boolean render_sprites = false;
    private boolean enhance_red = false;
    private boolean enhance_green = false;
    private boolean enhance_blue = false;

    /**
     * Load the Register with an 8bit value
     *
     * @param val the value to set (only the 8lsb are considered)
     */
    public void set(int val) {
        grayscale = (val & 0x01) == 0x01;
        render_background_left = (val & 0x02) == 0x02;
        render_sprite_left = (val & 0x04) == 0x04;
        render_background = (val & 0x08) == 0x08;
        render_sprites = (val & 0x10) == 0x10;
        enhance_red = (val & 0x20) == 0x20;
        enhance_green = (val & 0x40) == 0x40;
        enhance_blue = (val & 0x80) == 0x80;
    }

    /**
     * Return the value of the Register as an 8bit value
     *
     * @return an int with the 8 lsb set accordingly
     */
    public int get() {
        int val = 0x00;
        val |= grayscale ? 0x01 : 0x00;
        val |= render_background_left ? 0x02 : 0x00;
        val |= render_sprite_left ? 0x04 : 0x00;
        val |= render_background ? 0x08 : 0x00;
        val |= render_sprites ? 0x10 : 0x00;
        val |= enhance_red ? 0x20 : 0x00;
        val |= enhance_green ? 0x40 : 0x00;
        val |= enhance_blue ? 0x80 : 0x00;
        return val;
    }

    /**
     * Return whether or not the grayscale bit set
     *
     * @return is the grayscale bit set
     */
    public boolean isGrayscaleSet() {
        return grayscale;
    }

    /**
     * Set the grayscale bit high or low
     *
     * @param grayscale is the grayscale bit high
     */
    public void setGrayscale(boolean grayscale) {
        this.grayscale = grayscale;
    }

    /**
     * Return whether or not the render_background_left bit set
     *
     * @return is the render_background_left bit set
     */
    public boolean isRenderBackgroundLeftSet() {
        return render_background_left;
    }

    /**
     * Set the render_background_left bit high or low
     *
     * @param render_background_left is the render_background_left bit high
     */
    public void setRenderBackgroundLeftSet(boolean render_background_left) {
        this.render_background_left = render_background_left;
    }

    /**
     * Return whether or not the render_sprite_left bit set
     *
     * @return is the render_sprite_left bit set
     */
    public boolean isRenderSpriteLeftSet() {
        return render_sprite_left;
    }

    /**
     * Set the render_sprite_left bit high or low
     *
     * @param render_sprite_left is the render_sprite_left bit high
     */
    public void setRenderSpriteLeft(boolean render_sprite_left) {
        this.render_sprite_left = render_sprite_left;
    }

    /**
     * Return whether or not the render_background bit set
     *
     * @return is the render_background bit set
     */
    public boolean isRenderBackgroundSet() {
        return render_background;
    }

    /**
     * Set the render_background bit high or low
     *
     * @param render_background is the render_background bit high
     */
    public void setRenderBackground(boolean render_background) {
        this.render_background = render_background;
    }

    /**
     * Return whether or not the render_sprites bit set
     *
     * @return is the render_sprites bit set
     */
    public boolean isRenderSpritesSet() {
        return render_sprites;
    }

    /**
     * Set the render_sprites bit high or low
     *
     * @param render_sprites is the render_sprites bit high
     */
    public void setRenderSprites(boolean render_sprites) {
        this.render_sprites = render_sprites;
    }

    /**
     * Return whether or not the enhance_red bit set
     *
     * @return is the enhance_red bit set
     */
    public boolean isEnhanceRedSet() {
        return enhance_red;
    }

    /**
     * Set the enhance_red bit high or low
     *
     * @param enhance_red is the enhance_red bit high
     */
    public void setEnhanceRed(boolean enhance_red) {
        this.enhance_red = enhance_red;
    }

    /**
     * Return whether or not the enhance_green bit set
     *
     * @return is the enhance_green bit set
     */
    public boolean isEnhanceGreenSet() {
        return enhance_green;
    }

    /**
     * Set the enhance_green bit high or low
     *
     * @param enhance_green is the enhance_green bit high
     */
    public void setEnhanceGreen(boolean enhance_green) {
        this.enhance_green = enhance_green;
    }

    /**
     * Return whether or not the enhance_blue bit set
     *
     * @return is the enhance_blue bit set
     */
    public boolean isEnhanceBlueSet() {
        return enhance_blue;
    }

    /**
     * Set the enhance_blue bit high or low
     *
     * @param enhance_blue is the enhance_blue bit high
     */
    public void setEnhanceBlue(boolean enhance_blue) {
        this.enhance_blue = enhance_blue;
    }

    @Override
    public String toString() {
        return String.format("%02X ", get()) + "[" + Integer.toBinaryString(get()) + "]";
    }
}

package graphics.registers;

public class MaskRegister {

    private boolean grayscale = false;
    private boolean render_background_left = false;
    private boolean render_sprite_left = false;
    private boolean render_background = false;
    private boolean render_sprites = false;
    private boolean enhance_red = false;
    private boolean enhance_green = false;
    private boolean enhance_blue = false;

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

    public boolean isGrayscale() {
        return grayscale;
    }

    public void setGrayscale(boolean grayscale) {
        this.grayscale = grayscale;
    }

    public boolean isRender_background_left() {
        return render_background_left;
    }

    public void setRender_background_left(boolean render_background_left) {
        this.render_background_left = render_background_left;
    }

    public boolean isRender_sprite_left() {
        return render_sprite_left;
    }

    public void setRender_sprite_left(boolean render_sprite_left) {
        this.render_sprite_left = render_sprite_left;
    }

    public boolean isRender_background() {
        return render_background;
    }

    public void setRender_background(boolean render_background) {
        this.render_background = render_background;
    }

    public boolean isRender_sprites() {
        return render_sprites;
    }

    public void setRender_sprites(boolean render_sprites) {
        this.render_sprites = render_sprites;
    }

    public boolean isEnhance_red() {
        return enhance_red;
    }

    public void setEnhance_red(boolean enhance_red) {
        this.enhance_red = enhance_red;
    }

    public boolean isEnhance_green() {
        return enhance_green;
    }

    public void setEnhance_green(boolean enhance_green) {
        this.enhance_green = enhance_green;
    }

    public boolean isEnhance_blue() {
        return enhance_blue;
    }

    public void setEnhance_blue(boolean enhance_blue) {
        this.enhance_blue = enhance_blue;
    }
}

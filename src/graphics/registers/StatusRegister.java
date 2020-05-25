package graphics.registers;

public class StatusRegister {

    private boolean sprite_overflow = false;
    private boolean sprite_zero_hit = false;
    private boolean vertical_blank = false;

    public void set(int val) {
        vertical_blank = (val & 0x80) == 0x80;
        sprite_zero_hit = (val & 0x40) == 0x40;
        sprite_overflow = (val & 0x20) == 0x20;
    }

    public int get() {
        int val = 0x00;
        val |= vertical_blank ? 0x80 : 0x00;
        val |= sprite_zero_hit ? 0x40 : 0x00;
        val |= sprite_zero_hit ? 0x20 : 0x00;
        return val;
    }

    public boolean isSprite_overflow() {
        return sprite_overflow;
    }

    public void setSprite_overflow(boolean sprite_overflow) {
        this.sprite_overflow = sprite_overflow;
    }

    public boolean isSprite_zero_hit() {
        return sprite_zero_hit;
    }

    public void setSprite_zero_hit(boolean sprite_zero_hit) {
        this.sprite_zero_hit = sprite_zero_hit;
    }

    public boolean isVertical_blank() {
        return vertical_blank;
    }

    public void setVertical_blank(boolean vertical_blank) {
        this.vertical_blank = vertical_blank;
    }
}

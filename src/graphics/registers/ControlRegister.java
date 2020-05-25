package graphics.registers;

public class ControlRegister {

    private boolean nametable_x = false;
    private boolean nametable_y = false;
    private boolean increment_mode = false;
    private boolean pattern_sprite = false;
    private boolean pattern_background = false;
    private boolean sprite_size = false;
    private boolean slave_mode = false;
    private boolean enable_nmi = false;

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
        return val;
    }

    public boolean isNametable_x() {
        return nametable_x;
    }

    public void setNametable_x(boolean nametable_x) {
        this.nametable_x = nametable_x;
    }

    public boolean isNametable_y() {
        return nametable_y;
    }

    public void setNametable_y(boolean nametable_y) {
        this.nametable_y = nametable_y;
    }

    public boolean isIncrement_mode() {
        return increment_mode;
    }

    public void setIncrement_mode(boolean increment_mode) {
        this.increment_mode = increment_mode;
    }

    public boolean isPattern_sprite() {
        return pattern_sprite;
    }

    public void setPattern_sprite(boolean pattern_sprite) {
        this.pattern_sprite = pattern_sprite;
    }

    public boolean isPattern_background() {
        return pattern_background;
    }

    public void setPattern_background(boolean pattern_background) {
        this.pattern_background = pattern_background;
    }

    public boolean isSprite_size() {
        return sprite_size;
    }

    public void setSprite_size(boolean sprite_size) {
        this.sprite_size = sprite_size;
    }

    public boolean isSlave_mode() {
        return slave_mode;
    }

    public void setSlave_mode(boolean slave_mode) {
        this.slave_mode = slave_mode;
    }

    public boolean isEnable_nmi() {
        return enable_nmi;
    }

    public void setEnable_nmi(boolean enable_nmi) {
        this.enable_nmi = enable_nmi;
    }
}

package graphics.registers;

public class ObjectAttribute {

    private int y = 0x00;
    private int id = 0x00;
    private int attribute = 0x00;
    private int x = 0x00;

    public void set(int val) {
        x = val & 0x000000FF;
        attribute = val & (0x0000FF00) >> 8;
        id = val & (0x00FF0000) >> 16;
        y = val & (0xFF000000) >> 24;
    }

    public int get() {
        int val = 0x00000000;
        val |= x;
        val |= attribute << 8;
        val |= id << 16;
        val |= y << 24;
        return val;
    }
}

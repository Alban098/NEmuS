package graphics.registers;

public class LoopyRegister {

    private int coarse_x = 0x00;
    private int coarse_y = 0x00;
    private boolean nametable_x = false;
    private boolean nametable_y = false;
    private int fine_y = 0x00;

    public void set(int val) {
        coarse_x =     val & 0b0000000000011111;
        coarse_y =    ((val & 0b0000001111100000) >> 5) & 0x1F;
        nametable_x = (val & 0b0000010000000000) == 0b0000010000000000;
        nametable_y = (val & 0b0000100000000000) == 0b0000100000000000;
        fine_y =      ((val & 0b0111000000000000) >> 12) & 0x07;

    }

    public int get() {
        int val = 0x0000;
        val |= coarse_x & 0x00FF;
        val |= (coarse_y & 0x00FF) << 5;
        val |= nametable_x ? 0b0000010000000000 : 0x0000;
        val |= nametable_y ? 0b0000100000000000 : 0x0000;
        val |= (fine_y & 0x000F) << 12;
        return val;
    }

    public int getCoarse_x() {
        return coarse_x;
    }

    public void setCoarse_x(int coarse_x) {
        this.coarse_x = coarse_x  & 0x00FF;
    }

    public int getCoarse_y() {
        return coarse_y;
    }

    public void setCoarse_y(int coarse_y) {
        this.coarse_y = coarse_y & 0x00FF;
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

    public int getFine_y() {
        return fine_y;
    }

    public void setFine_y(int fine_y) {
        this.fine_y = fine_y & 0x000F;
    }

    @Override
    public String toString() {
        return String.format("%04X", get()) + "[" + Integer.toBinaryString(get()) + "]";
    }
}

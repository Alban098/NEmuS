package graphics.registers;

public class ObjectAttribute {

    private int y = 0x00;
    private int id = 0x00;
    private int attribute = 0x00;
    private int x = 0x00;

    public void clear(int val) {
        y = val;
        id = val;
        attribute = val;
        x = val;
    }

    public void set(ObjectAttribute o) {
        y = o.y;
        x = o.x;
        attribute = o.attribute;
        id = o.id;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}

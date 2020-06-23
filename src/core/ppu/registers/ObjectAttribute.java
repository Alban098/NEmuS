package core.ppu.registers;

/**
 * This class represent on Object Attribute Entry
 * is basically represents a sprites
 */
public class ObjectAttribute {

    private int y = 0x00;
    private int id = 0x00;
    private int attribute = 0x00;
    private int x = 0x00;

    /**
     * Set all field to a specified val
     *
     * @param val the val to fill with
     */
    public void clear(int val) {
        y = val;
        id = val;
        attribute = val;
        x = val;
    }

    /**
     * Copy the value of another ObjectAttribute
     *
     * @param o the ObjectAttribute to copy
     */
    public void set(ObjectAttribute o) {
        y = o.y;
        x = o.x;
        attribute = o.attribute;
        id = o.id;
    }

    /**
     * Return the y coordinate of the ObjectAttribute
     * which represent the y coordinate of the top-left corner of the sprite
     *
     * @return the y coordinate of the ObjectAttribute
     */
    public int getY() {
        return y & 0xFF;
    }

    /**
     * Set the y coordinate of the ObjectAttribute
     * which represent the y coordinate of the top-left corner of the sprite
     *
     * @param y the new y coordinate of the ObjectAttribute
     */
    public void setY(int y) {
        this.y = y & 0xFF;
    }

    /**
     * Return the id the the ObjectAttribute
     * which is basically the Tile ID used to index the Tile in the Pattern Tables
     *
     * @return the id of the ObjectAttribute
     */
    public int getId() {
        return id & 0xFF;
    }

    /**
     * Set the id the the ObjectAttribute
     * which is basically the Tile ID used to index the Tile in the Pattern Tables
     *
     * @param id the new id of the ObjectAttribute
     */
    public void setId(int id) {
        this.id = id & 0xFF;
    }

    /**
     * Return the attribute of the ObjectAttribute
     * which indicate the palette and priority of the sprite and whether or not the sprite is flipped horizontally or vertically
     *
     * @return the attribute of the ObjectAttribute
     */
    public int getAttribute() {
        return attribute & 0xFF;
    }

    /**
     * Return the attribute of the ObjectAttribute
     * which indicate the palette and priority of the sprite and whether or not the sprite is flipped horizontally or vertically
     *
     * @param attribute the new attribute of the ObjectAttribute
     */
    public void setAttribute(int attribute) {
        this.attribute = attribute & 0xFF;
    }

    /**
     * Return the x coordinate of the ObjectAttribute
     * which represent the x coordinate of the top-left corner of the sprite
     *
     * @return the x coordinate of the ObjectAttribute
     */
    public int getX() {
        return x & 0xFF;
    }

    /**
     * Set the x coordinate of the ObjectAttribute
     * which represent the x coordinate of the top-left corner of the sprite
     *
     * @param x the new x coordinate of the ObjectAttribute
     */
    public void setX(int x) {
        this.x = x & 0xFF;
    }
}

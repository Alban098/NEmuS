package core.ppu.registers;

/**
 * This class represent on Object Attribute Entry
 * is basically represents a sprites
 */
public class ObjectAttribute {

    private short y = 0x00;
    private short id = 0x00;
    private short attribute = 0x00;
    private short x = 0x00;

    /**
     * Set all field to a specified val
     *
     * @param val the val to fill with
     */
    public void clear(byte val) {
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
    public short getY() {
        return y;
    }

    /**
     * Set the y coordinate of the ObjectAttribute
     * which represent the y coordinate of the top-left corner of the sprite
     *
     * @param y the new y coordinate of the ObjectAttribute
     */
    public void setY(short y) {
        this.y = y;
    }

    /**
     * Return the id the the ObjectAttribute
     * which is basically the Tile ID used to index the Tile in the Pattern Tables
     *
     * @return the id of the ObjectAttribute
     */
    public short getId() {
        return id;
    }

    /**
     * Set the id the the ObjectAttribute
     * which is basically the Tile ID used to index the Tile in the Pattern Tables
     *
     * @param id the new id of the ObjectAttribute
     */
    public void setId(short id) {
        this.id = id;
    }

    /**
     * Return the attribute of the ObjectAttribute
     * which indicate the palette and priority of the sprite and whether or not the sprite is flipped horizontally or vertically
     *
     * @return the attribute of the ObjectAttribute
     */
    public short getAttribute() {
        return attribute;
    }

    /**
     * Return the attribute of the ObjectAttribute
     * which indicate the palette and priority of the sprite and whether or not the sprite is flipped horizontally or vertically
     *
     * @param attribute the new attribute of the ObjectAttribute
     */
    public void setAttribute(short attribute) {
        this.attribute = attribute;
    }

    /**
     * Return the x coordinate of the ObjectAttribute
     * which represent the x coordinate of the top-left corner of the sprite
     *
     * @return the x coordinate of the ObjectAttribute
     */
    public short getX() {
        return x;
    }

    /**
     * Set the x coordinate of the ObjectAttribute
     * which represent the x coordinate of the top-left corner of the sprite
     *
     * @param x the new x coordinate of the ObjectAttribute
     */
    public void setX(short x) {
        this.x = x;
    }

    // ======================================= Savestates Methods ======================================= //

    /**
     * Return a dump of the Object Attribute
     * that can be restored later
     *
     * @return a byte[4] containing the Object Attribute
     */
    public byte[] dump() {
        return new byte[]{(byte) x, (byte) attribute, (byte) id, (byte) y};
    }
}

package gui.inputs;

/**
 * THis class represent a Key or Gamepad Button and contains its internal ID
 * along with its name
 */
public class KeyTuple {

    public final int keyID;
    public final String keyName;

    KeyTuple(int keyID, String keyName) {
        this.keyID = keyID;
        this.keyName = keyName;
    }
}

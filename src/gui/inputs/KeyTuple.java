package gui.inputs;

/**
 * THis class represent a Key or Gamepad Button and contains its internal ID
 * along with its name
 */
public class KeyTuple {

    public final int keyID;
    public final String keyName;

    /**
     * Create a new KeyTuple
     *
     * @param keyID the key ID
     * @param keyName the key name
     */
    KeyTuple(int keyID, String keyName) {
        this.keyID = keyID;
        this.keyName = keyName;
    }
}

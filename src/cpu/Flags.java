package cpu;

public enum Flags {

    C((short)(1)),
    Z((short)(1 << 1)),
    I((short)(1 << 2)),
    D((short)(1 << 3)),
    B((short)(1 << 4)),
    U((short)(1 << 5)),
    V((short)(1 << 6)),
    N((short)(1 << 7));

    short value;

    Flags(short value) {
        this.value = value;
    }
}
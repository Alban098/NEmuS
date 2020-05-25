package cpu;

public enum Flags {

    C((int)(1)),
    Z((int)(1 << 1)),
    I((int)(1 << 2)),
    D((int)(1 << 3)),
    B((int)(1 << 4)),
    U((int)(1 << 5)),
    V((int)(1 << 6)),
    N((int)(1 << 7));

    int value;

    Flags(int value) {
        this.value = value;
    }
}
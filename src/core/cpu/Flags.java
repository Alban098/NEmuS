package core.cpu;

/**
 * Represents the flags of the CPU
 */
public enum Flags {

    C(1),       // Carry
    Z(1 << 1),  // Zero
    I(1 << 2),  // Interrupt Disable
    D(1 << 3),  // Decimal Mode (Unused)
    B(1 << 4),  // B (Irrelevant for our implementation)
    U(1 << 5),  // Unused
    V(1 << 6),  // Overflow
    N(1 << 7);  // Negative

    final int value;

    Flags(int value) {
        this.value = value;
    }
}
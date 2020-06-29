package core.cpu;

public enum OPCode {
    
    ADC(0),
    AND(0),
    ASL(0),
    BCC(OPCode.BRANCH),
    BCS(OPCode.BRANCH),
    BEQ(OPCode.BRANCH),
    BIT(0),
    BMI(OPCode.BRANCH),
    BNE(OPCode.BRANCH),
    BPL(OPCode.BRANCH),
    BRK(OPCode.BRANCH),
    BVC(OPCode.BRANCH),
    BVS(OPCode.BRANCH),
    CLC(0),
    CLD(0),
    CLI(0),
    CLV(0),
    CMP(0),
    CPX(0),
    CPY(0),
    DEC(0),
    DEX(0),
    DEY(0),
    EOR(0),
    INC(0),
    INX(0),
    INY(0),
    JMP(OPCode.BRANCH),
    JSR(OPCode.BRANCH),
    LDA(0),
    LDX(0),
    LDY(0),
    LSR(0),
    NOP(0),
    ORA(0),
    PHA(0),
    PHP(0),
    PLA(0),
    PLP(0),
    ROL(0),
    ROR(0),
    RTI(OPCode.BRANCH),
    RTS(OPCode.BRANCH),
    SBC(0),
    SEC(0),
    SED(0),
    SEI(0),
    STA(0),
    STX(0),
    STY(0),
    TAX(0),
    TAY(0),
    TSX(0),
    TXA(0),
    TXS(0),
    TYA(0),
    XXX(OPCode.ILLEGAL),
    ASO(OPCode.ILLEGAL),
    RLA(OPCode.ILLEGAL),
    LSE(OPCode.ILLEGAL),
    RRA(OPCode.ILLEGAL),
    AXS(OPCode.ILLEGAL),
    LAX(OPCode.ILLEGAL),
    DCM(OPCode.ILLEGAL),
    INS(OPCode.ILLEGAL),
    ALR(OPCode.ILLEGAL),
    ARR(OPCode.ILLEGAL),
    XAA(OPCode.ILLEGAL),
    OAL(OPCode.ILLEGAL),
    SAX(OPCode.ILLEGAL),
    SKB(OPCode.ILLEGAL),
    SKW(OPCode.ILLEGAL),
    HLT(OPCode.ILLEGAL),
    TAS(OPCode.ILLEGAL),
    SAY(OPCode.ILLEGAL),
    XAS(OPCode.ILLEGAL),
    AXA(OPCode.ILLEGAL),
    ANC(OPCode.ILLEGAL),
    LAS(OPCode.ILLEGAL);

    public static final int ILLEGAL = 2;
    public static final int BRANCH = 1;
    public final int type;
    
    OPCode(int type) {
        this.type = type;
    }
}

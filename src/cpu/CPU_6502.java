package cpu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CPU_6502 {

    private Bus bus;

    private short a = 0x00;
    private short x = 0x00;
    private short y = 0x00;
    private short stkp = 0x00;
    private short status = 0x00;
    private int pc = 0x0000;
    
    private short tmp = 0x0000;

    private short fetched = 0x00;
    private short opcode = 0x00;
    private short cycles = 0x00;
    private int addr_abs = 0x0000;
    private int addr_rel = 0x0000;

    private List<Instruction> opcodes;

    public CPU_6502() {
        opcodes = new ArrayList<>();
        opcodes.add(new Instruction("BRK", "IMM", (short)7) { public short operate() { return brk(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("ORA", "IZX", (short)6) { public short operate() { return ora(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)3) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ZP0", (short)3) { public short operate() { return ora(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ASL", "ZP0", (short)5) { public short operate() { return asl(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("PHP", "IMP", (short)3) { public short operate() { return php(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "IMM", (short)2) { public short operate() { return ora(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("ASL", "IMP", (short)2) { public short operate() { return asl(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABS", (short)4) { public short operate() { return ora(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("ASL", "ABS", (short)6) { public short operate() { return asl(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BPL", "REL", (short)2) { public short operate() { return bpl(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("ORA", "IZY", (short)5) { public short operate() { return ora(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ZPX", (short)4) { public short operate() { return ora(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ASL", "ZPX", (short)6) { public short operate() { return asl(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLC", "IMP", (short)2) { public short operate() { return clc(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABY", (short)4) { public short operate() { return ora(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABX", (short)4) { public short operate() { return ora(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("ASL", "ABX", (short)7) { public short operate() { return asl(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("JSR", "ABS", (short)6) { public short operate() { return jsr(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("AND", "IZX", (short)6) { public short operate() { return and(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BIT", "ZP0", (short)3) { public short operate() { return bit(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("AND", "ZP0", (short)3) { public short operate() { return and(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ROL", "ZP0", (short)5) { public short operate() { return rol(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("PLP", "IMP", (short)4) { public short operate() { return plp(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "IMM", (short)2) { public short operate() { return and(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("ROL", "IMP", (short)2) { public short operate() { return rol(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BIT", "ABS", (short)4) { public short operate() { return bit(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("AND", "ABS", (short)4) { public short operate() { return and(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("ROL", "ABS", (short)6) { public short operate() { return rol(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BMI", "REL", (short)2) { public short operate() { return bmi(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("AND", "IZY", (short)5) { public short operate() { return and(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ZPX", (short)4) { public short operate() { return and(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ROL", "ZPX", (short)6) { public short operate() { return rol(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SEC", "IMP", (short)2) { public short operate() { return sec(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ABY", (short)4) { public short operate() { return and(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ABX", (short)4) { public short operate() { return and(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("ROL", "ABX", (short)7) { public short operate() { return rol(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("RTI", "IMP", (short)6) { public short operate() { return rti(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "IZX", (short)6) { public short operate() { return eor(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)3) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ZP0", (short)3) { public short operate() { return eor(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LSR", "ZP0", (short)5) { public short operate() { return lsr(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("PHA", "IMP", (short)3) { public short operate() { return pha(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "IMM", (short)2) { public short operate() { return eor(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("LSR", "IMP", (short)2) { public short operate() { return lsr(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("JMP", "ABS", (short)3) { public short operate() { return jmp(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("EOR", "ABS", (short)4) { public short operate() { return eor(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("LSR", "ABS", (short)6) { public short operate() { return lsr(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BVC", "REL", (short)2) { public short operate() { return bvc(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("EOR", "IZY", (short)5) { public short operate() { return eor(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ZPX", (short)4) { public short operate() { return eor(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LSR", "ZPX", (short)6) { public short operate() { return lsr(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLI", "IMP", (short)2) { public short operate() { return cli(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ABY", (short)4) { public short operate() { return eor(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ABX", (short)4) { public short operate() { return eor(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("LSR", "ABX", (short)7) { public short operate() { return lsr(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("RTS", "IMP", (short)6) { public short operate() { return rts(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "IZX", (short)6) { public short operate() { return adc(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)3) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ZP0", (short)3) { public short operate() { return adc(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ROR", "ZP0", (short)5) { public short operate() { return ror(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("PLA", "IMP", (short)4) { public short operate() { return pla(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "IMM", (short)2) { public short operate() { return adc(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("ROR", "IMP", (short)2) { public short operate() { return ror(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("JMP", "IND", (short)5) { public short operate() { return jmp(); } public short addrmode() { return ind(); }});
        opcodes.add(new Instruction("ADC", "ABS", (short)4) { public short operate() { return adc(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("ROR", "ABS", (short)6) { public short operate() { return ror(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BVS", "REL", (short)2) { public short operate() { return bvs(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("ADC", "IZY", (short)5) { public short operate() { return adc(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ZPX", (short)4) { public short operate() { return adc(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ROR", "ZPX", (short)6) { public short operate() { return ror(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SEI", "IMP", (short)2) { public short operate() { return sei(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ABY", (short)4) { public short operate() { return adc(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ABX", (short)4) { public short operate() { return adc(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("ROR", "ABX", (short)7) { public short operate() { return ror(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "IZX", (short)6) { public short operate() { return sta(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ZP0", (short)3) { public short operate() { return sty(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("STA", "ZP0", (short)3) { public short operate() { return sta(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("STX", "ZP0", (short)3) { public short operate() { return stx(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)3) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("DEY", "IMP", (short)2) { public short operate() { return dey(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("TXA", "IMP", (short)2) { public short operate() { return txa(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ABS", (short)4) { public short operate() { return sty(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("STA", "ABS", (short)4) { public short operate() { return sta(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("STX", "ABS", (short)4) { public short operate() { return stx(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BCC", "REL", (short)2) { public short operate() { return bcc(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("STA", "IZY", (short)6) { public short operate() { return sta(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ZPX", (short)4) { public short operate() { return sty(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("STA", "ZPX", (short)4) { public short operate() { return sta(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("STX", "ZPY", (short)4) { public short operate() { return stx(); } public short addrmode() { return zpy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("TYA", "IMP", (short)2) { public short operate() { return tya(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "ABY", (short)5) { public short operate() { return sta(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("TXS", "IMP", (short)2) { public short operate() { return txs(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "ABX", (short)5) { public short operate() { return sta(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "IMM", (short)2) { public short operate() { return ldy(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("LDA", "IZX", (short)6) { public short operate() { return lda(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("LDX", "IMM", (short)2) { public short operate() { return ldx(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ZP0", (short)3) { public short operate() { return ldy(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LDA", "ZP0", (short)3) { public short operate() { return lda(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LDX", "ZP0", (short)3) { public short operate() { return ldx(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)3) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("TAY", "IMP", (short)2) { public short operate() { return tay(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDA", "IMM", (short)2) { public short operate() { return lda(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("TAX", "IMP", (short)2) { public short operate() { return tax(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ABS", (short)4) { public short operate() { return ldy(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("LDA", "ABS", (short)4) { public short operate() { return lda(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("LDX", "ABS", (short)4) { public short operate() { return ldx(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BCS", "REL", (short)2) { public short operate() { return bcs(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("LDA", "IZY", (short)5) { public short operate() { return lda(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ZPX", (short)4) { public short operate() { return ldy(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LDA", "ZPX", (short)4) { public short operate() { return lda(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LDX", "ZPY", (short)4) { public short operate() { return ldx(); } public short addrmode() { return zpy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLV", "IMP", (short)2) { public short operate() { return clv(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDA", "ABY", (short)4) { public short operate() { return lda(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("TSX", "IMP", (short)2) { public short operate() { return tsx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ABX", (short)4) { public short operate() { return ldy(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("LDA", "ABX", (short)4) { public short operate() { return lda(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("LDX", "ABY", (short)4) { public short operate() { return ldx(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "IMM", (short)2) { public short operate() { return cpy(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("CMP", "IZX", (short)6) { public short operate() { return cmp(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "ZP0", (short)3) { public short operate() { return cpy(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("CMP", "ZP0", (short)3) { public short operate() { return cmp(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("DEC", "ZP0", (short)5) { public short operate() { return dec(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("INY", "IMP", (short)2) { public short operate() { return iny(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "IMM", (short)2) { public short operate() { return cmp(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("DEX", "IMP", (short)2) { public short operate() { return dex(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "ABS", (short)4) { public short operate() { return cpy(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("CMP", "ABS", (short)4) { public short operate() { return cmp(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("DEC", "ABS", (short)6) { public short operate() { return dec(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BNE", "REL", (short)2) { public short operate() { return bne(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("CMP", "IZY", (short)5) { public short operate() { return cmp(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ZPX", (short)4) { public short operate() { return cmp(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("DEC", "ZPX", (short)6) { public short operate() { return dec(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLD", "IMP", (short)2) { public short operate() { return cld(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ABY", (short)4) { public short operate() { return cmp(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("NOP", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ABX", (short)4) { public short operate() { return cmp(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("DEC", "ABX", (short)7) { public short operate() { return dec(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "IMM", (short)2) { public short operate() { return cpx(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("SBC", "IZX", (short)6) { public short operate() { return sbc(); } public short addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "ZP0", (short)3) { public short operate() { return cpx(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("SBC", "ZP0", (short)3) { public short operate() { return sbc(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("INC", "ZP0", (short)5) { public short operate() { return inc(); } public short addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", (short)5) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("INX", "IMP", (short)2) { public short operate() { return inx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "IMM", (short)2) { public short operate() { return sbc(); } public short addrmode() { return imm(); }});
        opcodes.add(new Instruction("NOP", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return sbc(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "ABS", (short)4) { public short operate() { return cpx(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("SBC", "ABS", (short)4) { public short operate() { return sbc(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("INC", "ABS", (short)6) { public short operate() { return inc(); } public short addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("BEQ", "REL", (short)2) { public short operate() { return beq(); } public short addrmode() { return rel(); }});
        opcodes.add(new Instruction("SBC", "IZY", (short)5) { public short operate() { return sbc(); } public short addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", (short)2) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)8) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ZPX", (short)4) { public short operate() { return sbc(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("INC", "ZPX", (short)6) { public short operate() { return inc(); } public short addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)6) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SED", "IMP", (short)2) { public short operate() { return sed(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ABY", (short)4) { public short operate() { return sbc(); } public short addrmode() { return aby(); }});
        opcodes.add(new Instruction("NOP", "IMP", (short)2) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)4) { public short operate() { return nop(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ABX", (short)4) { public short operate() { return sbc(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("INC", "ABX", (short)7) { public short operate() { return inc(); } public short addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", (short)7) { public short operate() { return xxx(); } public short addrmode() { return imp(); }});
    }

    public Map<Integer, String> disassemble(int start, int end) {
        int addr = start;
        int line_addr;
        short value, low, high;

        Map<Integer, String> code = new TreeMap<>();

        while (addr < end) {
            line_addr = addr;
            String line = String.format("$%04X: ", addr);
            short opcode = bus.read(addr, true);
            addr++;
            Instruction instr = opcodes.get(opcode);
            line += instr.name + " ";
            switch (instr.addr_mode) {
                case "IMP":
                    line += "{IMP}";
                    break;
                case "IMM":
                    value = bus.read(addr, true);
                    addr++;
                    line += String.format("#$%02X {IMM}", value);
                    break;
                case "ZP0":
                    low = bus.read(addr, true);
                    addr++;
                    line += String.format("$%02X {ZP0}", low);
                    break;
                case "ZPX":
                    low = bus.read(addr, true);
                    addr++;
                    line += String.format("$%02X, X {ZPX}", low);
                    break;
                case "ZPY":
                    low = bus.read(addr, true);
                    addr++;
                    line += String.format("$%02X, Y {ZPY}", low);
                    break;
                case "IZX":
                    low = bus.read(addr, true);
                    addr++;
                    line += String.format("($%02X, X) {IZX}", low);
                    break;
                case "IZY":
                    low = bus.read(addr, true);
                    addr++;
                    line +=  String.format("($%02X), Y {IZY}", low);
                    break;
                case "ABS":
                    low = bus.read(addr, true);
                    addr++;
                    high = bus.read(addr, true);
                    addr++;
                    line += String.format("$%04X {ABS}", (high << 8) | low);
                    break;
                case "ABX":
                    low = bus.read(addr, true);
                    addr++;
                    high = bus.read(addr, true);
                    addr++;
                    line += String.format("$%04X, X {ABX}", (high << 8) | low);
                    break;
                case "ABY":
                    low = bus.read(addr, true);
                    addr++;
                    high = bus.read(addr, true);
                    addr++;
                    line += String.format("$%04X, Y {ABY}", (high << 8) | low);
                    break;
                case "IND":
                    low = bus.read(addr, true);
                    addr++;
                    high = bus.read(addr, true);
                    addr++;
                    line += String.format("($%04X) {IND}", (high << 8) | low);
                    break;
                case "REL":
                    value = bus.read(addr, true);
                    addr++;
                    line += String.format("$%02X ", value) + String.format("[$%04X] {IND}", addr + value);
            }
            code.put(line_addr, line);
        }
        return code;
    }

    void connectBus(Bus bus) {
        this.bus = bus;
    }

    private void write(int addr, short data) {
        bus.write(addr, data);
    }

    private short read(int addr) {
        return bus.read(addr);
    }

    public boolean getFlag(Flags flag) {
        return (status & flag.value) == flag.value;
    }

    private void setFlag(Flags flag, boolean value) {
        if (value)
            status |= flag.value;
        else
            status &= ~flag.value;
    }

    // ================================= Addressing Modes =================================
    private short imp() {
        fetched = a;
        return 0;
    }

    private short zp0() {
        addr_abs = read(pc);
        addr_abs &= 0x00FF;
        pc++;
        return 0;
    }

    private short zpy() {
        addr_abs = read(pc) + y;
        addr_abs &= 0x00FF;
        pc++;
        return 0;
    }

    private short abs() {
        short low = read(pc);
        pc++;
        short high = read(pc);
        pc++;
        addr_abs =  (high << 8) | low;
        return 0;
    }

    private short aby() {
        short low = read(pc);
        pc++;
        short high = read(pc);
        pc++;
        addr_abs = (high << 8 | low) + y;
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    private short izx() {
        short ptr = read(pc);
        pc++;
        short low = read((ptr + x) & 0x00FF);
        short high = read((ptr + x + 1) & 0x00FF);
        addr_abs =  (high << 8) | low;
        return 0;
    }

    private short imm() {
        addr_abs = pc;
        pc++;
        return 0;
    }

    private short zpx() {
        addr_abs = (short) (read(pc) + x);
        addr_abs &= 0x00FF;
        pc++;
        return 0;
    }

    private short rel() {
        addr_rel = read(pc);
        pc++;
        if ((addr_rel & 0x80) != 0x0000)
            addr_rel |= 0xFFFFFF00;
        return 0;
    }

    private short abx() {
        short low = read(pc);
        pc++;
        short high = read(pc);
        pc++;
        addr_abs =  ((high << 8) | low) + x;
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    private short ind() {
        short low = read(pc);
        pc++;
        short high = read(pc);
        pc++;
        int ptr =  (high << 8) | low;

        if (low == 0xFF)
            addr_abs =  (read((ptr & 0xFF00) << 8)) | read(ptr);
        else
            addr_abs = (read(ptr + 1) << 8) | read(ptr);
        return 0;
    }

    private short izy() {
        short ptr = read(pc);
        pc++;
        short low = read(ptr & 0x00FF);
        short high = read((ptr  + 1) & 0x00FF);
        addr_abs = (high << 8) | low + y;
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    // ====================================================================================

    // ====================================== Opcodes ======================================
    private short adc() {
        fetch();
        tmp = (short) (a + fetched + (getFlag(Flags.C) ? 0x1 : 0x0));
        setFlag(Flags.C, tmp > 0x00FF);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        setFlag(Flags.V, ((~(a ^ fetched) & (a ^ tmp)) & 0x0080) == 0x0080);
        a = (short) (tmp & 0x00FF);
        return 1;
    }

    private short and() {
        fetch();
        a = (short) (a & fetched);
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 1;
    }

    private short asl() {
        fetch();
        tmp = (short) (fetched << 1);
        setFlag(Flags.C, (tmp & 0xFF00) > 0);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a = (short) (tmp & 0x00FF);
        else
            write(addr_abs, (short) (tmp & 0x00FF));
        return 0;
    }

    private short bcc() {
        if (!getFlag(Flags.C)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short bcs() {
        if (getFlag(Flags.C)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short beq() {
        if (getFlag(Flags.Z)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short bit() {
        fetch();
        tmp = (short) (a & fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (fetched & (1 << 7)) == (1 << 7));
        setFlag(Flags.V, (fetched & (1 << 6)) == (1 << 6));
        return 0;
    }

    private short bmi() {
        if (getFlag(Flags.N)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short bne() {
         if (!getFlag(Flags.Z)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short bpl() {
        if (!getFlag(Flags.N)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short brk() {
        pc++;
        setFlag(Flags.I, true);
        write(0x0100 + stkp, (short) ((pc >> 8) & 0x00FF));
        stkp--;
        write(0x0100 + stkp, (short) (pc & 0x00FF));
        stkp--;
        setFlag(Flags.B, true);
        write(0x0100 + stkp, status);
        stkp--;
        setFlag(Flags.B, false);
        pc = read(0xFFFE) | (read(0xFFFF) << 8);
        return 0;
    }

    private short bvc() {
        if (!getFlag(Flags.V)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short bvs() {
        if (getFlag(Flags.V)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs;
        }
        return 0;
    }

    private short clc() {
        setFlag(Flags.C, false);
        return 0;
    }

    private short cld() {
        setFlag(Flags.D, false);
        return 0;
    }

    private short cli() {
        setFlag(Flags.I, false);
        return 0;
    }

    private short clv() {
        setFlag(Flags.V, false);
        return 0;
    }

    private short cmp() {
        fetch();
        tmp = (short) (a - fetched);
        setFlag(Flags.C, a >= fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 1;
    }

    private short cpx() {
        fetch();
        tmp = (short) (x - fetched);
        setFlag(Flags.C, x >= fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private short cpy() {
        fetch();
        tmp = (short) (y - fetched);
        setFlag(Flags.C, y >= fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private short dec() {
        fetch();
        tmp = (short) (fetched - 1);
        write(addr_abs, (short) (tmp & 0x00FF));
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private short dex() {
        x--;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 0;
    }

    private short dey() {
        y--;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 0;
    }

    private short eor() {
        fetch();
        a = (short) (a ^ fetched);
        setFlag(Flags.Z, (a & 0x00FF) == 0x0000);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private short inc() {
        fetch();
        tmp = (short) (fetched + 1);
        write(addr_abs, (short) (tmp & 0x00FF));
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private short inx() {
        x++;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 0;
    }

    private short iny() {
        y++;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 0;
    }

    private short jmp() {
        pc = addr_abs;
        return 0;
    }

    private short jsr() {
        pc--;
        write(0x0100 + stkp, (short) ((pc >> 8) & 0x00FF));
        stkp--;
        write(0x0100 + stkp, (short) (pc & 0x00FF));
        stkp--;
        pc = addr_abs;
        return 0;
    }

    private short lda() {
        fetch();
        a = fetched;
        setFlag(Flags.Z, (a & 0x00FF) == 0x0000);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private short ldx() {
        fetch();
        x = fetched;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 1;
    }

    private short ldy() {
        fetch();
        y = fetched;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 1;
    }

    private short lsr() {
        fetch();
        setFlag(Flags.C, (fetched & 0x0001) == 0x0001);
        tmp = (short) (fetched >> 1);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a = (short) (tmp & 0x00FF);
        else
            write(addr_abs, (short) (tmp & 0x00FF));
        return 0;
    }

    private short nop() {
        switch(opcode) {
            case 0x1C:
            case 0x3C:
            case 0x5C:
            case 0x7C:
            case 0xDC:
            case 0xFC:
                return 1;
        }
        return 0;
    }

    private short ora() {
        fetch();
        a = (short) (a | fetched);
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private short pha() {
        write(0x0100 + stkp, a);
        stkp--;
        return 0;
    }

    private short php() {
        write(0x0100 + stkp, (short) (status | Flags.U.value | Flags.B.value));
        setFlag(Flags.B, false);
        setFlag(Flags.U, false);
        stkp--;
        return 0;
    }

    private short pla() {
        stkp++;
        a = read(0x0100 + stkp);
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private short plp() {
        stkp++;
        status = read(0x0100 + stkp);
        setFlag(Flags.U, true);
        return 0;
    }

    private short rol() {
        fetch();
        tmp = (short) ((getFlag(Flags.C) ? 1 : 0) | fetched << 1);
        setFlag(Flags.C, (fetched & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x00);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a = (short) (tmp & 0x00FF);
        else
            write(addr_abs, (short) (tmp & 0x00FF));
        return 0;
    }

    private short ror() {
        fetch();
        tmp = (short) ((getFlag(Flags.C) ? 1 << 7 : 0) | fetched >> 1);
        setFlag(Flags.C, (fetched & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x00);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a = (short) (tmp & 0x00FF);
        else
            write(addr_abs, (short) (tmp & 0x00FF));
        return 0;
    }

    private short rti() {
        stkp++;
        status = read(0x0100 + stkp);
        status &= ~Flags.B.value;
        status &= ~Flags.U.value;

        stkp++;
        pc = read(0x0100 + stkp);
        stkp++;
        pc |= read(0x0100 + stkp) << 8;
        return 0;
    }

    private short rts() {
        stkp++;
        pc = read(0x0100 + stkp);
        stkp++;
        pc |= read(0x0100 + stkp) << 8;
        return 0;
    }

    private short sbc() {
        fetch();
        short value = (short) (fetched ^ 0x00FF);

        tmp = (short) (a + value + (getFlag(Flags.C) ? 0x1 : 0x0));
        setFlag(Flags.C, tmp > 0x00FF);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        setFlag(Flags.V, ((tmp ^ a) & (tmp ^ value) & 0x0080) == 0x0080);
        a = (short) (tmp & 0x00FF);
        return 1;
    }

    private short sec() {
        setFlag(Flags.C, true);
        return 0;
    }

    private short sed() {
        setFlag(Flags.D, true);
        return 0;
    }

    private short sei() {
        setFlag(Flags.I, true);
        return 0;
    }

    private short sta() {
        write(addr_abs, a);
        return 0;
    }

    private short stx() {
        write(addr_abs, x);
        return 0;
    }

    private short sty() {
        write(addr_abs, y);
        return 0;
    }

    private short tax() {
        x = a;
        setFlag(Flags.Z, x == 0x00);
        setFlag(Flags.N, (x & 0x80) != 0x00);
        return 0;
    }

    private short tay() {
        y = a;
        setFlag(Flags.Z, y == 0x00);
        setFlag(Flags.N, (y & 0x80) != 0x00);
        return 0;
    }

    private short tsx() {
        x = stkp;
        setFlag(Flags.Z, x == 0x00);
        setFlag(Flags.N, (x & 0x80) != 0x00);
        return 0;
    }

    private short txa() {
        a = x;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private short txs() {
        stkp = x;
        return 0;
    }

    private short tya() {
        a = y;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private short xxx() {
        return 0;
    }

    // =====================================================================================

    public void clock() {
        if (cycles == 0) {
            opcode = read(pc);
            pc++;

            Instruction instr = opcodes.get(opcode);
            cycles = instr.cycles;
            short additional_cycle_1 = instr.addrmode();
            short additional_cycle_2 = instr.operate();
            cycles += (additional_cycle_1 & additional_cycle_2);
        }
        cycles--;
    }

    public void reset() {
        a = 0x00;
        x = 0x00;
        y = 0x00;
        stkp = 0xFD;
        status = Flags.U.value;

        addr_abs = 0xFFFC;
        short low = read(addr_abs);
        short high = read(addr_abs + 1);
        pc = high << 8 | low;

        addr_rel = 0x0000;
        addr_abs = 0x0000;
        fetched = 0x00;

        cycles = 8;
    }

    public void irq() {
        if (!getFlag(Flags.I)) {
            write(0x0100 + stkp, (short) ((pc >> 8) & 0x00FF));
            stkp--;
            write(0x0100 + stkp, (short) (pc & 0x00FF));
            stkp--;

            setFlag(Flags.B, false);
            setFlag(Flags.U, true);
            setFlag(Flags.I, true);
            write(0x0100 + stkp, status);
            stkp--;

            addr_abs = 0xFFFE;
            short low = read(addr_abs);
            short high = read(addr_abs + 1);
            pc = high << 8 | low;

            cycles = 7;
        }
    }

    public void nmi() {
        write(0x0100 + stkp, (short) ((pc >> 8) & 0x00FF));
        stkp--;
        write(0x0100 + stkp, (short) (pc & 0x00FF));
        stkp--;

        setFlag(Flags.B, false);
        setFlag(Flags.U, true);
        setFlag(Flags.I, true);
        write(0x0100 + stkp, status);
        stkp--;

        addr_abs = 0xFFFA;
        short low = read(addr_abs);
        short high = read(addr_abs + 1);
        pc = high << 8 | low;

        cycles = 8;
    }

    private short fetch() {
        if (!opcodes.get(opcode).addr_mode.equals("IMP"))
            fetched = read(addr_abs);
        return fetched;
    }

    public boolean complete() {
        return cycles == 0;
    }

    public short getA() {
        return a;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public short getStkp() {
        return stkp;
    }

    public short getStatus() {
        return status;
    }

    public int getPc() {
        return pc;
    }
}

abstract class Instruction {

    String name;
    String addr_mode;
    short cycles;
    public abstract short operate();
    public abstract short addrmode();

    Instruction(String name, String addr_mode, short cycles) {
        this.name = name;
        this.addr_mode = addr_mode;
        this.cycles = cycles;
    }
}

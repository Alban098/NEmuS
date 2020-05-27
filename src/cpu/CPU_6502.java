package cpu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CPU_6502 {

    private Bus bus;

    private int a = 0x00;
    private int x = 0x00;
    private int y = 0x00;
    private int stkp = 0x00;
    private int status = 0x00;
    private int pc = 0x0000;
    
    private int tmp = 0x0000;

    private int fetched = 0x00;
    private int opcode = 0x00;
    private int cycles = 0x00;
    private int addr_abs = 0x0000;
    private int addr_rel = 0x0000;

    private List<Instruction> opcodes;

    public CPU_6502() {
        opcodes = new ArrayList<>();
        opcodes.add(new Instruction("BRK", "IMM", 7) { public int operate() { return brk(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("ORA", "IZX", 6) { public int operate() { return ora(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 3) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ZP0", 3) { public int operate() { return ora(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ASL", "ZP0", 5) { public int operate() { return asl(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("PHP", "IMP", 3) { public int operate() { return php(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "IMM", 2) { public int operate() { return ora(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("ASL", "IMP", 2) { public int operate() { return asl(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABS", 4) { public int operate() { return ora(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("ASL", "ABS", 6) { public int operate() { return asl(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BPL", "REL", 2) { public int operate() { return bpl(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("ORA", "IZY", 5) { public int operate() { return ora(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ZPX", 4) { public int operate() { return ora(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ASL", "ZPX", 6) { public int operate() { return asl(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLC", "IMP", 2) { public int operate() { return clc(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABY", 4) { public int operate() { return ora(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ORA", "ABX", 4) { public int operate() { return ora(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("ASL", "ABX", 7) { public int operate() { return asl(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("JSR", "ABS", 6) { public int operate() { return jsr(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("AND", "IZX", 6) { public int operate() { return and(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BIT", "ZP0", 3) { public int operate() { return bit(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("AND", "ZP0", 3) { public int operate() { return and(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ROL", "ZP0", 5) { public int operate() { return rol(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("PLP", "IMP", 4) { public int operate() { return plp(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "IMM", 2) { public int operate() { return and(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("ROL", "IMP", 2) { public int operate() { return rol(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BIT", "ABS", 4) { public int operate() { return bit(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("AND", "ABS", 4) { public int operate() { return and(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("ROL", "ABS", 6) { public int operate() { return rol(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BMI", "REL", 2) { public int operate() { return bmi(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("AND", "IZY", 5) { public int operate() { return and(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ZPX", 4) { public int operate() { return and(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ROL", "ZPX", 6) { public int operate() { return rol(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SEC", "IMP", 2) { public int operate() { return sec(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ABY", 4) { public int operate() { return and(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("AND", "ABX", 4) { public int operate() { return and(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("ROL", "ABX", 7) { public int operate() { return rol(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("RTI", "IMP", 6) { public int operate() { return rti(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "IZX", 6) { public int operate() { return eor(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 3) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ZP0", 3) { public int operate() { return eor(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LSR", "ZP0", 5) { public int operate() { return lsr(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("PHA", "IMP", 3) { public int operate() { return pha(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "IMM", 2) { public int operate() { return eor(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("LSR", "IMP", 2) { public int operate() { return lsr(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("JMP", "ABS", 3) { public int operate() { return jmp(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("EOR", "ABS", 4) { public int operate() { return eor(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("LSR", "ABS", 6) { public int operate() { return lsr(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BVC", "REL", 2) { public int operate() { return bvc(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("EOR", "IZY", 5) { public int operate() { return eor(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ZPX", 4) { public int operate() { return eor(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LSR", "ZPX", 6) { public int operate() { return lsr(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLI", "IMP", 2) { public int operate() { return cli(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ABY", 4) { public int operate() { return eor(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("EOR", "ABX", 4) { public int operate() { return eor(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("LSR", "ABX", 7) { public int operate() { return lsr(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("RTS", "IMP", 6) { public int operate() { return rts(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "IZX", 6) { public int operate() { return adc(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 3) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ZP0", 3) { public int operate() { return adc(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("ROR", "ZP0", 5) { public int operate() { return ror(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("PLA", "IMP", 4) { public int operate() { return pla(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "IMM", 2) { public int operate() { return adc(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("ROR", "IMP", 2) { public int operate() { return ror(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("JMP", "IND", 5) { public int operate() { return jmp(); } public int addrmode() { return ind(); }});
        opcodes.add(new Instruction("ADC", "ABS", 4) { public int operate() { return adc(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("ROR", "ABS", 6) { public int operate() { return ror(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BVS", "REL", 2) { public int operate() { return bvs(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("ADC", "IZY", 5) { public int operate() { return adc(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ZPX", 4) { public int operate() { return adc(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("ROR", "ZPX", 6) { public int operate() { return ror(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SEI", "IMP", 2) { public int operate() { return sei(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ABY", 4) { public int operate() { return adc(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("ADC", "ABX", 4) { public int operate() { return adc(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("ROR", "ABX", 7) { public int operate() { return ror(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "IZX", 6) { public int operate() { return sta(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ZP0", 3) { public int operate() { return sty(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("STA", "ZP0", 3) { public int operate() { return sta(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("STX", "ZP0", 3) { public int operate() { return stx(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 3) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("DEY", "IMP", 2) { public int operate() { return dey(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("TXA", "IMP", 2) { public int operate() { return txa(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ABS", 4) { public int operate() { return sty(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("STA", "ABS", 4) { public int operate() { return sta(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("STX", "ABS", 4) { public int operate() { return stx(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BCC", "REL", 2) { public int operate() { return bcc(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("STA", "IZY", 6) { public int operate() { return sta(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STY", "ZPX", 4) { public int operate() { return sty(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("STA", "ZPX", 4) { public int operate() { return sta(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("STX", "ZPY", 4) { public int operate() { return stx(); } public int addrmode() { return zpy(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("TYA", "IMP", 2) { public int operate() { return tya(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "ABY", 5) { public int operate() { return sta(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("TXS", "IMP", 2) { public int operate() { return txs(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("STA", "ABX", 5) { public int operate() { return sta(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "IMM", 2) { public int operate() { return ldy(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("LDA", "IZX", 6) { public int operate() { return lda(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("LDX", "IMM", 2) { public int operate() { return ldx(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ZP0", 3) { public int operate() { return ldy(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LDA", "ZP0", 3) { public int operate() { return lda(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("LDX", "ZP0", 3) { public int operate() { return ldx(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 3) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("TAY", "IMP", 2) { public int operate() { return tay(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDA", "IMM", 2) { public int operate() { return lda(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("TAX", "IMP", 2) { public int operate() { return tax(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ABS", 4) { public int operate() { return ldy(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("LDA", "ABS", 4) { public int operate() { return lda(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("LDX", "ABS", 4) { public int operate() { return ldx(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BCS", "REL", 2) { public int operate() { return bcs(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("LDA", "IZY", 5) { public int operate() { return lda(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ZPX", 4) { public int operate() { return ldy(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LDA", "ZPX", 4) { public int operate() { return lda(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("LDX", "ZPY", 4) { public int operate() { return ldx(); } public int addrmode() { return zpy(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLV", "IMP", 2) { public int operate() { return clv(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDA", "ABY", 4) { public int operate() { return lda(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("TSX", "IMP", 2) { public int operate() { return tsx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("LDY", "ABX", 4) { public int operate() { return ldy(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("LDA", "ABX", 4) { public int operate() { return lda(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("LDX", "ABY", 4) { public int operate() { return ldx(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "IMM", 2) { public int operate() { return cpy(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("CMP", "IZX", 6) { public int operate() { return cmp(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "ZP0", 3) { public int operate() { return cpy(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("CMP", "ZP0", 3) { public int operate() { return cmp(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("DEC", "ZP0", 5) { public int operate() { return dec(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("INY", "IMP", 2) { public int operate() { return iny(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "IMM", 2) { public int operate() { return cmp(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("DEX", "IMP", 2) { public int operate() { return dex(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPY", "ABS", 4) { public int operate() { return cpy(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("CMP", "ABS", 4) { public int operate() { return cmp(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("DEC", "ABS", 6) { public int operate() { return dec(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BNE", "REL", 2) { public int operate() { return bne(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("CMP", "IZY", 5) { public int operate() { return cmp(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ZPX", 4) { public int operate() { return cmp(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("DEC", "ZPX", 6) { public int operate() { return dec(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CLD", "IMP", 2) { public int operate() { return cld(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ABY", 4) { public int operate() { return cmp(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("NOP", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CMP", "ABX", 4) { public int operate() { return cmp(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("DEC", "ABX", 7) { public int operate() { return dec(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "IMM", 2) { public int operate() { return cpx(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("SBC", "IZX", 6) { public int operate() { return sbc(); } public int addrmode() { return izx(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "ZP0", 3) { public int operate() { return cpx(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("SBC", "ZP0", 3) { public int operate() { return sbc(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("INC", "ZP0", 5) { public int operate() { return inc(); } public int addrmode() { return zp0(); }});
        opcodes.add(new Instruction("???", "IMP", 5) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("INX", "IMP", 2) { public int operate() { return inx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "IMM", 2) { public int operate() { return sbc(); } public int addrmode() { return imm(); }});
        opcodes.add(new Instruction("NOP", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return sbc(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("CPX", "ABS", 4) { public int operate() { return cpx(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("SBC", "ABS", 4) { public int operate() { return sbc(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("INC", "ABS", 6) { public int operate() { return inc(); } public int addrmode() { return abs(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("BEQ", "REL", 2) { public int operate() { return beq(); } public int addrmode() { return rel(); }});
        opcodes.add(new Instruction("SBC", "IZY", 5) { public int operate() { return sbc(); } public int addrmode() { return izy(); }});
        opcodes.add(new Instruction("???", "IMP", 2) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 8) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ZPX", 4) { public int operate() { return sbc(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("INC", "ZPX", 6) { public int operate() { return inc(); } public int addrmode() { return zpx(); }});
        opcodes.add(new Instruction("???", "IMP", 6) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SED", "IMP", 2) { public int operate() { return sed(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ABY", 4) { public int operate() { return sbc(); } public int addrmode() { return aby(); }});
        opcodes.add(new Instruction("NOP", "IMP", 2) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 4) { public int operate() { return nop(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("SBC", "ABX", 4) { public int operate() { return sbc(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("INC", "ABX", 7) { public int operate() { return inc(); } public int addrmode() { return abx(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
        opcodes.add(new Instruction("???", "IMP", 7) { public int operate() { return xxx(); } public int addrmode() { return imp(); }});
    }

    public Map<Integer, String> disassemble(int start, int end) {
        int addr = start;
        int line_addr;
        int value, low, high;

        Map<Integer, String> code = new TreeMap<>();

        while (addr < end) {
            line_addr = addr;
            String line = String.format("$%04X: ", addr);
            int opcode = bus.cpuRead(addr, true);
            addr = (addr+1) & 0x1FFFF;
            Instruction instr = opcodes.get(opcode);
            line += instr.name + " ";
            switch (instr.addr_mode) {
                case "IMP":
                    line += "{IMP}";
                    break;
                case "IMM":
                    value = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("#$%02X {IMM}", value);
                    break;
                case "ZP0":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%02X {ZP0}", low);
                    break;
                case "ZPX":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%02X, X {ZPX}", low);
                    break;
                case "ZPY":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%02X, Y {ZPY}", low);
                    break;
                case "IZX":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("($%02X, X) {IZX}", low);
                    break;
                case "IZY":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line +=  String.format("($%02X), Y {IZY}", low);
                    break;
                case "ABS":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    high = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%04X {ABS}", (high << 8) | low);
                    break;
                case "ABX":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    high = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%04X, X {ABX}", (high << 8) | low);
                    break;
                case "ABY":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    high = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%04X, Y {ABY}", (high << 8) | low);
                    break;
                case "IND":
                    low = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    high = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("($%04X) {IND}", (high << 8) | low);
                    break;
                case "REL":
                    value = bus.cpuRead(addr, true);
                    addr = (addr+1) & 0x1FFFF;
                    line += String.format("$%02X ", value) + String.format("[$%04X] {IND}", addr + (byte)(value));
            }
            code.put(line_addr, line);
        }
        return code;
    }

    void connectBus(Bus bus) {
        this.bus = bus;
    }

    private void write(int addr, int data) {
        bus.cpuWrite(addr, data);
    }

    private int read(int addr) {
        return bus.cpuRead(addr);
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
    private int imp() {
        fetched = a & 0x00FF;
        return 0;
    }

    private int zp0() {
        addr_abs = read(pc);
        addr_abs &= 0x00FF;
        pc = (pc+1) & 0xFFFF;
        return 0;
    }

    private int zpy() {
        addr_abs = read(pc) + (y & 0x00FF);
        addr_abs &= 0x00FF;
        pc = (pc+1) & 0xFFFF;
        return 0;
    }

    private int abs() {
        int low = read(pc);
        pc = (pc+1) & 0xFFFF;
        int high = read(pc);
        pc = (pc+1) & 0xFFFF;
        addr_abs =  (high << 8) | low;
        return 0;
    }

    private int aby() {
        int low = read(pc);
        pc = (pc+1) & 0xFFFF;
        int high = read(pc);
        pc = (pc+1) & 0xFFFF;
        addr_abs = (high << 8 | low) & 0xFFFF;
        addr_abs += y & 0x00FF;
        addr_abs &= 0xFFFF;
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    private int izx() {
        int ptr = read(pc);
        pc = (pc+1) & 0xFFFF;
        int low = read((ptr + x) & 0x00FF);
        int high = read((ptr + x + 1) & 0x00FF);
        addr_abs =  (high << 8) | low;
        return 0;
    }

    private int imm() {
        addr_abs = pc;
        pc =(pc+1) & 0xFFFF;
        return 0;
    }

    private int zpx() {
        addr_abs =  (read(pc) + (x & 0x00FF));
        addr_abs &= 0x00FF;
        pc = (pc+1) & 0xFFFF;
        return 0;
    }

    private int rel() {
        addr_rel = read(pc);
        pc = (pc+1) & 0xFFFF;
        if ((addr_rel & 0x80) != 0x0000)
            addr_rel |= 0xFFFFFF00;
        return 0;
    }

    private int abx() {
        int low = read(pc);
        pc = (pc+1) & 0xFFFF;
        int high = read(pc);
        pc = (pc+1) & 0xFFFF;
        addr_abs =  ((high << 8) | low) + (x & 0x00FF);
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    private int ind() {
        int low = read(pc);
        pc = (pc+1) & 0xFFFF;
        int high = read(pc);
        pc = (pc+1) & 0xFFFF;
        int ptr =  (high << 8) | low;

        if (low == 0xFF)
            addr_abs =  (read((ptr & 0xFF00) << 8)) | read(ptr);
        else
            addr_abs = (read(ptr + 1) << 8) | read(ptr);
        return 0;
    }

    private int izy() {
        int ptr = read(pc);
        pc = (pc+1) & 0xFFFF;

        int low = read(ptr & 0x00FF) & 0x00FF;
        int high = read((ptr  + 1) & 0x00FF) & 0x00FF;
        addr_abs = ((high << 8) | low ) & 0xFFFF;
        addr_abs += y & 0x00FF;
        addr_abs &= 0xFFFF;
        if ((addr_abs & 0xFF00) != (high << 8))
            return 1;
        return 0;
    }

    // ====================================================================================

    // ====================================== Opcodes ======================================
    private int adc() {
        fetch();
        tmp =  (a + fetched + (getFlag(Flags.C) ? 0x1 : 0x0)) & 0x01FF;
        setFlag(Flags.C, tmp > 0x00FF);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        setFlag(Flags.V, ((~(a ^ fetched) & (a ^ tmp)) & 0x0080) == 0x0080);
        a =  (tmp & 0x00FF);
        return 1;
    }

    private int and() {
        fetch();
        a =  (a & fetched) & 0x00FF;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 1;
    }

    private int asl() {
        fetch();
        tmp =  (fetched << 1) & 0x01FF;
        setFlag(Flags.C, (tmp & 0xFF00) > 0);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a =  (tmp & 0x00FF);
        else
            write(addr_abs,  (tmp & 0x00FF));
        return 0;
    }

    private int bcc() {
        if (!getFlag(Flags.C)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int bcs() {
        if (getFlag(Flags.C)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int beq() {
        if (getFlag(Flags.Z)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int bit() {
        fetch();
        tmp =  (a & fetched) & 0x00FF;
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (fetched & (1 << 7)) == (1 << 7));
        setFlag(Flags.V, (fetched & (1 << 6)) == (1 << 6));
        return 0;
    }

    private int bmi() {
        if (getFlag(Flags.N)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int bne() {
         if (!getFlag(Flags.Z)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int bpl() {
        if (!getFlag(Flags.N)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int brk() {
        pc = (pc+1) & 0xFFFF;
        setFlag(Flags.I, true);
        write(0x0100 + stkp,  ((pc >> 8) & 0x00FF));
        stkp = (stkp-1) & 0x00FF;
        write(0x0100 + stkp,  (pc & 0x00FF));
        stkp = (stkp-1) & 0x00FF;
        setFlag(Flags.B, true);
        write(0x0100 + stkp, status);
        stkp = (stkp-1) & 0x00FF;
        setFlag(Flags.B, false);
        pc = read(0xFFFE) | (read(0xFFFF)<< 8);
        return 0;
    }

    private int bvc() {
        if (!getFlag(Flags.V)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int bvs() {
        if (getFlag(Flags.V)) {
            cycles++;
            addr_abs = pc + addr_rel;
            if ((addr_abs & 0xFF00) != (pc & 0xFF00))
                cycles++;
            pc = addr_abs & 0xFFFF;
        }
        return 0;
    }

    private int clc() {
        setFlag(Flags.C, false);
        return 0;
    }

    private int cld() {
        setFlag(Flags.D, false);
        return 0;
    }

    private int cli() {
        setFlag(Flags.I, false);
        return 0;
    }

    private int clv() {
        setFlag(Flags.V, false);
        return 0;
    }

    private int cmp() {
        fetch();
        tmp =  (a + (fetched ^ 0x00FF) + 1) & 0x00FF;
        setFlag(Flags.C, a >= fetched);
        setFlag(Flags.Z, (a & 0xFF) == (fetched & 0xFF));
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 1;
    }

    private int cpx() {
        fetch();
        tmp =  (x + (fetched ^ 0x00FF) + 1) & 0x00FF;
        setFlag(Flags.C, x >= fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private int cpy() {
        fetch();
        tmp =  (y + (fetched ^ 0x00FF) + 1) & 0x00FF;
        setFlag(Flags.C, y >= fetched);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private int dec() {
        fetch();
        tmp =  (fetched - 1) & 0x00FF;
        write(addr_abs,  (tmp & 0x00FF));
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private int dex() {
        x = (x-1) & 0x00FF;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 0;
    }

    private int dey() {
        y = (y-1) & 0x00FF;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 0;
    }

    private int eor() {
        fetch();
        a =  (a ^ fetched) & 0x00FF;
        setFlag(Flags.Z, (a & 0x00FF) == 0x0000);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private int inc() {
        fetch();
        tmp =  (fetched + 1) & 0x00FF;
        write(addr_abs,  (tmp & 0x00FF));
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        return 0;
    }

    private int inx() {
        x = (x+1) & 0x00FF;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 0;
    }

    private int iny() {
        y = (y+1) & 0x00FF;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 0;
    }

    private int jmp() {
        pc = addr_abs & 0xFFFF;
        return 0;
    }

    private int jsr() {
        pc = (pc-1) & 0xFFFF;
        write(0x0100 + stkp,  ((pc >> 8) & 0x00FF));
        stkp = (stkp-1) & 0x00FF;
        write(0x0100 + stkp,  (pc & 0x00FF));
        stkp = (stkp-1) & 0x00FF;
        pc = addr_abs & 0xFFFF;
        return 0;
    }

    private int lda() {
        fetch();
        a = fetched & 0x00FF;
        setFlag(Flags.Z, (a & 0x00FF) == 0x0000);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private int ldx() {
        fetch();
        x = fetched & 0x00FF;
        setFlag(Flags.Z, (x & 0x00FF) == 0x0000);
        setFlag(Flags.N, (x & 0x0080) == 0x0080);
        return 1;
    }

    private int ldy() {
        fetch();
        y = fetched & 0x00FF;
        setFlag(Flags.Z, (y & 0x00FF) == 0x0000);
        setFlag(Flags.N, (y & 0x0080) == 0x0080);
        return 1;
    }

    private int lsr() {
        fetch();
        setFlag(Flags.C, (fetched & 0x0001) == 0x0001);
        tmp =  (fetched >> 1);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x0000);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a =  (tmp & 0x00FF);
        else
            write(addr_abs,  (tmp & 0x00FF));
        return 0;
    }

    private int nop() {
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

    private int ora() {
        fetch();
        a =  (a | fetched) & 0x00FF;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x0080) == 0x0080);
        return 1;
    }

    private int pha() {
        write(0x0100 + (stkp & 0x00FF), a & 0x00FF);
        stkp = (stkp-1) & 0x00FF;
        return 0;
    }

    private int php() {
        write(0x0100 + (stkp & 0x00FF),  ((status | Flags.U.value | Flags.B.value) & 0x00FF));
        setFlag(Flags.B, false);
        setFlag(Flags.U, false);
        stkp = (stkp-1) & 0x00FF;
        return 0;
    }

    private int pla() {
        stkp = (stkp+1) & 0x00FF;
        a = read(0x0100 + stkp);
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private int plp() {
        stkp = (stkp+1) & 0x00FF;
        status = read(0x0100 + stkp);
        setFlag(Flags.U, true);
        return 0;
    }

    private int rol() {
        fetch();
        tmp =  ((getFlag(Flags.C) ? 1 : 0) | fetched << 1);
        setFlag(Flags.C, (tmp & 0xFF00) != 0x0000);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x00);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a =  (tmp & 0x00FF);
        else
            write(addr_abs,  (tmp & 0x00FF));
        return 0;
    }

    private int ror() {
        fetch();
        tmp =  ((getFlag(Flags.C) ? 1 << 7 : 0) | fetched >> 1);
        setFlag(Flags.C, (fetched & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0x00);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        if (opcodes.get(opcode).addr_mode.equals("IMP"))
            a =  (tmp & 0x00FF);
        else
            write(addr_abs,  (tmp & 0x00FF));
        return 0;
    }

    private int rti() {
        stkp = (stkp+1) & 0x00FF;
        status = read(0x0100 + stkp);
        status &= ~Flags.B.value;
        status &= ~Flags.U.value;

        stkp = (stkp+1) & 0x00FF;
        pc = read(0x0100 + stkp);
        stkp = (stkp+1) & 0x00FF;
        pc |= read(0x0100 + stkp) << 8;
        return 0;
    }

    private int rts() {
        stkp = (stkp+1) & 0x00FF;
        pc = read(0x0100 + stkp);
        stkp = (stkp+1) & 0x00FF;
        pc |= read(0x0100 + stkp) << 8;
        pc++;
        return 0;
    }

    private int sbc() {
        fetch();
        int value =  (fetched ^ 0x00FF);

        tmp =  (a + value + (getFlag(Flags.C) ? 0x1 : 0x0)) & 0x01FF;
        setFlag(Flags.C, tmp > 0x00FF);
        setFlag(Flags.Z, (tmp & 0x00FF) == 0);
        setFlag(Flags.N, (tmp & 0x0080) == 0x0080);
        setFlag(Flags.V, ((tmp ^ a) & (tmp ^ value) & 0x0080) == 0x0080);
        a =  (tmp & 0x00FF);
        return 1;
    }

    private int sec() {
        setFlag(Flags.C, true);
        return 0;
    }

    private int sed() {
        setFlag(Flags.D, true);
        return 0;
    }

    private int sei() {
        setFlag(Flags.I, true);
        return 0;
    }

    private int sta() {
        write(addr_abs, a & 0x00FF);
        return 0;
    }

    private int stx() {
        write(addr_abs, x & 0x00FF);
        return 0;
    }

    private int sty() {
        write(addr_abs, y & 0x00FF);
        return 0;
    }

    private int tax() {
        x = a & 0x00FF;
        setFlag(Flags.Z, x == 0x00);
        setFlag(Flags.N, (x & 0x80) != 0x00);
        return 0;
    }

    private int tay() {
        y = a  & 0x00FF;
        setFlag(Flags.Z, y == 0x00);
        setFlag(Flags.N, (y & 0x80) != 0x00);
        return 0;
    }

    private int tsx() {
        x = stkp & 0x00FF;
        setFlag(Flags.Z, x == 0x00);
        setFlag(Flags.N, (x & 0x80) != 0x00);
        return 0;
    }

    private int txa() {
        a = x & 0x00FF;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private int txs() {
        stkp = x & 0x00FF;
        return 0;
    }

    private int tya() {
        a = y & 0x00FF;
        setFlag(Flags.Z, a == 0x00);
        setFlag(Flags.N, (a & 0x80) != 0x00);
        return 0;
    }

    private int xxx() {
        return 0;
    }

    // =====================================================================================

    public void clock() {
        if (cycles == 0) {
            opcode = read(pc);
            pc = (pc+1) & 0xFFFF;

            Instruction instr = opcodes.get(opcode);
            cycles = instr.cycles;
            int additional_cycle_1 = instr.addrmode();
            int additional_cycle_2 = instr.operate();
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
        int low = read(addr_abs);
        int high = read(addr_abs + 1);
        pc = high << 8 | low;

        addr_rel = 0x0000;
        addr_abs = 0x0000;
        fetched = 0x00;

        cycles = 8;
    }

    public void irq() {
        if (!getFlag(Flags.I)) {
            write(0x0100 + stkp,  ((pc >> 8) & 0x00FF));
            stkp = (stkp-1) & 0x00FF;
            write(0x0100 + stkp,  (pc & 0x00FF));
            stkp = (stkp-1) & 0x00FF;

            setFlag(Flags.B, false);
            setFlag(Flags.U, true);
            setFlag(Flags.I, true);
            write(0x0100 + stkp, status);
            stkp = (stkp-1) & 0x00FF;

            addr_abs = 0xFFFE;
            int low = read(addr_abs);
            int high = read(addr_abs + 1);
            pc = high << 8 | low;

            cycles = 7;
        }
    }

    public void nmi() {
        write(0x0100 + stkp,  ((pc >> 8) & 0x00FF));
        stkp = (stkp-1) & 0x00FF;
        write(0x0100 + stkp,  (pc & 0x00FF));
        stkp = (stkp-1) & 0x00FF;

        setFlag(Flags.B, false);
        setFlag(Flags.U, true);
        setFlag(Flags.I, true);
        write(0x0100 + stkp, status);
        stkp = (stkp-1) & 0x00FF;

        addr_abs = 0xFFFA;
        int low = read(addr_abs);
        int high = read(addr_abs + 1);
        pc = high << 8 | low;

        cycles = 8;
    }

    private int fetch() {
        if (!opcodes.get(opcode).addr_mode.equals("IMP"))
            fetched = read(addr_abs);
        return fetched & 0x00FF;
    }

    public boolean complete() {
        return cycles == 0;
    }

    public int getA() {
        return a;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStkp() {
        return stkp;
    }

    public int getStatus() {
        return status;
    }

    public int getPc() {
        return pc;
    }
}

abstract class Instruction {

    String name;
    String addr_mode;
    int cycles;
    public abstract int operate();
    public abstract int addrmode();

    Instruction(String name, String addr_mode, int cycles) {
        this.name = name;
        this.addr_mode = addr_mode;
        this.cycles = cycles;
    }
}

package core.cpu;

import core.NES;
import utils.IntegerWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represent the CPU of the NES
 */
public class CPU_6502 {

    private final List<Instruction> opcodes;
    private NES nes;
    private int accumulator = 0x00;
    private int x_register = 0x00;
    private int y_register = 0x00;
    private int stack_pointer = 0x00;
    private int status = 0x00;
    private int program_counter = 0x0000;
    private int tmp = 0x0000;
    private int fetched = 0x00;
    private int opcode = 0x00;
    private int cycles = 0x00;
    private int addr_abs = 0x0000;
    private int addr_rel = 0x00;
    private long cpu_clock = 0L;
    private boolean halted;

    /**
     * Create a new CPU and populate the opcode list
     */
    public CPU_6502() {
        opcodes = new ArrayList<>();
        opcodes.add(new Instruction(OPCode.BRK, AddressingMode.IMM, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ASL, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.PHP, AddressingMode.IMP, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ASL, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ANC, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ASL, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BPL, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ASL, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.CLC, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ORA, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ASL, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.ASO, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.JSR, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.BIT, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ROL, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.PLP, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ROL, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ANC, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.BIT, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROL, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BMI, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROL, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SEC, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AND, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROL, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.RLA, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.RTI, AddressingMode.IMP, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.LSR, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.PHA, AddressingMode.IMP, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LSR, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ALR, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.JMP, AddressingMode.ABS, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LSR, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BVC, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LSR, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.CLI, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.EOR, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LSR, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.LSE, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.RTS, AddressingMode.IMP, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.ROR, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.PLA, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ROR, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ARR, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.JMP, AddressingMode.IND, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROR, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BVS, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROR, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SEI, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ADC, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.ROR, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.RRA, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.AXS, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.STY, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.STX, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.AXS, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.DEY, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.TXA, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.XAA, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.STY, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.STX, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AXS, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.BCC, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.IZY, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.AXA, AddressingMode.IZY, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.STY, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.STX, AddressingMode.ZPY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.AXS, AddressingMode.ZPY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.TYA, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.ABY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.TXS, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.TAS, AddressingMode.ABY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.SAY, AddressingMode.ABX, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.STA, AddressingMode.ABX, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.XAS, AddressingMode.ABY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.AXA, AddressingMode.ABY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.LDY, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.LDX, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.LDY, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.LDX, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.TAY, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.TAX, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.OAL, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LDY, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDX, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.BCS, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.LDY, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDX, AddressingMode.ZPY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.ZPY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.CLV, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.TSX, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.LAS, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDY, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDA, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LDX, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.LAX, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.CPY, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.CPY, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.DEC, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.INY, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.DEX, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SAX, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CPY, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.DEC, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BNE, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.DEC, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.CLD, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.CMP, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.DEC, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.DCM, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.CPX, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.IZX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.IZX, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.CPX, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.ZP0, opcodes.size(), 3, this));
        opcodes.add(new Instruction(OPCode.INC, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.ZP0, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.INX, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.IMM, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.CPX, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.ABS, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.INC, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.ABS, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.BEQ, AddressingMode.REL, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.IZY, opcodes.size(), 5, this));
        opcodes.add(new Instruction(OPCode.HLT, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.IZY, opcodes.size(), 8, this));
        opcodes.add(new Instruction(OPCode.SKB, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.ZPX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.INC, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.ZPX, opcodes.size(), 6, this));
        opcodes.add(new Instruction(OPCode.SED, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.ABY, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.NOP, AddressingMode.IMP, opcodes.size(), 2, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.ABY, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.SKW, AddressingMode.IMP, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.SBC, AddressingMode.ABX, opcodes.size(), 4, this));
        opcodes.add(new Instruction(OPCode.INC, AddressingMode.ABX, opcodes.size(), 7, this));
        opcodes.add(new Instruction(OPCode.INS, AddressingMode.ABX, opcodes.size(), 7, this));
    }

    /**
     * Connect the CPU to a Bus
     *
     * @param NES the Bus to connect to
     */
    public void connectBus(NES NES) {
        this.nes = NES;
    }

    /**
     * Write to an address in the addressable range
     *
     * @param addr the address to write to
     */
    private void write(int addr, int data) {
        nes.cpuWrite(addr, data);
    }

    /**
     * Read from an address in the addressable range
     *
     * @param addr the address to read from
     * @return the read data
     */
    private int read(int addr) {
        return nes.cpuRead(addr, false) & 0xFF;
    }

    /**
     * Get a Flag value
     *
     * @param flag the Flag to get
     * @return is the Flag set to 1
     */
    private boolean getFlag(Flags flag) {
        return (status & flag.value) == flag.value;
    }

    /**
     * Set a Flag to 0 or 1
     *
     * @param flag  The Flag to set
     * @param value should the Flag be set 1
     */
    private void setFlag(Flags flag, boolean value) {
        if (value)
            status |= flag.value;
        else
            status &= ~flag.value;
    }

    // =========================================== Addressing Modes ===========================================

    /**
     * Implied Addressing
     * Their is not data to fetch, the instruction will use the Accumulator as input
     *
     * @return 0 No extra cycle required
     */
    int imp() {
        fetched = accumulator & 0xFF;

        return 0;
    }

    /**
     * Zero Page Addressing
     * The value following the OPCode is considered as an offset
     * this offset is used to index the 0th page
     * Example : Passed value      : 0x12
     * Effective address : 0x0012
     *
     * @return 0 No extra cycle required
     */
    int zp0() {
        addr_abs = read(program_counter++);
        program_counter &= 0xFFFF;

        return 0;
    }

    /**
     * Zero Page Addressing with Y Offset
     * The value following the OPCode is considered as an offset
     * the content of the Y Register is added to that offset
     * this offset is used to index the 0th page
     * Example : Passed value      : 0x12
     * Y Register        : 0xD
     * Effective address : 0x00E2
     *
     * @return 0 No extra cycle required
     */
    int zpy() {
        addr_abs = read(program_counter++) + y_register;
        addr_abs &= 0x00FF;
        program_counter &= 0xFFFF;

        return 0;
    }

    /**
     * Absolute Addressing
     * The address is read as the following 16bit
     *
     * @return 0 No extra cycle required
     */
    int abs() {
        int low = read(program_counter++);
        program_counter &= 0xFFFF;
        int high = read(program_counter++);
        program_counter &= 0xFFFF;

        addr_abs = (high << 8) | low;

        return 0;
    }

    /**
     * Absolute Addressing with Y Offset
     * The Address is read as the following 16bit
     * the Y Register is then adder to that Address
     *
     * @return 1 if a page Boundary is crossed when adding Y Register, 0 otherwise
     */
    int aby() {
        int low = read(program_counter++);
        program_counter &= 0xFFFF;
        int high = read(program_counter++);
        program_counter &= 0xFFFF;

        addr_abs = (high << 8 | low) + y_register;
        addr_abs &= 0xFFFF;

        if ((addr_abs & 0xFF00) != (high << 8)) return 1;
        return 0;
    }

    /**
     * Indirect X Addressing
     * Read the address next to the OPCode and index in the 0th page
     * The X Register is then added to that address
     * We then load a 16bit value from that offset address (8 LSB read followed by 8 MSB)
     * Example : Passed address      : 0xD8
     * value in X Register : 0x10
     * 0th page address    : 0x00E8
     * value at 0x00E8     : 0x12
     * value at 0x00E9     : 0x23
     * effective address   : 0x2312
     *
     * @return 0 No extra cycle required
     */
    int izx() {
        int ptr = read(program_counter++);
        program_counter &= 0xFFFF;

        int low = read((ptr + (x_register & 0xFF) & 0xFFFF) & 0x00FF);
        int high = read(((ptr + (x_register & 0xFF) + 1) & 0xFFFF) & 0x00FF);

        addr_abs = (high << 8) | low;

        return 0;
    }

    /**
     * Immediate Addressing
     * The value following the OPCode is the value searched
     * the effective address is then the Program Counter
     *
     * @return 0 No extra cycle required
     */
    int imm() {
        addr_abs = program_counter++;
        program_counter &= 0xFFFF;

        return 0;
    }

    /**
     * Zero Page Addressing with X Offset
     * The value following the OPCode is considered as an offset
     * the content of the X Register is added to that offset
     * this offset is used to index the 0th page
     * Example : Passed value      : 0x12
     * X Register        : 0xD
     * Effective address : 0x00E2
     *
     * @return 0 No extra cycle required
     */
    int zpx() {
        addr_abs = read(program_counter++) + x_register;
        addr_abs &= 0x00FF;
        program_counter &= 0xFFFF;

        return 0;
    }

    /**
     * Relative Addressing (Exclusive to Branching instruction)
     * The value following the OPCode is considered as a signed 8bit value
     * that value is then added (as signed) to the current address
     *
     * @return 0 No extra cycle required
     */
    int rel() {
        addr_rel = read(program_counter++);
        program_counter &= 0xFFFF;

        if ((addr_rel & 0x80) == 0x80) addr_rel |= 0xFFFFFF00;

        return 0;
    }

    /**
     * Absolute Addressing with X Offset
     * The address is read as the following 16bit
     * the X Register is then adder to that Address
     *
     * @return 1 if a page boundary is crossed when adding X Register, 0 otherwise
     */
    int abx() {
        int low = read(program_counter++);
        program_counter &= 0xFFFF;
        int high = read(program_counter++);
        program_counter &= 0xFFFF;

        addr_abs = ((high << 8) | low) + x_register;
        addr_abs &= 0xFFFF;

        //Dummy read
        if (low + x_register > 0xFF || opcodes.get(opcode).assembly == OPCode.ROL)
            read(((high << 8) & 0xFF00) | (addr_abs & 0xFF));
        if ((addr_abs & 0xFF00) != (high << 8)) return 1;

        return 0;
    }

    /**
     * Indirect Addressing
     * an address is read from the 16bit following the OPCode (8 LSB then 8 MSB)
     * We then read the Address at that location (8 LSB followed by 8 MSB)
     * <p>
     * This Addressing mode contains a bug in real hardware
     * If the address following the OPCode has the 8 LSB equals to 0xFF
     * then when reading the effective address the page boundary isn't crossed
     * it then read the first address of the same page
     * <p>
     * Examples : Passed addresses  : 0x1FF3     0x1DFF
     * value at 0x1FF3   : 0x12
     * value at 0x1FF4   : 0x23
     * value at 0x1DFF   : 0x32
     * value at 0x1D00   : 0xA1
     * effective address : 0x2312     0xA132  *The 8 MSB are read from 0x1D00 instead of 1E00
     *
     * @return 0 No extra cycle required (because no page boundary cross can occur)
     */
    int ind() {
        int low = read(program_counter++);
        program_counter &= 0xFFFF;
        int high = read(program_counter++);
        program_counter &= 0xFFFF;

        int ptr = (high << 8) | low;

        if (low == 0xFF) addr_abs = (read(ptr & 0xFF00) << 8) | read(ptr); //Page boundary bug
        else addr_abs = (read(ptr + 1) << 8) | read(ptr);

        return 0;
    }

    /**
     * Indirect Y Addressing
     * Read the address next to the OPCode and index in the 0th page
     * We then load a 16bit value from that address (8 LSB followed by 8 MSB)
     * The Y Register is then added to get the final address
     * Example : Passed address      : 0xF8
     * 0th page address    : 0x00F8
     * value at 0x00F8     : 0x12
     * value at 0x00F9     : 0x23
     * value in Y Register : 0x10
     * read Address        : 0x2312
     * effective address   : 0x2322
     *
     * @return 1 If when adding Y we cross a page boundary 0 otherwise
     */
    int izy() {
        int ptr = read(program_counter++);
        program_counter &= 0xFFFF;

        int low = read(ptr);
        int high = read((ptr + 1) & 0x00FF);

        addr_abs = (high << 8) | low;
        addr_abs += y_register;
        addr_abs &= 0xFFFF;

        //Dummy read
        if (low + y_register > 0xFF)
            read(((high << 8) & 0xFF00) | (addr_abs & 0xFF));

        if ((addr_abs & 0xFF00) != (high << 8)) return 1;

        return 0;
    }


    // ================================================ Opcodes ================================================

    /**
     * Add the fetched value to the Accumulator
     * C Flag set if Accumulator + Fetched > 0xFF
     * Z Flag set if the final Accumulator = 0
     * N Flag set if the final Accumulator has MSB set
     * V Flag set if (When considering data as signed) :
     * Positive + Positive = Negative (MSB set)
     * Negative + Negative = Positive (MSB not set)
     *
     * @return 1 An extra cycle can be required depending on the addressing mode
     */
    int adc() {
        fetch();
        tmp = (accumulator + fetched + (getFlag(Flags.C) ? 0x1 : 0x0)) & 0x01FF;

        setFlag(Flags.V, ((tmp ^ accumulator) & (tmp ^ fetched) & 0x80) == 0x80);
        setFlag(Flags.C, tmp > 0xFF);
        setFlag(Flags.Z, (tmp & 0xFF) == 0);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        accumulator = tmp & 0x00FF;

        return 1;
    }

    /**
     * Compute a Logic AND between the Accumulator and the Fetched data
     * stores it into the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 1 Extra cycle may be required
     */
    int and() {
        fetch();
        accumulator = accumulator & fetched;
        accumulator &= 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) != 0x00);

        return 1;
    }

    /**
     * Shift Left the Fetched data and store it depending on the addressing mode
     * C Flag set if Fetched data has MSB set
     * Z Flag set if Computed data = 0
     * N Flag set if Computed data has MSB set
     *
     * @return 0 No extra cycle required
     */
    int asl() {
        fetch();
        tmp = fetched << 1;

        setFlag(Flags.C, (tmp & 0xFF00) > 0);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        if (opcodes.get(opcode).addr_mode == AddressingMode.IMP) accumulator = tmp & 0xFF;
        else write(addr_abs, tmp & 0xFF);

        return 0;
    }

    /**
     * Branch on Carry Clear
     * Jump to Fetched Address if C Flag isn't set
     *
     * @return 0 No extra cycle required
     */
    int bcc() {
        if (!getFlag(Flags.C)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Branch on Carry Set
     * Jump to Fetched Address if C Flag is set
     *
     * @return 0 No extra cycle required
     */
    int bcs() {
        if (getFlag(Flags.C)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Branch on Equal
     * Jump to Fetched Address if Z Flag is set
     *
     * @return 0 No extra cycle required
     */
    int beq() {
        if (getFlag(Flags.Z)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Do a Bit test between the Accumulator and the Fetched data
     * Z Flag set if the Accumulator and the Fetched data have no common bits
     * N Flag set if Fetched data has 7th bit set
     * V Flag set if Fetched data has 6th bit set
     *
     * @return 0 No extra cycle required
     */
    int bit() {
        fetch();
        tmp = (accumulator & fetched);

        setFlag(Flags.Z, (tmp & 0xFF) == 0x0000);
        setFlag(Flags.N, (fetched & 0x80) == 0x80);
        setFlag(Flags.V, (fetched & 0x40) == 0x40);

        return 0;
    }

    /**
     * Branch on Negative
     * Jump to Fetched Address if N Flag is set
     *
     * @return 0 No extra cycle required
     */
    int bmi() {
        if (getFlag(Flags.N)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Branch on Not Equal
     * Jump to Fetched Address if Z Flag isn't set
     *
     * @return 0 No extra cycle required
     */
    int bne() {
        if (!getFlag(Flags.Z)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Branch on Positive
     * Jump to Fetched Address if N Flag isn't set
     *
     * @return 0 No extra cycle required
     */
    int bpl() {
        if (!getFlag(Flags.N)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Fire an Interrupt
     * Push the Program Counter and Status Register (Set the B Flag before) to the Stack
     * Jump to the Address Specified at Location 0xFFFF and 0xFFFE
     *
     * @return 0 No extra cycle required
     */
    int brk() {
        //Dummy Read
        read((program_counter - 1) & 0xFFFF);
        pushStack((program_counter >> 8) & 0xFF);
        pushStack(program_counter & 0xFF);
        pushStack((status | Flags.B.value) & 0xFF);
        program_counter = irqVector();

        setFlag(Flags.I, true);

        return 0;
    }

    /**
     * Branch on Not Overflow
     * Jump to Fetched Address if V Flag isn't set
     *
     * @return 0 No extra cycle required
     */
    int bvc() {
        if (!getFlag(Flags.V)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Branch on Overflow
     * Jump to Fetched Address if V Flag is set
     *
     * @return 0 No extra cycle required
     */
    int bvs() {
        if (getFlag(Flags.V)) {
            cycles++;
            addr_abs = program_counter + addr_rel;
            addr_abs &= 0xFFFF;
            if ((addr_abs & 0xFF00) != (program_counter & 0xFF00))
                cycles++;
            program_counter = addr_abs;
        }
        return 0;
    }

    /**
     * Set the Carry Flag of the Status Register to 0
     * C Flag not set
     *
     * @return 0 No extra cycle required
     */
    int clc() {
        setFlag(Flags.C, false);
        return 0;
    }

    /**
     * Set the Decimal Flag of the Status Register to 0
     * D Flag not set
     *
     * @return 0 No extra cycle required
     */
    int cld() {
        setFlag(Flags.D, false);
        return 0;
    }

    /**
     * Set the Interrupt Flag of the Status Register to 0
     * I Flag not set
     *
     * @return 0 No extra cycle required
     */
    int cli() {
        setFlag(Flags.I, false);
        return 0;
    }

    /**
     * Set the Overflow Flag of the Status Register to 0
     * V Flag not set
     *
     * @return 0 No extra cycle required
     */
    int clv() {
        setFlag(Flags.V, false);
        return 0;
    }

    /**
     * Compare the Fetched data with the Accumulator
     * C Flag set if Accumulator > Fetched data
     * Z Flag set if Accumulator = Fetched data
     * N Flag set if Accumulator < Fetched data
     *
     * @return 0 No extra cycle required
     */
    int cmp() {
        fetch();
        tmp = accumulator - fetched;

        setFlag(Flags.C, accumulator >= fetched);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        return 1;
    }

    /**
     * Compare the Fetched data with the X Register
     * C Flag set if X Register > Fetched data
     * Z Flag set if X Register = Fetched data
     * N Flag set if X Register < Fetched data
     *
     * @return 0 No extra cycle required
     */
    int cpx() {
        fetch();
        tmp = x_register - fetched;

        setFlag(Flags.C, x_register >= fetched);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        return 0;
    }

    /**
     * Compare the Fetched data with the Y Register
     * C Flag set if Y Register > Fetched data
     * Z Flag set if Y Register = Fetched data
     * N Flag set if Y Register < Fetched data
     *
     * @return 0 No extra cycle required
     */
    int cpy() {
        fetch();
        tmp = y_register - fetched;

        setFlag(Flags.C, y_register >= fetched);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        return 0;
    }

    /**
     * Decrement the Accumulator
     * If Accumulator = 0x00 => wrap to 0xFF
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int dec() {
        fetch();
        tmp = (fetched - 1) & 0xFF;
        write(addr_abs, tmp);

        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        return 0;
    }

    /**
     * Decrement the X Register
     * If X Register = 0x00 => wrap to 0xFF
     * Z Flag set if X Register = 0
     * N Flag set if X Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int dex() {
        x_register--;
        x_register &= 0xFF;

        setFlag(Flags.Z, (x_register & 0xFF) == 0x00);
        setFlag(Flags.N, (x_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Decrement the Y Register
     * If Y Register = 0x00 => wrap to 0xFF
     * Z Flag set if Y Register = 0
     * N Flag set if Y Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int dey() {
        y_register--;
        y_register &= 0xFF;

        setFlag(Flags.Z, (y_register & 0xFF) == 0x00);
        setFlag(Flags.N, (y_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Compute a Logic XOR between the Accumulator and the Fetched data
     * stores it into the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 1 Extra cycle may be required
     */
    int eor() {
        fetch();
        accumulator = (accumulator ^ fetched) & 0x00FF;

        setFlag(Flags.Z, (accumulator & 0xFF) == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 1;
    }

    /**
     * Increment the Accumulator
     * If Accumulator = 0xFF => wrap to 0x00
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int inc() {
        fetch();
        tmp = (fetched + 1) & 0xFF;
        write(addr_abs, tmp);

        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        return 0;
    }

    /**
     * Increment the X Register
     * If X Register = 0xFF => wrap to 0x00
     * Z Flag set if X Register = 0
     * N Flag set if X Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int inx() {
        x_register++;
        x_register &= 0xFF;

        setFlag(Flags.Z, (x_register & 0xFF) == 0x00);
        setFlag(Flags.N, (x_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Increment the Y Register
     * If Y Register = 0xFF => wrap to 0x00
     * Z Flag set if Y Register = 0
     * N Flag set if Y Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int iny() {
        y_register++;
        y_register &= 0xFF;

        setFlag(Flags.Z, (y_register & 0xFF) == 0x00);
        setFlag(Flags.N, (y_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Jump to Fetched Address
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int jmp() {
        program_counter = addr_abs & 0xFFFF;
        return 0;
    }

    /**
     * Jump to Subroutine
     * Push Program Counter to the Stack and Jump to Fetched Address
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int jsr() {
        program_counter--;
        program_counter &= 0xFFFF;

        pushStack((program_counter >> 8) & 0xFF);
        pushStack(program_counter & 0xFF);
        program_counter = addr_abs & 0xFFFF;

        return 0;
    }

    /**
     * Store the Fetched data into the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 1 Extra cycle may be required depending on the addressing mode
     */
    int lda() {
        fetch();
        accumulator = fetched & 0xFF;

        setFlag(Flags.Z, (accumulator & 0xFF) == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 1;
    }

    /**
     * Store the Fetched data into the X Register
     * Z Flag set if X Register = 0
     * N Flag set if X Register has MSB set
     *
     * @return 1 Extra cycle may be required depending on the addressing mode
     */
    int ldx() {
        fetch();
        x_register = fetched & 0xFF;

        setFlag(Flags.Z, (x_register & 0xFF) == 0x00);
        setFlag(Flags.N, (x_register & 0x80) == 0x80);

        return 1;
    }

    /**
     * Store the Fetched data into the Y Register
     * Z Flag set if Y Register = 0
     * N Flag set if Y Register has MSB set
     *
     * @return 1 Extra cycle may be required depending on the addressing mode
     */
    int ldy() {
        fetch();
        y_register = fetched & 0xFF;

        setFlag(Flags.Z, (y_register & 0xFF) == 0x00);
        setFlag(Flags.N, (y_register & 0x80) == 0x80);

        return 1;
    }

    /**
     * Shift Right the Fetched data and store it depending on the addressing mode
     * Z Flag set if Computed data = 0
     * N Flag set if Computed data has MSB set
     *
     * @return 0 No extra cycle required
     */
    int lsr() {
        fetch();
        setFlag(Flags.C, (fetched & 0x01) == 0x01);
        tmp = (fetched >> 1);

        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        if (opcodes.get(opcode).addr_mode == AddressingMode.IMP) accumulator = tmp & 0xFF;
        else write(addr_abs, tmp & 0xFF);

        return 0;
    }

    /**
     * Do nothing
     *
     * @return 1 if OPCode in { 0x1C; Ox3C; 0x5C, 0x7C, 0xDC, 0xFC} 0 otherwise
     */
    int nop() {
        switch (opcode) {
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

    /**
     * Compute a Logic OR between the Accumulator and the Fetched data
     * stores it into the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 1 Extra cycle may be required
     */
    int ora() {
        fetch();
        accumulator = (accumulator | fetched) & 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 1;
    }

    /**
     * Push the Accumulator to the Stack
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int pha() {
        pushStack(accumulator);
        return 0;
    }

    /**
     * Push the Status Register to the Stack
     * B Flag set
     * U Flag set
     *
     * @return 0 No extra cycle required
     */
    int php() {
        pushStack((status | Flags.U.value | Flags.B.value) & 0xFF);

        setFlag(Flags.B, false);
        setFlag(Flags.U, false);

        return 0;
    }

    /**
     * Pool the Accumulator from the Stack
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int pla() {
        accumulator = popStack();

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 0;
    }

    /**
     * Pool the Status Register from the Stack
     * U Flag set
     * All Flags set to the pulled Status Register value
     *
     * @return 0 No extra cycle required
     */
    int plp() {
        status = popStack();

        setFlag(Flags.U, true);

        return 0;
    }

    /**
     * Rotate Left the Fetched data and store it to the Accumulator
     * The LSB is set using the Carry Flag
     * C Flag set if Fetched has MSB set
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int rol() {
        fetch();
        tmp = (getFlag(Flags.C) ? 1 : 0) | (fetched << 1);

        setFlag(Flags.C, (tmp & 0xFF00) != 0x0000);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        if (opcodes.get(opcode).addr_mode == AddressingMode.IMP) accumulator = tmp & 0xFF;
        else write(addr_abs, tmp & 0xFF);

        return 0;
    }

    /**
     * Rotate Right the Fetched data and store it to the Accumulator
     * The MSB is set using the Carry Flag
     * C Flag set if Fetched has LSB set
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set (if C Flag was set)
     *
     * @return 0 No extra cycle required
     */
    int ror() {
        fetch();
        tmp = ((getFlag(Flags.C) ? 1 << 7 : 0) | fetched >> 1);

        setFlag(Flags.C, (fetched & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        if (opcodes.get(opcode).addr_mode == AddressingMode.IMP) accumulator = tmp & 0xFF;
        else write(addr_abs, tmp & 0xFF);

        return 0;
    }

    /**
     * Return from Interrupt Subroutine by pooling the Program Counter and Status Register from the Stack
     * B Flag unset
     * U Flag unset
     * All Flags set to the pulled Status Register value
     *
     * @return 0 No extra cycle required
     */
    int rti() {
        //Dummy read
        read(program_counter);
        status = popStack();
        program_counter = popStack();
        program_counter |= popStack() << 8;
        program_counter &= 0xFFFF;

        status &= ~Flags.B.value & 0x00FF;
        status &= ~Flags.U.value & 0x00FF;

        return 0;
    }

    /**
     * Return from Subroutine by pulling the Program Counter from the Stack
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int rts() {
        //Dummy read
        read(program_counter);
        program_counter = popStack();
        program_counter |= popStack() << 8;
        program_counter++;
        program_counter &= 0xFFFF;

        return 0;
    }

    /**
     * Subtract the fetched value from the Accumulator
     * C Flag set if Accumulator < Fetched
     * Z Flag set if Accumulator = Fetched
     * N Flag set if the final Accumulator has MSB set
     * V Flag set the same way as ADC
     *
     * @return 1 An extra cycle can be required depending on the addressing mode
     */
    int sbc() {
        fetch();
        int complement = (fetched ^ 0xFF);
        tmp = ((accumulator + complement + (getFlag(Flags.C) ? 0x1 : 0x0)) & 0x01FF);

        setFlag(Flags.C, tmp > 0xFF);
        setFlag(Flags.Z, (tmp & 0xFF) == 0);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);
        setFlag(Flags.V, ((tmp ^ accumulator) & (tmp ^ complement) & 0x80) == 0x80);

        accumulator = (tmp & 0xFF);

        return 1;
    }

    /**
     * Set the Carry Flag of the Status Register to 1
     * C Flag set
     *
     * @return 0 No extra cycle required
     */
    int sec() {
        setFlag(Flags.C, true);
        return 0;
    }

    /**
     * Set the Decimal Flag of the Status Register to 1
     * D Flag set
     *
     * @return 0 No extra cycle required
     */
    int sed() {
        setFlag(Flags.D, true);
        return 0;
    }

    /**
     * Set the Interrupt Flag of the Status Register to 1
     * I Flag set
     *
     * @return 0 No extra cycle required
     */
    int sei() {
        setFlag(Flags.I, true);
        return 0;
    }

    /**
     * Store the Accumulator to the fetched Address
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int sta() {
        write(addr_abs, accumulator);
        return 0;
    }

    /**
     * Store the X Register to the fetched Address
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int stx() {
        write(addr_abs, x_register);
        return 0;
    }

    /**
     * Store the Y Register to the fetched Address
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int sty() {
        write(addr_abs, y_register);
        return 0;
    }

    /**
     * Copy the Accumulator to the X Register
     * Z Flag set if X Register = 0
     * N Flag set if X Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int tax() {
        x_register = accumulator;
        x_register &= 0xFF;

        setFlag(Flags.Z, x_register == 0x00);
        setFlag(Flags.N, (x_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Copy the Accumulator to the Y Register
     * Z Flag set if y Register = 0
     * N Flag set if Y Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int tay() {
        y_register = accumulator;
        y_register &= 0xFF;

        setFlag(Flags.Z, y_register == 0x00);
        setFlag(Flags.N, (y_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Copy the Stack Pointer to the X Register
     * Z Flag set if X Register = 0
     * N Flag set if X Register has MSB set
     *
     * @return 0 No extra cycle required
     */
    int tsx() {
        x_register = stack_pointer;
        x_register &= 0xFF;

        setFlag(Flags.Z, x_register == 0x00);
        setFlag(Flags.N, (x_register & 0x80) == 0x80);

        return 0;
    }

    /**
     * Copy the X Register to the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int txa() {
        accumulator = x_register;
        accumulator &= 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 0;
    }

    /**
     * Copy the X Register to the Stack Pointer
     * No Flag Update
     *
     * @return 0 No extra cycle required
     */
    int txs() {
        stack_pointer = x_register;
        stack_pointer &= 0xFF;

        return 0;
    }

    /**
     * Copy the Y Register to the Accumulator
     * Z Flag set if Accumulator = 0
     * N Flag set if Accumulator has MSB set
     *
     * @return 0 No extra cycle required
     */
    int tya() {
        accumulator = y_register;
        accumulator &= 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        return 0;
    }

    int aso() {
        asl();
        ora();
        return 0;
    }

    int rla() {
        rol();
        and();

        return 0;
    }

    int lse() {
        lsr();
        eor();

        return 0;
    }

    int rra() {
        ror();
        adc();

        return 0;
    }

    int axs() {
        write(addr_abs, accumulator & x_register);

        return 0;
    }

    int lax() {
        lda();
        ldx();

        return 1;
    }

    int dcm() {
        dec();
        cmp();

        return 0;
    }

    int ins() {
        inc();
        sbc();

        return 0;
    }

    int alr() {
        and();
        tmp = (accumulator >> 1);

        setFlag(Flags.C, (accumulator & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        accumulator = tmp & 0xFF;
        return 0;
    }

    int arr() {
        and();
        fetch();
        tmp = ((getFlag(Flags.C) ? 1 << 7 : 0) | accumulator >> 1);

        setFlag(Flags.C, (accumulator & 0x01) == 0x01);
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        accumulator = tmp & 0xFF;

        return 0;
    }

    int xaa() {
        txa();
        and();

        return 0;
    }

    int oal() {
        accumulator = (accumulator | read(0x00EE)) & 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) == 0x80);

        and();
        tax();

        return 0;
    }

    int sax() {
        fetch();
        tmp = (accumulator & x_register) & 0xFF;

        setFlag(Flags.C, tmp >= fetched);
        tmp -= fetched;
        setFlag(Flags.Z, (tmp & 0xFF) == 0x00);
        setFlag(Flags.N, (tmp & 0x80) == 0x80);

        x_register = tmp & 0xFF;

        return 0;
    }

    int skb() {
        program_counter++;
        program_counter &= 0xFFFF;

        return 0;
    }

    int skw() {
        fetch();
        skb();
        fetch();
        skb();

        return 1;
    }

    int hlt() {
        halted = true;

        return 0;
    }

    int tas() {
        tmp = (accumulator & x_register) & 0xFF;
        pushStack(tmp);
        tmp = (tmp & (read(program_counter-1) + 1)) & 0xFF;

        write(addr_abs, tmp);

        return 0;
    }

    int say() {
        tmp = (y_register & (read(program_counter-1) + 1)) & 0xFF;

        write(addr_abs, tmp);

        return 0;
    }

    int xas() {
        tmp = (x_register & (read(program_counter-1) + 1)) & 0xFF;

        write(addr_abs, tmp);

        return 0;
    }

    int axa() {
        tmp = (accumulator & x_register & (read(program_counter-1) + 1)) & 0xFF;

        write(addr_abs, tmp);

        return 0;
    }

    int anc() {
        fetch();
        accumulator = (accumulator & fetched) & 0xFF;

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) != 0x00);
        setFlag(Flags.C, (accumulator & 0x80) != 0x00);

        return 0;
    }

    int las() {
        fetch();
        accumulator = (fetched & stack_pointer);

        setFlag(Flags.Z, accumulator == 0x00);
        setFlag(Flags.N, (accumulator & 0x80) != 0x00);

        return 0;
    }


    // ========================================================= Utility Methods ========================================================= //

    /**
     * Execute one tick of the CPU
     */
    public void clock() {
        if (!halted) {
            //If the CPU has finished the last Instruction
            if (cycles <= 0) {

                //Fetch the Operation Code
                opcode = read(program_counter);
                setFlag(Flags.U, true);
                //Increment the Program Counter
                program_counter++;
                program_counter &= 0xFFFF;
                //Get the Instruction
                Instruction instr = opcodes.get(opcode);
                //Set the required number of cycle for this instruction
                cycles = instr.cycles;
                //Execute the Instruction (Fetch data + treatment)
                int additional_cycle_1 = instr.addrmode();
                int additional_cycle_2 = instr.operate();
                //If the Instruction is susceptible of requiring an extra cycle and the addressing mode require one, the the Instruction require an extra cycle
                cycles += (additional_cycle_1 & additional_cycle_2);
                setFlag(Flags.U, true);
            }
            //Decrement the remaining busy cycle index
            cpu_clock++;
            cycles--;
        }
    }

    /**
     * Reset the CPU to the default state
     */
    public void reset() {
        halted = false;
        stack_pointer -= 3;
        status |= Flags.I.value;

        program_counter = resetVector();

        addr_rel = 0x0000;
        addr_abs = 0x0000;
        fetched = 0x00;

        cycles = 8;
    }

    public void startup() {
        halted = false;
        accumulator = 0x00;
        x_register = 0x00;
        y_register = 0x00;
        stack_pointer = 0xFD;
        status = Flags.I.value | Flags.B.value | Flags.U.value;

        program_counter = resetVector();

        addr_rel = 0x0000;
        addr_abs = 0x0000;
        fetched = 0x00;
    }

    /**
     * Trigger an Interrupt
     */
    public void irq() {
        //The Interrupt is trigger only if they aren't disable (I Flag of the Status Register)
        if (!getFlag(Flags.I) && !halted) {
            //Push the current Program Counter to the Stack LSB first
            pushStack((program_counter >> 8) & 0xFF);
            pushStack(program_counter & 0xFF);

            //Push the current Status Register to the Stack
            setFlag(Flags.B, false);
            setFlag(Flags.U, true);
            setFlag(Flags.I, true);
            pushStack(status);

            //Jump to the NMI Routine specified at 0xFFFA
            program_counter = irqVector();

            // An Interrupt take 7 cycles
            cycles = 7;
        }
    }

    /**
     * Trigger a Non Maskable Interrupt
     */
    public void nmi() {
        if (!halted) {
            //Push the current Program Counter to the Stack LSB first
            pushStack((program_counter >> 8) & 0xFF);
            pushStack(program_counter & 0xFF);

            //Push the current Status Register to the Stack
            setFlag(Flags.B, false);
            setFlag(Flags.U, true);
            setFlag(Flags.I, true);
            pushStack(status);

            //Jump to the NMI Routine specified at 0xFFFA
            program_counter = nmiVector();

            //An NMI take 8 cycles
            cycles = 8;
        }
    }

    /**
     * Update the fetched data used according to the current Instruction addressing mode
     */
    private void fetch() {
        if (opcodes.get(opcode).addr_mode != AddressingMode.IMP)
            fetched = read(addr_abs);
    }

    /**
     * Push data to the stack
     * Update the Stack Pointer accordingly
     *
     * @param data the data to push
     */
    private void pushStack(int data) {
        write(0x0100 + stack_pointer, data);
        stack_pointer--;
        stack_pointer &= 0xFF;
    }

    /**
     * Pop data from the stack
     * Update the Stack Pointer accordingly
     *
     * @return the popped data
     */
    private int popStack() {
        stack_pointer++;
        stack_pointer &= 0xFF;
        return read(0x0100 + stack_pointer);
    }

    /**
     * Return the Reset Vector stored at (0xFFFA-0xFFFB)
     *
     * @return the Non Maskable Interrupt Vector
     */
    private int resetVector() {
        int low = read(0xFFFC);
        int high = read(0xFFFD);
        return (high << 8 | low);
    }

    /**
     * Return the Non Maskable Interrupt Vector stored at (0xFFFA-0xFFFB)
     *
     * @return the Non Maskable Interrupt Vector
     */
    private int nmiVector() {
        int low = read(0xFFFA);
        int high = read(0xFFFB);
        return (high << 8 | low);
    }

    /**
     * Return the Interrupt Vector stored at (0xFFFE-0xFFFF)
     *
     * @return the Interrupt Vector
     */
    private int irqVector() {
        int low = read(0xFFFE);
        int high = read(0xFFFF);
        return (high << 8 | low);
    }

    // ========================================================== Debug Methods ========================================================== //

    /**
     * Disassemble an address range into readable 6502 assembly
     *
     * @param start range start address
     * @param separator separator sequence put between each element
     * @return a Map with addresses as Keys and Instructions as Values
     */
    public Map<Integer, String> disassemble(int start, int length, String separator) {
        int addr = start;
        int order_index = 0;
        IntegerWrapper line_size = new IntegerWrapper();

        Map<Integer, String> code = new TreeMap<>();
        while (code.size() <= length) {
            code.put(addr | (order_index << 16), decompileInstruction(addr, line_size, separator));
            addr += line_size.value;
            order_index++;
        }
        return code;
    }

    private String decompileInstruction(int addr, IntegerWrapper instrSize, String separator) {
        int low, high;
        String line = String.format("$%04X:" + separator, addr);
        int opcode = nes.cpuRead(addr, true);
        addr = (addr + 1) & 0x1FFFF;
        Instruction instr = opcodes.get(opcode);
        if (instr.assembly != OPCode.XXX)
            line += instr.assembly + separator;
        else
            line += "???" + separator;
        switch (instr.addr_mode) {
            case IMP:
                line += separator + "{IMP}" + separator + String.format("%02X", instr.opcode);
                instrSize.value = 1;
                break;
            case IMM:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("#$%02X" + separator + "{IMM}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case ZP0:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("$%02X" + separator + "{ZP0}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case ZPX:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("$%02X, X" + separator + "{ZPX}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case ZPY:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("$%02X, Y" + separator + "{ZPY}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case IZX:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("($%02X, X)" + separator + "{IZX}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case IZY:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("($%02X), Y" + separator + "{IZY}", low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
                break;
            case ABS:
                low = nes.cpuRead(addr, true);
                addr = (addr + 1) & 0x1FFFF;
                high = nes.cpuRead(addr, true);
                instrSize.value = 3;
                line += String.format("$%04X" + separator + "{ABS}", (high << 8) | low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low) + separator + String.format("%02X", high);
                break;
            case ABX:
                low = nes.cpuRead(addr, true);
                addr = (addr + 1) & 0x1FFFF;
                high = nes.cpuRead(addr, true);
                instrSize.value = 3;
                line += String.format("$%04X, X" + separator + "{ABX}", (high << 8) | low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low) + separator + String.format("%02X", high);
                break;
            case ABY:
                low = nes.cpuRead(addr, true);
                addr = (addr + 1) & 0x1FFFF;
                high = nes.cpuRead(addr, true);
                instrSize.value = 3;
                line += String.format("$%04X, Y" + separator + "{ABY}", (high << 8) | low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low) + separator + String.format("%02X", high);
                break;
            case IND:
                low = nes.cpuRead(addr, true);
                addr = (addr + 1) & 0x1FFFF;
                high = nes.cpuRead(addr, true);
                instrSize.value = 3;
                line += String.format("($%04X)" + separator + "{IND}", (high << 8) | low) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low) + separator + String.format("%02X", high);
                break;
            case REL:
                low = nes.cpuRead(addr, true);
                instrSize.value = 2;
                line += String.format("$%02X ", low) + String.format("[$%04X]" + separator + "{IND}", addr + 1 + (byte) (low)) + separator + String.format("%02X", instr.opcode) + separator + String.format("%02X", low);
        }
        return line + separator + instr.assembly.type;
    }

    /**
     * Return whether or not the current instruction is complete
     *
     * @return is the current instruction complete
     */
    public boolean complete() {
        return cycles == 0;
    }

    /**
     * Return the current Accumulator value as an 8bit unsigned value
     *
     * @return the current Y Accumulator value as an 8bit unsigned value
     */
    public int getAccumulator() {
        return accumulator;
    }

    /**
     * Return the current X Register value as an 8bit unsigned value
     *
     * @return the current X Register value as an 8bit unsigned value
     */
    public int getXRegister() {
        return x_register;
    }

    /**
     * Return the current Y Register value as an 8bit unsigned value
     *
     * @return the current Y Register value as an 8bit unsigned value
     */
    public int getYRegister() {
        return y_register;
    }

    /**
     * Return the current Stack Pointer as an 8bit unsigned value
     *
     * @return the current Stack Pointer as an 8bit unsigned value
     */
    public int getStackPointer() {
        return stack_pointer;
    }

    /**
     * Return the current Program Counter
     *
     * @return the current Program Counter
     */
    public int getStatus() {
        return status;
    }

    /**
     * Return the current Program Counter as a 16bit unsigned value
     *
     * @return the current Program Counter as a 16bit unsigned value
     */
    public int getProgramCounter() {
        return program_counter;
    }
}
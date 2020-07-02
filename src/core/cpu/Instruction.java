package core.cpu;

/**
 * This class represent an Instruction that can be fetched and executed by the CPU
 */
public class Instruction {

    private final CPU_6502 cpu;
    final OPCode assembly;
    final AddressingMode addr_mode;
    final int opcode;
    final int cycles;

    Instruction(OPCode name, AddressingMode addr_mode, int opcode, int cycles, CPU_6502 cpu) {
        this.assembly = name;
        this.addr_mode = addr_mode;
        this.cycles = cycles;
        this.opcode = opcode;
        this.cpu = cpu;
    }

    /**
     * Execute the instruction
     *
     * @return 1 if the operation is susceptible of requiring an extra cycle 0 otherwise
     */
    int operate() {
        switch(assembly) {
            case ADC:
                return cpu.adc();
            case AND:
                return cpu.and();
            case ASL:
                return cpu.asl();
            case BCC:
                return cpu.bcc();
            case BCS:
                return cpu.bcs();
            case BEQ:
                return cpu.beq();
            case BIT:
                return cpu.bit();
            case BMI:
                return cpu.bmi();
            case BNE:
                return cpu.bne();
            case BPL:
                return cpu.bpl();
            case BRK:
                return cpu.brk();
            case BVC:
                return cpu.bvc();
            case BVS:
                return cpu.bvs();
            case CLC:
                return cpu.clc();
            case CLD:
                return cpu.cld();
            case CLI:
                return cpu.cli();
            case CLV:
                return cpu.clv();
            case CMP:
                return cpu.cmp();
            case CPX:
                return cpu.cpx();
            case CPY:
                return cpu.cpy();
            case DEC:
                return cpu.dec();
            case DEX:
                return cpu.dex();
            case DEY:
                return cpu.dey();
            case EOR:
                return cpu.eor();
            case INC:
                return cpu.inc();
            case INX:
                return cpu.inx();
            case INY:
                return cpu.iny();
            case JMP:
                return cpu.jmp();
            case JSR:
                return cpu.jsr();
            case LDA:
                return cpu.lda();
            case LDX:
                return cpu.ldx();
            case LDY:
                return cpu.ldy();
            case LSR:
                return cpu.lsr();
            case NOP:
                return cpu.nop();
            case ORA:
                return cpu.ora();
            case PHA:
                return cpu.pha();
            case PHP:
                return cpu.php();
            case PLA:
                return cpu.pla();
            case PLP:
                return cpu.plp();
            case ROL:
                return cpu.rol();
            case ROR:
                return cpu.ror();
            case RTI:
                return cpu.rti();
            case RTS:
                return cpu.rts();
            case SBC:
                return cpu.sbc();
            case SEC:
                return cpu.sec();
            case SED:
                return cpu.sed();
            case SEI:
                return cpu.sei();
            case STA:
                return cpu.sta();
            case STX:
                return cpu.stx();
            case STY:
                return cpu.sty();
            case TAX:
                return cpu.tax();
            case TAY:
                return cpu.tay();
            case TSX:
                return cpu.tsx();
            case TXA:
                return cpu.txa();
            case TXS:
                return cpu.txs();
            case TYA:
                return cpu.tya();
            case ASO:
                return cpu.aso();
            case RLA:
                return cpu.rla();
            case LSE:
                return cpu.lse();
            case RRA:
                return cpu.rra();
            case AXS:
                return cpu.axs();
            case LAX:
                return cpu.lax();
            case DCM:
                return cpu.dcm();
            case INS:
                return cpu.ins();
            case ALR:
                return cpu.alr();
            case ARR:
                return cpu.arr();
            case XAA:
                return cpu.xaa();
            case OAL:
                return cpu.oal();
            case SAX:
                return cpu.sax();
            case SKB:
                return cpu.skb();
            case SKW:
                return cpu.skw();
            case HLT:
                return cpu.hlt();
            case TAS:
                return cpu.tas();
            case SAY:
                return cpu.say();
            case XAS:
                return cpu.xas();
            case AXA:
                return cpu.axa();
            case ANC:
                return cpu.anc();
            case LAS:
                return cpu.las();
        }
        return 0;
    }

    /**
     * Fetch the appropriate data
     *
     * @return 1 if the data gathering require an extra cycle 0 otherwise
     */
    int addrmode() {
        switch (addr_mode) {
            case IMP:
                return cpu.imp();
            case IMM:
                return cpu.imm();
            case ZP0:
                return cpu.zp0();
            case ZPX:
                return cpu.zpx();
            case ZPY:
                return cpu.zpy();
            case IZX:
                return cpu.izx();
            case IZY:
                return cpu.izy();
            case ABS:
                return cpu.abs();
            case ABX:
                return cpu.abx();
            case ABY:
                return cpu.aby();
            case IND:
                return cpu.ind();
            case REL:
                return cpu.rel();
        }
        return 0;
    }
}
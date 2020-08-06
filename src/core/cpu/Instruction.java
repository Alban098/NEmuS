package core.cpu;

import java.util.function.Supplier;

/**
 * This class represent an Instruction that can be fetched and executed by the CPU
 */
public class Instruction {

    final OPCode assembly;
    final AddressingMode addr_mode;
    final int opcode;
    final int cycles;
    final Supplier<Integer> fct_addr_mode;
    final Supplier<Integer> fct_operate;

    Instruction(OPCode name, AddressingMode addr_mode, int opcode, int cycles, CPU_6502 cpu) {
        this.assembly = name;
        this.addr_mode = addr_mode;
        this.cycles = cycles;
        this.opcode = opcode;
        switch (addr_mode) {
            case IMP -> fct_addr_mode = cpu::imp;
            case IMM -> fct_addr_mode = cpu::imm;
            case ZP0 -> fct_addr_mode = cpu::zp0;
            case ZPX -> fct_addr_mode = cpu::zpx;
            case ZPY -> fct_addr_mode = cpu::zpy;
            case IZX -> fct_addr_mode = cpu::izx;
            case IZY -> fct_addr_mode = cpu::izy;
            case ABS -> fct_addr_mode = cpu::abs;
            case ABX -> fct_addr_mode = cpu::abx;
            case ABY -> fct_addr_mode = cpu::aby;
            case IND -> fct_addr_mode = cpu::ind;
            case REL -> fct_addr_mode = cpu::rel;
            default -> fct_addr_mode = () -> 0;
        }
        switch (assembly) {
            case ADC -> fct_operate = cpu::adc;
            case AND -> fct_operate = cpu::and;
            case ASL -> fct_operate = cpu::asl;
            case BCC -> fct_operate = cpu::bcc;
            case BCS -> fct_operate = cpu::bcs;
            case BEQ -> fct_operate = cpu::beq;
            case BIT -> fct_operate = cpu::bit;
            case BMI -> fct_operate = cpu::bmi;
            case BNE -> fct_operate = cpu::bne;
            case BPL -> fct_operate = cpu::bpl;
            case BRK -> fct_operate = cpu::brk;
            case BVC -> fct_operate = cpu::bvc;
            case BVS -> fct_operate = cpu::bvs;
            case CLC -> fct_operate = cpu::clc;
            case CLD -> fct_operate = cpu::cld;
            case CLI -> fct_operate = cpu::cli;
            case CLV -> fct_operate = cpu::clv;
            case CMP -> fct_operate = cpu::cmp;
            case CPX -> fct_operate = cpu::cpx;
            case CPY -> fct_operate = cpu::cpy;
            case DEC -> fct_operate = cpu::dec;
            case DEX -> fct_operate = cpu::dex;
            case DEY -> fct_operate = cpu::dey;
            case EOR -> fct_operate = cpu::eor;
            case INC -> fct_operate = cpu::inc;
            case INX -> fct_operate = cpu::inx;
            case INY -> fct_operate = cpu::iny;
            case JMP -> fct_operate = cpu::jmp;
            case JSR -> fct_operate = cpu::jsr;
            case LDA -> fct_operate = cpu::lda;
            case LDX -> fct_operate = cpu::ldx;
            case LDY -> fct_operate = cpu::ldy;
            case LSR -> fct_operate = cpu::lsr;
            case NOP -> fct_operate = cpu::nop;
            case ORA -> fct_operate = cpu::ora;
            case PHA -> fct_operate = cpu::pha;
            case PHP -> fct_operate = cpu::php;
            case PLA -> fct_operate = cpu::pla;
            case PLP -> fct_operate = cpu::plp;
            case ROL -> fct_operate = cpu::rol;
            case ROR -> fct_operate = cpu::ror;
            case RTI -> fct_operate = cpu::rti;
            case RTS -> fct_operate = cpu::rts;
            case SBC -> fct_operate = cpu::sbc;
            case SEC -> fct_operate = cpu::sec;
            case SED -> fct_operate = cpu::sed;
            case SEI -> fct_operate = cpu::sei;
            case STA -> fct_operate = cpu::sta;
            case STX -> fct_operate = cpu::stx;
            case STY -> fct_operate = cpu::sty;
            case TAX -> fct_operate = cpu::tax;
            case TAY -> fct_operate = cpu::tay;
            case TSX -> fct_operate = cpu::tsx;
            case TXA -> fct_operate = cpu::txa;
            case TXS -> fct_operate = cpu::txs;
            case TYA -> fct_operate = cpu::tya;
            case ASO -> fct_operate = cpu::aso;
            case RLA -> fct_operate = cpu::rla;
            case LSE -> fct_operate = cpu::lse;
            case RRA -> fct_operate = cpu::rra;
            case AXS -> fct_operate = cpu::axs;
            case LAX -> fct_operate = cpu::lax;
            case DCM -> fct_operate = cpu::dcm;
            case INS -> fct_operate = cpu::ins;
            case ALR -> fct_operate = cpu::alr;
            case ARR -> fct_operate = cpu::arr;
            case XAA -> fct_operate = cpu::xaa;
            case OAL -> fct_operate = cpu::oal;
            case SAX -> fct_operate = cpu::sax;
            case SKB -> fct_operate = cpu::skb;
            case SKW -> fct_operate = cpu::skw;
            case HLT -> fct_operate = cpu::hlt;
            case TAS -> fct_operate = cpu::tas;
            case SAY -> fct_operate = cpu::say;
            case XAS -> fct_operate = cpu::xas;
            case AXA -> fct_operate = cpu::axa;
            case ANC -> fct_operate = cpu::anc;
            case LAS -> fct_operate = cpu::las;
            default -> fct_operate = () -> 0;
        }
    }

    /**
     * Execute the instruction
     *
     * @return 1 if the operation is susceptible of requiring an extra cycle 0 otherwise
     */
    int operate() {
        return fct_operate.get();
    }

    /**
     * Fetch the appropriate data
     *
     * @return 1 if the data gathering require an extra cycle 0 otherwise
     */
    int addrmode() {
        return fct_addr_mode.get();
    }
}
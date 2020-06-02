package core.cartridge;

import core.ppu.Mirror;
import utils.IntegerWrapper;

public class Mapper004 extends Mapper {

    private int targetRegister = 0x00;
    private boolean bPRGBankMode = false;
    private boolean bCHRInversion = false;

    private int[] register;
    private int[] chrBank;
    private int[] prgBank;

    private boolean irqActive = false;
    private boolean irqEnable = false;
    private boolean irqUpdate = false;
    private int irqCounter = 0x0000;
    private int irqReload = 0x0000;

    private int[] staticVRAM;

    private Mirror mirror = Mirror.HORIZONTAL;


    /**
     * Create a new instance of Mapper
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    Mapper004(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
        register = new int[8];
        chrBank = new int[8];
        prgBank = new int[4];
        staticVRAM = new int[32*1024];
        reset();
    }

    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            data.value = staticVRAM[addr & 0x1FFF] & 0xFF;
            return true;
        }
        if (addr >= 0x8000 && addr <= 0x9FFF) {
            mapped.value = prgBank[0] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xA000 && addr <= 0xBFFF) {
            mapped.value = prgBank[1] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xC000 && addr <= 0xDFFF) {
            mapped.value = prgBank[2] + (addr & 0x1FFF);
            return true;
        }
        if (addr >= 0xE000 && addr <= 0xFFFF) {
            mapped.value = prgBank[3] + (addr & 0x1FFF);
            return true;
        }
        return false;
    }

    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        if (addr >= 0x6000 && addr <= 0x7FFF) {
            mapped.value = -1;
            staticVRAM[addr & 0x1FFF] = data & 0xFF;
            return true;
        }

        if (addr >= 0x8000 && addr <= 0x9FFF) {
            if ((addr & 0x1) != 0x1) {
                targetRegister = data & 0x7;
                bPRGBankMode = (data & 0x40) == 0x40;
                bCHRInversion = (data & 0x80) == 0x80;
            } else {
                register[targetRegister] = data;
                if (bCHRInversion) {
                    chrBank[0] = register[2] * 0x0400;
                    chrBank[1] = register[3] * 0x0400;
                    chrBank[2] = register[4] * 0x0400;
                    chrBank[3] = register[5] * 0x0400;
                    chrBank[4] = (register[0] & 0xFE) * 0x0400;
                    chrBank[5] = register[0] * 0x0400 + 0x0400;
                    chrBank[6] = (register[1] & 0xFE) * 0x0400;
                    chrBank[7] = register[1] * 0x0400 + 0x0400;
                } else {
                    chrBank[0] = (register[0] & 0xFE) * 0x0400;
                    chrBank[1] = register[0] * 0x0400 + 0x0400;
                    chrBank[2] = (register[1] & 0xFE) * 0x0400;
                    chrBank[3] = register[1] * 0x0400 + 0x0400;
                    chrBank[4] = register[2] * 0x0400;
                    chrBank[5] = register[3] * 0x0400;
                    chrBank[6] = register[4] * 0x0400;
                    chrBank[7] = register[5] * 0x0400;
                }

                if (bPRGBankMode) {
                    prgBank[2] = (register[6] & 0x3F) * 0x2000;
                    prgBank[0] = (nPRGBanks * 2 - 2) * 0x2000;
                } else {
                    prgBank[0] = (register[6] & 0x3F) * 0x2000;
                    prgBank[2] = (nPRGBanks * 2 - 2) * 0x2000;
                }
                prgBank[1] = (register[7] & 0x3F) * 0x2000;
                prgBank[3] = (nPRGBanks * 2 - 1) * 0x2000;
            }
            return false;
        }

        if (addr >= 0xA000 && addr <= 0xBFFF) {
            if ((addr & 0x1) != 0x1) {
                if ((data & 0x1) == 0x1)
                    mirror = Mirror.HORIZONTAL;
                else
                    mirror = Mirror.VERTICAL;
            } else {}
            return false;
        }
        if (addr >= 0xC000 && addr <= 0xDFFF) {
            if ((addr & 0x1) != 0x1)
                irqReload = data;
            else
                irqCounter = 0;
            return false;
        }
        if (addr >= 0xE000 && addr <= 0xFFFF) {
            if ((addr & 0x1) != 0x1) {
                irqEnable = false;
                irqActive = false;
            } else {
                irqEnable = true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        if (addr >= 0x0000 && addr <= 0x03FF) {
            mapped.value = chrBank[0] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x0400 && addr <= 0x07FF) {
            mapped.value = chrBank[1] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x0800 && addr <= 0x0BFF) {
            mapped.value = chrBank[2] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x0C00 && addr <= 0x0FFF) {
            mapped.value = chrBank[3] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x1000 && addr <= 0x13FF) {
            mapped.value = chrBank[4] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x1400 && addr <= 0x17FF) {
            mapped.value = chrBank[5] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x1800 && addr <= 0x1BFF) {
            mapped.value = chrBank[6] + (addr & 0x03FF);
            return true;
        }
        if (addr >= 0x1C00 && addr <= 0x1FFF) {
            mapped.value = chrBank[7] + (addr & 0x03FF);
            return true;
        }
        return false;
    }

    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        return false;
    }

    @Override
    public Mirror mirror() {
        return mirror;
    }

    @Override
    public boolean irqState() {
        return irqActive;
    }

    @Override
    public void irqClear() {
        irqActive = false;
    }

    @Override
    public void scanline() {
        if (irqCounter == 0) {
            irqCounter = irqReload;
        } else {
            irqCounter--;
        }
        if (irqCounter == 0 && irqEnable)
            irqActive = true;
    }

    @Override
    public void reset() {
        targetRegister = 0;
        bPRGBankMode = false;
        bCHRInversion = false;
        mirror = Mirror.HORIZONTAL;
        irqActive = false;
        irqEnable = false;
        irqUpdate = false;
        irqCounter = 0;
        irqReload = 0;

        for (int i = 0; i < 4; i++) prgBank[i] = 0;
        for (int i = 0; i < 8; i++) {chrBank[i] = 0; register[i] = 0;}

        prgBank[0] = 0;
        prgBank[1] = 0x2000;
        prgBank[2] = (nPRGBanks * 2 - 2) * 0x2000;
        prgBank[3] = (nPRGBanks * 2 - 1) * 0x2000;
    }
}

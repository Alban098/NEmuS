package core.cartridge;

import core.ppu.Mirror;
import utils.IntegerWrapper;


public class Mapper001 extends Mapper {

    private int selectedCHRBank4Low = 0x00;
    private int selectedCHRBank4High = 0x00;
    private int selectedCHRBank8 = 0x00;

    private int selectedPRGBank16Low = 0x00;
    private int selectedPRGBank16High = 0x00;
    private int selectedPRGBank32 = 0x00;

    private int loadRegister = 0x00;
    private int loadRegisterCount = 0x00;
    private int controlRegister = 0X00;

    private Mirror mirroring_mode = Mirror.HORIZONTAL;

    private int[] staticVRAM;

    /**
     * Create a new instance of Mapper
     *
     * @param nPRGBanks number of Program ROM Banks
     * @param nCHRBanks number of Character ROM Banks
     */
    Mapper001(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
        staticVRAM = new int[32 * 1024];
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

        if (addr >= 0x8000) {
            if ((controlRegister & 0b01000) == 0b01000) {
                if (addr <= 0xBFFF) {
                    mapped.value = (selectedPRGBank16Low * 0x4000) + (addr & 0x3FFF);
                    return true;
                } else if (addr <= 0xFFFF) {
                    mapped.value = (selectedPRGBank16High * 0x4000) + (addr & 0x3FFF);
                    return true;
                }
            } else {
                mapped.value = (selectedPRGBank32 * 0x8000) + (addr & 0x7FFF);
                return true;
            }
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

        if (addr >= 0x8000) {
            if ((data & 0x80) == 0x80) {
                loadRegister = 0x00;
                loadRegisterCount = 0;
                controlRegister = controlRegister | 0x0C;
            } else {
                loadRegister >>= 1;
                loadRegister |= ((data & 0x01) << 4);
                loadRegisterCount++;

                if (loadRegisterCount == 5) {
                    int targetRegister = (addr >> 13) & 0x03;
                    if (targetRegister == 0) {
                        controlRegister = loadRegister & 0x1F;
                        switch (controlRegister & 0x03) {
                            case 0: mirroring_mode = Mirror.ONESCREEN_LOW; break;
                            case 1: mirroring_mode = Mirror.ONESCREEN_HIGH; break;
                            case 2: mirroring_mode = Mirror.VERTICAL; break;
                            case 3: mirroring_mode = Mirror.HORIZONTAL; break;
                        }
                    } else if (targetRegister == 1) {
                        if ((controlRegister & 0b10000) == 0b10000)
                            selectedCHRBank4Low =  loadRegister & 0x1F;
                        else
                            selectedCHRBank8 =  loadRegister & 0x1E;
                    } else if (targetRegister == 2) {
                        if ((controlRegister & 0b10000) == 0b10000)
                            selectedCHRBank4High = loadRegister & 0x1F;
                    } else {
                        int prgMode = (controlRegister >> 2) & 0x03;
                        if (prgMode == 0 || prgMode == 1) {
                            selectedPRGBank32 = (loadRegister & 0x0E) >> 1;
                        } else if (prgMode == 2) {
                            selectedPRGBank16Low = 0;
                            selectedPRGBank16High = loadRegister & 0x0F;
                        } else {
                            selectedPRGBank16Low = loadRegister & 0x0F;
                            selectedPRGBank16High = nPRGBanks - 1;
                        }
                    }
                    loadRegister = 0x00;
                    loadRegisterCount = 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped, IntegerWrapper data) {
        addr &= 0xFFFF;
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            if (nCHRBanks == 0) {
                mapped.value = addr;
                return true;
            } else {
                if ((controlRegister & 0b10000) == 0b10000) {
                    if (addr <= 0x0FFF) {
                        mapped.value = (selectedCHRBank4Low * 0x1000) + (addr & 0x0FFF);
                        return true;
                    }
                    mapped.value = (selectedCHRBank4High * 0x1000) + (addr & 0x0FFF);
                    return true;
                } else {
                    mapped.value = (selectedCHRBank8 * 0x2000) + (addr & 0x1FFF);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped, int data) {
        addr &= 0xFFFF;
        if (addr <= 0x1FFF) {
            if (nCHRBanks == 0) {
                mapped.value = addr;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public Mirror mirror() {
        return mirroring_mode;
    }


    @Override
    public void reset() {
        controlRegister = 0x1C;
        loadRegister = 0x00;
        loadRegisterCount = 0x00;

        selectedCHRBank4Low = 0;
        selectedCHRBank4High = 0;
        selectedCHRBank8 = 0;

        selectedPRGBank16Low = 0;
        selectedPRGBank16High = nPRGBanks - 1;
        selectedPRGBank32 = 0;
    }
}

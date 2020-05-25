package cartridge;

import utils.IntegerWrapper;

public class Mapper000 extends Mapper {

    public Mapper000(int nPRGBanks, int nCHRBanks) {
        super(nPRGBanks, nCHRBanks);
    }

    @Override
    public boolean cpuMapRead(int addr, IntegerWrapper mapped) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    @Override
    public boolean cpuMapWrite(int addr, IntegerWrapper mapped) {
        if (addr >= 0x8000 && addr <= 0xFFFF) {
            mapped.value = addr & (nPRGBanks > 1 ? 0x7FFF : 0x3FFF);
            return true;
        }
        return false;
    }

    @Override
    public boolean ppuMapRead(int addr, IntegerWrapper mapped) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            mapped.value = addr;
            return true;
        }
        return false;
    }

    @Override
    public boolean ppuMapWrite(int addr, IntegerWrapper mapped) {
        return false;
    }
}

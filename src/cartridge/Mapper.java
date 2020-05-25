package cartridge;

import utils.IntegerWrapper;

public abstract class Mapper {

    protected int nPRGBanks;
    protected int nCHRBanks;

    public Mapper(int nPRGBanks, int nCHRBanks) {
        this.nPRGBanks = nPRGBanks;
        this.nCHRBanks = nCHRBanks;
    }

    public abstract boolean cpuMapRead(int addr, IntegerWrapper mapped);
    public abstract boolean cpuMapWrite(int addr, IntegerWrapper mapped);
    public abstract boolean ppuMapRead(int addr, IntegerWrapper mapped);
    public abstract boolean ppuMapWrite(int addr, IntegerWrapper mapped);
}

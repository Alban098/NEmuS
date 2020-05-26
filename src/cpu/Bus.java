package cpu;

import cartridge.Cartridge;
import graphics.PPU_2C02;
import utils.IntegerWrapper;

public class Bus {

    private long systemTicks = 0;

    private int[] ram;
    private CPU_6502 cpu;
    private PPU_2C02 ppu;
    private Cartridge cartridge;


    public Bus() {
        ram = new int[2048];
        for (int i = 0; i < 2048; i++)
            ram[i] = 0x0000;
        cpu = new CPU_6502();
        ppu = new PPU_2C02();
        cpu.connectBus(this);
    }


    public CPU_6502 getCpu() {
        return cpu;
    }

    public PPU_2C02 getPpu() {
        return ppu;
    }

    public void cpuWrite(int addr, int data) {
        if (cartridge.cpuWrite(addr, data)) {

        } else if (addr >= 0x0000 && addr <= 0x1FFF)
            ram[addr & 0x07FF] = data;
        else if (addr >= 0x2000 && addr <= 0x3FFF)
            ppu.cpuWrite(addr & 0x0007, data);

    }

    public int cpuRead(int addr) {
        return cpuRead(addr, false);
    }

    public int cpuRead(int addr, boolean readOnly) {
        IntegerWrapper data = new IntegerWrapper();
        if (cartridge.cpuRead(addr, data)) {

        } else if (addr >= 0x0000 && addr <= 0x1FFF)
            data.value = ram[addr & 0x07FF];
        else if (addr >= 0x2000 && addr <= 0x3FFF) {
            data.value = ppu.cpuRead(addr & 0x0007);
        }

        return data.value & 0x00FF;
    }

    public void insertCartridge(Cartridge cart) {
        this.cartridge = cart;
        ppu.connectCartridge(cartridge);
    }

    public void reset() {
        cpu.reset();
        systemTicks = 0;
    }

    public void clock() {
        ppu.clock();
        if (systemTicks % 3 == 0)
            cpu.clock();
        if (ppu.nmi())
            cpu.nmi();
        systemTicks++;
    }


    public void loadStringByte(String s, int i) {
        try {
            String[] code = s.split(" ");
            int addr = i;
            for (String hex : code) {
                if (addr >= 0x0000 && addr <= 0xFFFF) {
                    cpuWrite(addr, Short.valueOf(hex, 16));
                    addr++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading program");
        }
    }
}

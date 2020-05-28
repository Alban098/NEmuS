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

    public int[] controller;
    private int[] controller_state;

    private int dma_page = 0x00;
    private int dma_addr = 0x00;
    private int dma_data = 0x00;
    private boolean dam_transfer = false;
    private boolean dma_dummy = true;

    public Bus() {
        ram = new int[2048];
        for (int i = 0; i < 2048; i++)
            ram[i] = 0x0000;
        cpu = new CPU_6502();
        ppu = new PPU_2C02();
        controller = new int[2];
        controller_state = new int[2];
        cpu.connectBus(this);
    }


    public CPU_6502 getCpu() {
        return cpu;
    }

    public PPU_2C02 getPpu() {
        return ppu;
    }

    public void cpuWrite(int addr, int data) {
        if (cartridge.cpuWrite(addr, data)) {}
        else if (addr >= 0x0000 && addr <= 0x1FFF) {
            ram[addr & 0x07FF] = data; }
        else if (addr >= 0x2000 && addr <= 0x3FFF)
            ppu.cpuWrite(addr & 0x0007, data);
        else if (addr == 0x4014) {
            dma_page = data;
            dma_addr = 0x00;
            dam_transfer = true;
        } else if (addr >= 0x4016 && addr <= 0x4017)
            controller_state[addr & 0x0001] = controller[addr & 0x001];

    }

    public int cpuRead(int addr) {
        return cpuRead(addr, false);
    }

    public synchronized int threadSafeCpuRead(int addr) {
        return cpuRead(addr, true);
    }

    public int cpuRead(int addr, boolean readOnly) {
        IntegerWrapper data = new IntegerWrapper();
        if (cartridge.cpuRead(addr, data)) {}
        else if (addr >= 0x0000 && addr <= 0x1FFF)
            data.value = ram[addr & 0x07FF];
        else if (addr >= 0x2000 && addr <= 0x3FFF)
            data.value = ppu.cpuRead(addr & 0x0007, readOnly);
        else if (addr >= 0x4016 && addr <= 0x4017) {
            data.value = ((controller_state[addr & 0x0001] & 0x80) > 0) ? 0x1 : 0x0;
            controller_state[addr & 0x0001] <<= 1;
        }
        return data.value & 0x00FF;
    }

    public void insertCartridge(Cartridge cart) {
        this.cartridge = cart;
        ppu.connectCartridge(cartridge);
    }

    public void reset() {
        cpu.reset();
        ppu.reset();
        systemTicks = 0;
    }

    public void clock() {
        ppu.clock();
        if (systemTicks % 3 == 0)
            if (dam_transfer) {
                if (dma_dummy) {
                    if (systemTicks % 2 == 1)
                        dma_dummy = false;
                } else {
                    if (systemTicks % 2 == 0)
                        dma_data = cpuRead(dma_page << 8 | dma_addr);
                    else {
                        switch(dma_addr & 0x03) {
                            case 0x0:
                                ppu.getOams()[dma_addr >> 2].setY(dma_data);
                            case 0x1:
                                ppu.getOams()[dma_addr >> 2].setId(dma_data);
                            case 0x2:
                                ppu.getOams()[dma_addr >> 2].setAttribute(dma_data);
                            case 0x3:
                                ppu.getOams()[dma_addr >> 2].setX(dma_data);
                        }
                        dma_addr = (dma_addr + 1) & 0x00FF;
                        if (dma_addr == 0x00) {
                            dam_transfer = false;
                            dma_dummy = true;
                        }
                    }
                }
            } else
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

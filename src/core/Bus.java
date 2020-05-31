package core;

import core.cartridge.Cartridge;
import core.cpu.CPU_6502;
import core.ppu.PPU_2C02;
import exceptions.DumpException;
import utils.ByteWrapper;

/**
 * This class represent the Bus of the NES
 * it is the Core of the system and control everything
 */
public class Bus {

    public final byte[] controller;
    private long systemTicks = 0;
    private final byte[] ram;
    private final CPU_6502 cpu;
    private final PPU_2C02 ppu;
    private Cartridge cartridge;
    private final byte[] controller_state;

    private short dma_page = 0x00;
    private short dma_addr = 0x00;
    private short dma_data = 0x00;
    private boolean dma_transfer = false;
    private boolean dma_dummy = true;

    /**
     * Create a new Instance of Bus ready to be started
     */
    public Bus() {
        ram = new byte[2048];
        for (int i = 0; i < 2048; i++)
            ram[i] = 0x0000;
        cpu = new CPU_6502();
        ppu = new PPU_2C02();
        controller = new byte[2];
        controller_state = new byte[2];
        cpu.connectBus(this);
    }

    /**
     * Return a Pointer to the CPU instance
     *
     * @return the CPU
     */
    public CPU_6502 getCpu() {
        return cpu;
    }

    /**
     * Return a Pointer to the PPU instance
     *
     * @return the PPU
     */
    public PPU_2C02 getPpu() {
        return ppu;
    }


    /**
     * Write a value to the CPU Addressable range
     *
     * @param addr the Address to write to
     * @param data the data to write
     */
    public void cpuWrite(int addr, short data) {
        //If the Cartridge is interested we write the value and directly return
        if (!cartridge.cpuWrite(addr, data)) {
            //Write to RAM (8Kb addressable, mirror in 4 2Kb chunks)
            if (addr >= 0x0000 && addr <= 0x1FFF) {
                ram[addr & 0x07FF] = (byte) data;
            //Write PPU Register (8 values mirrored over the range)
            } else if (addr >= 0x2000 && addr <= 0x3FFF)
                ppu.cpuWrite(addr & 0x0007, data);
            //Write to DMA Register
            else if (addr == 0x4014) {
                dma_page = data;
                dma_addr = 0x00;
                dma_transfer = true;
                //When trying to write to controller register, we snapshot the current controller state
            } else if (addr >= 0x4016 && addr <= 0x4017)
                controller_state[addr & 0x0001] = controller[addr & 0x001];
        }
    }

    /**
     * Read a value from the CPU Addressable range
     * Used for dumping RAM from other Thread without altering the console state
     *
     * @param addr the Address to read from
     * @return the read value
     */
    public synchronized short threadSafeCpuRead(int addr) {
        return cpuRead(addr, true);
    }

    /**
     * Read a value from the CPU Addressable range
     *
     * @param addr     the Address to read from
     * @param readOnly is the reading action allowed to alter CPU/PPU state
     * @return the read value
     */
    public short cpuRead(int addr, boolean readOnly) {
        //The wrapper that will contain the Cartridge data if read from it
        ByteWrapper data = new ByteWrapper();
        //If the Cartridge is interested we return the value
        if (!cartridge.cpuRead(addr, data)) {
            //Read from RAM (8Kb addressable, mirror in 4 2Kb chunks)
            if (addr >= 0x0000 && addr <= 0x1FFF)
                data.value = ram[addr & 0x07FF];
                //Read PPU Register (8 values mirrored over the range)
            else if (addr >= 0x2000 && addr <= 0x3FFF)
                data.value = (byte) ppu.cpuRead(addr & 0x0007, readOnly);
                //Read the controllers
            else if (addr >= 0x4016 && addr <= 0x4017) {
                //Controller read is Serial, when read from, the value is shifted left
                data.value = (byte) (((controller_state[addr & 0x0001] & 0x80) > 0) ? 0x1 : 0x0);
                controller_state[addr & 0x0001] <<= 1;
            }
        }
        return (short) (data.value & 0xFF);
    }

    /**
     * Load a Cartridge into the console and link it to the PPU
     *
     * @param cart the Cartridge to load
     */
    public void insertCartridge(Cartridge cart) {
        this.cartridge = cart;
        ppu.connectCartridge(cartridge);
    }

    /**
     * Reset the console by resetting the CPU, the PPU and set the systemTicks to 0
     */
    public synchronized void reset() {
        cpu.reset();
        ppu.reset();
        cartridge.reset();
        systemTicks = 0;
    }

    /**
     * Compute a console clock, used be by Debug windows
     * it is Thread safe
     */
    public synchronized void debugClock() {
        synchronized (getPpu()) {
            synchronized (getCpu()) {
                clock();
            }
        }
    }

    /**
     * Compute one console tick
     * the PPU is clocked every times
     * the CPU is clocked one every 3 times
     */
    public void clock() {
        //The PPU is clocked every tick
        ppu.clock();
        //The CPU clock is 3 time slower than the PPU clock, so it is clocked every 3 ticks
        if (systemTicks % 3 == 0)
            //If a Direct Memory Access is occurring
            if (dma_transfer) {
                //Wait for the write clock cycle (DMA chip busy)
                if (dma_dummy) {
                    //When at the write cycle, the DMA chip is ready and wait the next cycle to start transferring
                    if (systemTicks % 2 == 1)
                        dma_dummy = false;
                    //If the transfer is occurring
                } else {
                    //On even cycles, we read from the selected CPU Memory Page
                    if (systemTicks % 2 == 0)
                        dma_data = cpuRead(dma_page << 8 | dma_addr, false);
                        //On odd cycles, we write the read data to the PPU Memory (OAM Memory)
                    else {
                        //One Object Attribute is represented as 4 8bit values [0x(x)FF (attr)FF  (tileId)FF (y)FF]
                        //We select which attribute is actually being written by taking the 2 lsb of the current DMA Address
                        //We select the OAM Entry index using the 5 lsb of the current DMA Address
                        switch (dma_addr & 0x03) {
                            case 0x0:
                                ppu.getOams()[dma_addr >> 2].setY(dma_data);
                            case 0x1:
                                ppu.getOams()[dma_addr >> 2].setId(dma_data);
                            case 0x2:
                                ppu.getOams()[dma_addr >> 2].setAttribute(dma_data);
                            case 0x3:
                                ppu.getOams()[dma_addr >> 2].setX(dma_data);
                        }
                        //The DMA address is automatically incremented
                        dma_addr = (short) ((dma_addr + 1) & 0x00FF);
                        //At the end of the page (aka 512 cycles) the DMA transfer is complete and the CPU can start again
                        if (dma_addr == 0x00) {
                            dma_transfer = false;
                            dma_dummy = true;
                        }
                    }
                }
                //If no Direct Memory Access is occurring, the CPU is clocked
            } else
                cpu.clock();
        //If the PPU triggers an Non Maskable Interrupt, it is propagated to the CPU (Vertical Blank)
        if (ppu.nmi())
            cpu.nmi();
        systemTicks++;
    }


    // ======================================= Savestates Methods ======================================= //

    /**
     * Return a dump of the CPU Status
     * that can be restored later
     *
     * @return a byte[7] containing the PPU Status
     */
    public byte[] dumpCPU() {
        return cpu.dumpStatus();
    }

    /**
     * Restore the CPU Status to a dumped state
     *
     * @param dump the dumped memory (Must be 7 bytes)
     * @throws DumpException when the dump size isn't 7 bytes
     */
    public void restoreCPUDump(byte[] dump) throws DumpException {
        cpu.restoreStatusDump(dump);
    }

    /**
     * Return a dump of the PPU Status
     * that can be restored later
     *
     * @return a byte[11] containing the PPU Status
     */
    public byte[] dumpPPU() {
        return ppu.dumpStatus();
    }

    /**
     * Restore the PPU Status to a dumped state
     *
     * @param dump the dumped memory (Must be 11 bytes)
     * @throws DumpException when the dump size isn't 11 bytes
     */
    public void restorePPUDump(byte[] dump) throws DumpException {
        ppu.restoreStatusDump(dump);
    }

    /**
     * Return a dump of the RAM
     * that can be restored later
     *
     * @return a byte[2048] containing the RAM
     */
    public byte[] dumpRAM() {
        byte[] dump = new byte[2048];
        System.arraycopy(ram, 0, dump, 0, 2048);
        return dump;
    }

    /**
     * Restore the RAM to a dumped state
     *
     * @param dump the dumped memory (Must be 2048 bytes)
     * @throws DumpException when the dump size isn't 2048 bytes
     */
    public void restoreRAMDump(byte[] dump) throws DumpException {
        if (dump.length != 2048)
            throw new DumpException("RAM size (" + dump.length + ") must be 2048 bytes");
        System.arraycopy(dump, 0, ram, 0, 2048);
    }

    /**
     * Return a dump of the RAM
     * that can be restored later
     *
     * @return a byte[10528] containing the VRAM
     */
    public byte[] dumpVRAM() {
        byte[] dump = new byte[10528];
        int index = 0;
        System.arraycopy(ppu.dumpPatternTables(), 0, dump, index, 8192);
        index += 8192;
        System.arraycopy(ppu.dumpNametables(), 0, dump, index, 2048);
        index += 2048;
        System.arraycopy(ppu.dumpPalette(), 0, dump, index, 32);
        index += 32;
        System.arraycopy(ppu.dumpOAM(), 0, dump, index, 256);
        return dump;
    }

    /**
     * Restore the RAM to a dumped state
     *
     * @param dump the dumped memory (Must be 10528 bytes)
     * @throws DumpException when the dump size isn't 10528 bytes
     */
    public void restoreVRAMDump(byte[] dump) throws DumpException {
        if (dump.length != 10528)
            throw new DumpException("VRAM size (" + dump.length + ") must be 10528 bytes");
        int index = 0;
        byte[] patterntables = new byte[8192];
        byte[] nametables = new byte[2048];
        byte[] palettes = new byte[32];
        byte[] oam = new byte[256];
        System.arraycopy(dump, index, patterntables, 0, 8192);
        ppu.restorePatternTablesDump(patterntables);
        index += 8192;
        System.arraycopy(dump, index, nametables, 0, 2048);
        ppu.restoreNametablesDump(nametables);
        index += 2048;
        System.arraycopy(dump, index, palettes, 0, 32);
        ppu.restorePaletteDump(palettes);
        index += 32;
        System.arraycopy(dump, index, oam, 0, 256);
        ppu.restoreOAMDump(oam);
    }

}

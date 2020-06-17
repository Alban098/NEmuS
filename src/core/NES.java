package core;

import core.apu.APU_2A03;
import core.cartridge.Cartridge;
import core.cpu.CPU_6502;
import core.ppu.PPU_2C02;
import gui.NEmuS;
import utils.IntegerWrapper;

/**
 * This class represent the Bus of the NES
 * it is the Core of the system and control everything
 */
public class NES {

    private static final long SAVE_INTERVAL = 20000;
    public final int[] controller;
    private final byte[] ram;
    private final CPU_6502 cpu;
    private final PPU_2C02 ppu;
    private final APU_2A03 apu;
    private final int[] controller_state;
    public double dAudioSample = 0.0;
    private long next_save = 0;
    private long systemTicks = 0;
    private Cartridge cartridge;
    private int dma_page = 0x00;
    private int dma_addr = 0x00;
    private int dma_data = 0x00;
    private boolean dma_transfer = false;
    private boolean dma_dummy = true;
    private double dAudioTime = 0.0;
    private double dAudioTimePerNESClock = 0.0;
    private double dAudioTimePerSystemSample = 0.0;
    private boolean sound_rendering = true;


    /**
     * Create a new Instance of Bus ready to be started
     */
    public NES() {
        ram = new byte[2048];
        for (int i = 0; i < 2048; i++)
            ram[i] = 0x0000;
        cpu = new CPU_6502();
        ppu = new PPU_2C02();
        apu = new APU_2A03();
        controller = new int[2];
        controller_state = new int[2];
        cpu.connectBus(this);
    }

    public void setSampleFreq(int sampleRate) {
        dAudioTimePerSystemSample = 1.0 / (double) sampleRate;
        dAudioTimePerNESClock = 1.0 / 5369318.0;
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
    public void cpuWrite(int addr, int data) {
        data &= 0xFF;
        addr &= 0xFFFF;
        //If the Cartridge is interested we write the value and directly return
        if (!cartridge.cpuWrite(addr, data)) {
            if (addr <= 0x1FFF) { //Write to RAM (8Kb addressable, mirror in 4 2Kb chunks)
                ram[addr & 0x07FF] = (byte) data;
            } else if (addr <= 0x3FFF) { //Write PPU Register (8 values mirrored over the range)
                ppu.cpuWrite(addr & 0x0007, data);
            } else if (addr <= 0x4013 || addr == 0x4015 || addr == 0x4017) { //  NES APU
                apu.cpuWrite(addr, data);
            } else if (addr == 0x4014) { //Write to DMA Register
                dma_page = data;
                dma_addr = 0;
                dma_transfer = true;
            } else if (addr == 0x4016) { //When trying to write to controller register, we snapshot the current controller state
                controller_state[data & 0x1] = controller[data & 0x1];
            }
        }
    }

    /**
     * Read a value from the CPU Addressable range
     * Used for dumping RAM from other Thread without altering the console state
     *
     * @param addr the Address to read from
     * @return the read value
     */
    public synchronized int threadSafeCpuRead(int addr) {
        return cpuRead(addr, true);
    }

    /**
     * Read a value from the CPU Addressable range
     *
     * @param addr     the Address to read from
     * @param readOnly is the reading action allowed to alter CPU/PPU state
     * @return the read value
     */
    public int cpuRead(int addr, boolean readOnly) {
        addr &= 0xFFFF;
        //The wrapper that will contain the Cartridge data if read from it
        IntegerWrapper data = new IntegerWrapper();
        //If the Cartridge is interested we return the value
        if (!cartridge.cpuRead(addr, data)) {
            if (addr <= 0x1FFF) //Read from RAM (8Kb addressable, mirror in 4 2Kb chunks)
                data.value = ram[addr & 0x07FF];
            else if (addr <= 0x3FFF)  //Read PPU Register (8 values mirrored over the range)
                data.value = ppu.cpuRead(addr & 0x0007, readOnly);
            else if (addr == 0x4015)
                data.value = apu.cpuRead(addr);
            else if (addr >= 0x4016 && addr <= 0x4017) { //Read the controllers
                //Controller read is Serial, when read from, the value is shifted left
                data.value = ((controller_state[addr & 0x0001] & 0x80) > 0) ? 0x1 : 0x0;
                controller_state[addr & 0x0001] <<= 1;
            }
        }
        return data.value & 0xFF;
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
        dma_page = 0x00;
        dma_addr = 0x00;
        dma_data = 0x00;
        dma_dummy = true;
        dma_transfer = false;
    }

    /**
     * Set the console to its default boot up state
     */
    public void startup() {
        cpu.startup();
        apu.startup();
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
    public boolean clock() {
        //The PPU and APU are clocked every tick
        ppu.clock();
        apu.clock(!NEmuS.DEBUG_MODE && sound_rendering);
        //The CPU clock is 3 time slower than the PPU clock, so it is clocked every 3 ticks
        if (systemTicks % 3 == 0) {
            //If a Direct Memory Access is occurring
            if (dma_transfer) {
                if (dma_dummy) { //Wait for the write clock cycle (DMA chip busy)
                    //When at the write cycle, the DMA chip is ready and wait the next cycle to start transferring
                    if (systemTicks % 2 == 1)
                        dma_dummy = false;
                } else { //If the transfer is occurring
                    int oam_addr = ppu.getOamAddr();
                    if (systemTicks % 2 == 0) //On even cycles, we read from the selected CPU Memory Page
                        dma_data = cpuRead(dma_page << 8 | dma_addr, false);
                    else { //On odd cycles, we write the read data to the PPU Memory (OAM Memory)
                        //One Object Attribute is represented as 4 8bit values [0x(x)FF (attr)FF  (tileId)FF (y)FF]
                        //We select which attribute is actually being written by taking the 2 lsb of the current DMA Address
                        //We select the OAM Entry index using the 5 lsb of the current DMA Address
                        switch ((dma_addr) & 0x03) {
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
                        dma_addr++;
                        dma_addr &= 0xFF;
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
        }

        boolean audioSampleReady = false;
        dAudioTime += dAudioTimePerNESClock;
        //We verify if it is time to calculate an audio sample
        if (dAudioTime >= dAudioTimePerSystemSample) {
            //If so we set the time for the next audio sample and compute the current one
            dAudioTime -= dAudioTimePerSystemSample;
            dAudioSample = apu.getSample();
            audioSampleReady = true;
        }

        //If the PPU triggers an Non Maskable Interrupt, it is propagated to the CPU (Vertical Blank)
        if (ppu.nmi())
            cpu.nmi();
        //If the APU or Cartridge need to trigger an IRQ, we propagate it
        if (apu.irq())
            cpu.irq();
        if (cartridge.getMapper().irqState()) {
            cartridge.getMapper().irqClear();
            cpu.irq();
        }

        //If it is time to save the Cartridge, we trigger that event
        if (System.currentTimeMillis() >= next_save) {
            cartridge.save();
            next_save = System.currentTimeMillis() + SAVE_INTERVAL;
        }
        systemTicks++;

        return audioSampleReady;
    }

    /**
     * Return a reference to the currently inserted Cartridge
     *
     * @return the currently inserted Cartridge
     */
    public Cartridge getCartridge() {
        return cartridge;
    }

    /**
     * Enable or disable audio rendering
     *
     * @param enabled should the audio rendering be enabled
     */
    public void enableSoundRendering(boolean enabled) {
        this.sound_rendering = enabled;
    }

    /**
     * Enable or Disable RAW Audio mode
     *
     * @param raw should RAW Audio be triggered or not
     */
    public void toggleRawAudio(boolean raw) {
        apu.enabledRawMode(raw);
    }
}

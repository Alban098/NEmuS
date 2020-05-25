package cpu;

public class Bus {

    private short[] ram;
    private CPU_6502 cpu;

    public Bus() {
        ram = new short[64 * 1024];
        for (int i = 0; i < 64 * 1024; i++)
            ram[i] = 0x0000;
        cpu = new CPU_6502();
        cpu.connectBus(this);
    }

    public short[] getRam() {
        return ram;
    }

    public CPU_6502 getCpu() {
        return cpu;
    }

    public void write(int addr, short data) {
        if (addr >= 0x0000 && addr <= 0xFFFF)
            ram[addr] = data;
    }

    public short read(int addr) {
        return read(addr, false);
    }

    public short read(int addr, boolean readOnly) {
        if (addr >= 0x0000 && addr <= 0xFFFF)
            return ram[addr];
        return 0x0000;
    }

    public void loadStringByte(String s, int i) {
        try {
            String[] code = s.split(" ");
            int addr = i;
            for (String hex : code) {
                if (addr >= 0x0000 && addr <= 0xFFFF) {
                    write(addr, Short.valueOf(hex, 16));
                    addr++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading program");
        }
    }


}

package core.apu;

import core.apu.channels.NoiseChannel;
import core.apu.channels.PulseChannel;

public class APU_2A03 {

    public static int[] length_table = {10, 254, 20,  2, 40,  4, 80,  6, 160, 8, 60, 10, 14, 12, 26, 14, 12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30 };

    private PulseChannel pulse_1;
    private PulseChannel pulse_2;
    private NoiseChannel noise;

    private int clock_counter = 0;
    private double dGlobalTime = 0.0;
    private int frame_counter = 0;

    public APU_2A03() {
        pulse_1 = new PulseChannel();
        pulse_2 = new PulseChannel();
        noise = new NoiseChannel();
    }

    public double getSample() {
        return (pulse_1.output - 0.8) * 0.1 + (pulse_2.output - 0.8) * 0.1 + (2.0 * noise.output - 0.5) * 0.1;
    }

    public void cpuWrite(int addr, int data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        switch (addr)
        {
            case 0x4000:
                pulse_1.writeDutyCycle(data);
                break;
            case 0x4001:
                pulse_1.writeSweep(data);
                break;
            case 0x4002:
                pulse_1.writeTimerLow(data);
                break;
            case 0x4003:
                pulse_1.writeTimerHigh(data);
                pulse_1.writeLengthCounterLoad(data);
                break;
            case 0x4004:
                pulse_2.writeDutyCycle(data);
                break;
            case 0x4005:
                pulse_2.writeSweep(data);
                break;
            case 0x4006:
                pulse_2.writeTimerLow(data);
                break;
            case 0x4007:
                pulse_2.writeTimerHigh(data);
                pulse_2.writeLengthCounterLoad(data);
                break;
            case 0x4008:
                break;
            case 0x400C:
                noise.updateReload(data);
                break;
            case 0x400E:
               break;
            case 0x4015:
                pulse_1.enabled = (data & 0x1) == 1;
                pulse_2.enabled = (data & 0x2) == 2;
                noise.enabled = (data & 0x4) == 4;
                break;
            case 0x400F:
                pulse_1.envelope.started = true;
                pulse_2.envelope.started = true;
                noise.envelope.started = true;
                noise.lengthCounter.counter = length_table[(data & 0xF8) >> 3];
                break;
        }
    }

    public int cpuRead(int addr) {
        int data = 0x00;
        if (addr == 0x4015) {
            //	data |= (pulse1_lc.counter > 0) ? 0x01 : 0x00;
            //	data |= (pulse2_lc.counter > 0) ? 0x02 : 0x00;
            //	data |= (noise_lc.counter > 0) ? 0x04 : 0x00;
        }
        return data;
    }

    public void clock() {
        boolean quarterFrameClock = false;
        boolean halfFrameClock = false;

        dGlobalTime += .333333333 / 1789773.0;

        if (clock_counter % 6 == 0) {
            frame_counter++;
            if (frame_counter == 3729) {
                quarterFrameClock = true;
            }

            if (frame_counter == 7457) {
                quarterFrameClock = true;
                halfFrameClock = true;
            }

            if (frame_counter == 11186) {
                quarterFrameClock = true;
            }

            if (frame_counter == 14916) {
                quarterFrameClock = true;
                halfFrameClock = true;
                frame_counter = 0;
            }

            if (quarterFrameClock) {
                pulse_1.envelope.clock(pulse_1.halted);
                pulse_2.envelope.clock(pulse_2.halted);
                noise.envelope.clock(noise.halted);
            }
            if (halfFrameClock) {
                pulse_1.lengthCounter.clock(pulse_1.enabled, pulse_1.halted);
                pulse_2.lengthCounter.clock(pulse_2.enabled, pulse_2.halted);
                noise.lengthCounter.clock(noise.enabled, noise.halted);
                pulse_1.sweeper.clock(pulse_1.sequencer.reload, false);
                pulse_2.sweeper.clock(pulse_2.sequencer.reload, true);
            }
            pulse_1.compute(dGlobalTime);
            pulse_2.compute(dGlobalTime);
            noise.compute();
        }
        pulse_1.sweeper.track(pulse_1.sequencer.reload);
        pulse_2.sweeper.track(pulse_2.sequencer.reload);
        clock_counter++;
    }

    public void reset() {

    }
}

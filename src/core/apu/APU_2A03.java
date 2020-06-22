package core.apu;

import core.NES;
import core.apu.channels.DMCChannel;
import core.apu.channels.NoiseChannel;
import core.apu.channels.PulseChannel;
import core.apu.channels.TriangleChannel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represent the APU of the NES
 * it handle everything sound related
 */
public class APU_2A03 {

    private static final int VISUALIZER_SAMPLE_SIZE = 128;

    private static final double clock_time = .333333333 / 1789773.0;
    public static int[] length_table = {10, 254, 20, 2, 40, 4, 80, 6, 160, 8, 60, 10, 14, 12, 26, 14, 12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
    private static double volume = 1;

    private PulseChannel pulse_1;
    private PulseChannel pulse_2;
    private TriangleChannel triangle;
    private NoiseChannel noise;
    private DMCChannel dmc;

    private int clock_counter = 0;
    private double total_time = 0.0;
    private int frame_counter = 0;
    private int cycle_remaining_since_4017_write = -1;

    private boolean frame_IRQ = false;
    private boolean flag_IRQ_inhibit = false;
    private boolean flag_5_step_mode = false;

    private boolean raw_audio = false;
    private boolean pulse_1_rendered = true;
    private boolean pulse_2_rendered = true;
    private boolean noise_rendered = true;
    private boolean triangle_rendered = true;
    private boolean dmc_rendered = true;

    private Queue<Double> pulse_1_visualizer_queue;
    private Queue<Double> pulse_2_visualizer_queue;
    private Queue<Double> noise_visualizer_queue;
    private Queue<Double> triangle_visualizer_queue;
    private Queue<Double> dmc_visualizer_queue;
    private Queue<Double> mixer_visualizer_queue;

    private int cycles_until_visualizer_sample = 0;

    /**
     * Create a new instance of an APU
     */
    public APU_2A03(NES nes) {
        pulse_1 = new PulseChannel();
        pulse_2 = new PulseChannel();
        triangle = new TriangleChannel();
        noise = new NoiseChannel();
        dmc = new DMCChannel(nes);
        pulse_1_visualizer_queue = new ConcurrentLinkedQueue<>();
        pulse_2_visualizer_queue = new ConcurrentLinkedQueue<>();
        triangle_visualizer_queue = new ConcurrentLinkedQueue<>();
        noise_visualizer_queue = new ConcurrentLinkedQueue<>();
        dmc_visualizer_queue = new ConcurrentLinkedQueue<>();
        mixer_visualizer_queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Return the current volume
     *
     * @return the current APU volume
     */
    public static double getVolume() {
        return volume;
    }

    /**
     * Set the master volume of the APU
     *
     * @param volume the volume to set between 0 and 1
     */
    public static synchronized void setVolume(double volume) {
        APU_2A03.volume = volume;
    }

    /**
     * Return the current audio sample as a value between -1 and 1
     *
     * @return the current audio sample as a value between -1 and 1
     */
    public double getSample() {
        double sample = ((0.00752 * (((pulse_1_rendered ? pulse_1.sample : 0) * 15) + ((pulse_2_rendered ? pulse_2.sample : 0) * 15))) + (0.00851 * (triangle_rendered ? triangle.sample : 0) * 15) + (0.00494 * (noise_rendered ? noise.sample : 0) * 15) + 0.00335 * (dmc_rendered ? dmc.output : 0) * 128);
        if (cycles_until_visualizer_sample == 0) {
            if (pulse_1_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                pulse_1_visualizer_queue.poll();
            pulse_1_visualizer_queue.offer((pulse_1_rendered ? pulse_1.sample : 0));

            if (pulse_2_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                pulse_2_visualizer_queue.poll();
            pulse_2_visualizer_queue.offer((pulse_2_rendered ? pulse_2.sample : 0));

            if (triangle_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                triangle_visualizer_queue.poll();
            triangle_visualizer_queue.offer((triangle_rendered ? triangle.sample : 0));

            if (noise_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                noise_visualizer_queue.poll();
            noise_visualizer_queue.offer((noise_rendered ? noise.sample : 0));

            if (dmc_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                dmc_visualizer_queue.poll();
            dmc_visualizer_queue.offer((dmc_rendered ? dmc.output : 0));

            if (mixer_visualizer_queue.size() >= VISUALIZER_SAMPLE_SIZE)
                mixer_visualizer_queue.poll();
            mixer_visualizer_queue.offer(sample * 2.5);

            cycles_until_visualizer_sample = 1280 / VISUALIZER_SAMPLE_SIZE;
        }
        cycles_until_visualizer_sample--;
        return sample * 2 * volume;
    }

    public Queue<Double> getPulse1VisualizerQueue() {
        return pulse_1_visualizer_queue;
    }

    public Queue<Double> getPulse2VisualizerQueue() {
        return pulse_2_visualizer_queue;
    }

    public Queue<Double> getNoiseVisualizerQueue() {
        return noise_visualizer_queue;
    }

    public Queue<Double> getTriangleVisualizerQueue() {
        return triangle_visualizer_queue;
    }

    public Queue<Double> getDmcVisualizerQueue() {
        return dmc_visualizer_queue;
    }

    public Queue<Double> getMixerVisualizerQueue() {
        return mixer_visualizer_queue;
    }

    /**
     * Enable or Disable the first pulse channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void setPulse1Rendered(boolean enabled) {
        this.pulse_1_rendered = enabled;
    }

    /**
     * Enable or Disable the second pulse channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void setPulse2Rendered(boolean enabled) {
        this.pulse_2_rendered = enabled;
    }

    /**
     * Enable or Disable the noise channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void setNoiseRendered(boolean enabled) {
        this.noise_rendered = enabled;
    }

    /**
     * Enable or Disable the triangle channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void setTriangleRendered(boolean enabled) {
        this.triangle_rendered = enabled;
    }

    /**
     * Enable or Disable the DMC channel rendering
     * (only inhibit the output of the channel to the Mixer)
     *
     * @param enabled should the channel be rendered
     */
    public void setDMCRendered(boolean enabled) {
        this.dmc_rendered = enabled;
    }

    /**
     * Trigger a Reset of the APU
     */
    public void reset() {
        cpuWrite(0x4015, 0x00);
    }

    /**
     * Set the APU to its startup state
     */
    public void startup() {
       for (int i = 0x4000; i < 0x4007; i++)
           cpuWrite(i, 0x00);
        for (int i = 0x4010; i < 0x4013; i++)
            cpuWrite(i, 0x00);
        noise.setSequence(0xDBDB);
        frame_counter = 15;
        cpuWrite(0x4015, 0x00);
    }

    /**
     * Called when the CPU try to write to the APU
     *
     * @param addr the address to write to
     */
    public void cpuWrite(int addr, int data) {
        addr &= 0xFFFF;
        data &= 0xFF;
        switch (addr) {
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
                pulse_1.writeLengthCounter(data);
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
                pulse_2.writeLengthCounter(data);
                break;
            case 0x4008:
                triangle.writeLinearCounter(data);
                break;
            case 0x400A:
                triangle.writeTimerLow(data);
                break;
            case 0x400B:
                triangle.writeTimerHigh(data);
                triangle.loadLengthCounter(data);
                break;
            case 0x400C:
                noise.writeEnvelope(data);
                break;
            case 0x400E:
                noise.updateReload(data);
                break;
            case 0x400F:
                noise.writeLengthCounter(data);
                break;
            case 0x4010:
                dmc.writeRate(data);
                break;
            case 0x4011:
                dmc.directLoad(data);
                break;
            case 0x4012:
                dmc.writeSampleAddr(data);
                break;
            case 0x4013:
                dmc.writeSampleLength(data);
                break;
            case 0x4015:
                pulse_1.enable(false);
                pulse_2.enable(false);
                triangle.enable(false);
                noise.enable(false);
                dmc.clearIrq();

                if ((data & 0x10) == 0x00)
                    dmc.clearReader();

                if ((data & 0x1) == 0x1)
                    pulse_1.enable(true);
                else
                    pulse_1.resetLengthCounter();

                if ((data & 0x2) == 0x2)
                    pulse_2.enable(true);
                else
                    pulse_2.resetLengthCounter();

                if ((data & 0x4) == 0x4)
                    triangle.enable(true);
                else {
                    triangle.resetLengthCounter();
                    triangle.resetLinearCounter();
                }

                if ((data & 0x8) == 0x8)
                    noise.enable(true);
                else
                    noise.resetLengthCounter();

                break;
            case 0x4017:
                flag_5_step_mode = (data & 0x80) == 0x80;
                flag_IRQ_inhibit = (data & 0x40) == 0x40;
                if (flag_IRQ_inhibit) frame_IRQ = false;
                cycle_remaining_since_4017_write = 4;
                //If bit 7 is set quarter and half frame signals ar triggered
                if (flag_5_step_mode) {
                    pulse_1.clockLengthCounter();
                    pulse_1.clockEnvelope();
                    pulse_1.clockSweeper();

                    pulse_2.clockLengthCounter();
                    pulse_2.clockEnvelope();
                    pulse_2.clockSweeper();

                    triangle.clockLinearCounter();
                    triangle.clockLengthCounter();

                    noise.clockEnvelope();
                    noise.clockLengthCounter();
                }
                break;
        }
    }

    /**
     * Called when the CPU try to read from the APU
     * Only address 0x4015 is relevant for the CPU
     *
     * @param addr the address to read from
     * @return the value represent the state of the counters of every implemented channels
     */
    public int cpuRead(int addr, boolean readOnly) {
        int data = 0x00;
        if (addr == 0x4015) {
            data |= (pulse_1.getLengthCounter() > 0) ? 0x01 : 0x00;
            data |= (pulse_2.getLengthCounter() > 0) ? 0x02 : 0x00;
            data |= (triangle.getLengthCounter() > 0) ? 0x04 : 0x00;
            data |= (noise.getLengthCounter() > 0) ? 0x08 : 0x00;
            data |= (dmc.hasBytesLeft()) ? 0x10 : 0x00;
            data |= (dmc.hasInterruptTriggered()) ? 0x80 : 0x00;
            data |= frame_IRQ ? 0x40 : 0x00;
        }
        if (!readOnly)
            frame_IRQ = false;
        return data;
    }

    /**
     * A system clock of the APU, sampling can be deactivated for better performance
     * but there will be no sound
     * when sampling is disabled we only update what is susceptible to be read (the length counters)
     *
     * @param enable_sampling if sampling is enabled
     */
    public void clock(boolean enable_sampling, double timePerClock) {
        boolean quarter_frame = false;
        boolean half_frame = false;

        total_time += clock_time;
        if (clock_counter % 3 == 0) {
            dmc.clock();
            if (enable_sampling)
                triangle.computeSample(timePerClock/3, raw_audio);
        }
        if (clock_counter % 6 == 0) {
            //A write to 0x4017 will cause the frame counter to be reset after 4 CPU cycles (2 APU cycles)
            if (cycle_remaining_since_4017_write == 0) {
                frame_counter = 0;
                cycle_remaining_since_4017_write = -1;
            }
            if (cycle_remaining_since_4017_write >= 0)
                cycle_remaining_since_4017_write -= 2;

            frame_counter++;
            if (flag_5_step_mode) {
                if (frame_counter == 3729)
                    quarter_frame = true;
                if (frame_counter == 7457) {
                    quarter_frame = true;
                    half_frame = true;
                }
                if (frame_counter == 11186)
                    quarter_frame = true;
                if (frame_counter == 18641) {
                    quarter_frame = true;
                    half_frame = true;
                    frame_counter = 0;
                }
            } else {
                if (frame_counter == 3729)
                    quarter_frame = true;
                if (frame_counter == 7457) {
                    quarter_frame = true;
                    half_frame = true;
                }
                if (frame_counter == 11186)
                    quarter_frame = true;
                if (frame_counter == 14916) {
                    quarter_frame = true;
                    half_frame = true;
                    frame_counter = 0;
                    if (!flag_IRQ_inhibit)
                        frame_IRQ = true;
                }
            }
            if (quarter_frame) {
                triangle.clockLinearCounter();
                if (enable_sampling) {
                    pulse_1.clockEnvelope();
                    pulse_2.clockEnvelope();
                    noise.clockEnvelope();
                }
            }
            if (half_frame) {
                pulse_1.clockLengthCounter();
                pulse_2.clockLengthCounter();
                triangle.clockLengthCounter();
                noise.clockLengthCounter();
                pulse_1.clockSweeper();
                pulse_2.clockSweeper();
            }
            if (enable_sampling) {
                pulse_1.computeSample(total_time, raw_audio);
                pulse_2.computeSample(total_time, raw_audio);
                noise.computeSample();
                dmc.computeSample();
            }
        }
        pulse_1.trackSweeper();
        pulse_2.trackSweeper();

        clock_counter++;
    }

    /**
     * Enable or Disable RAW Audio mode
     *
     * @param raw should RAW Audio be triggered or not
     */
    public void enabledRawMode(boolean raw) {
        raw_audio = raw;
    }

    /**
     * Return whether or not the APU need to trigger an IRQ
     * If an IRQ is triggered, the source can be determined by reading from 0x4015
     *
     * @return Does the APU need to trigger an IRQ
     */
    public boolean irq() {
        return frame_IRQ || dmc.hasInterruptTriggered();
    }
}

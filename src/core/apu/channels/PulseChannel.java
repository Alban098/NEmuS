package core.apu.channels;

import core.apu.*;
import core.apu.components.*;

public class PulseChannel {

    public double sample = 0;
    public double output = 0.0;

    public boolean enabled = false;
    public boolean halted = false;

    public Sequencer sequencer;
    public Oscillator oscillator;
    public Envelope envelope;
    public LengthCounter lengthCounter;
    public Sweeper sweeper;

    public PulseChannel() {
        sequencer = new Sequencer();
        oscillator = new Oscillator();
        envelope = new Envelope();
        lengthCounter = new LengthCounter();
        sweeper = new Sweeper();
    }

    public void writeDutyCycle(int data) {
        switch ((data & 0xC0) >> 6) {
            case 0x00: sequencer.sequence_2 = 0b00000001; oscillator.duty_cycle = 0.125f; break;
            case 0x01: sequencer.sequence_2 = 0b00000011; oscillator.duty_cycle = 0.250f; break;
            case 0x02: sequencer.sequence_2 = 0b00001111; oscillator.duty_cycle = 0.500f; break;
            case 0x03: sequencer.sequence_2 = 0b11111100; oscillator.duty_cycle = 0.750f; break;
        }
        sequencer.sequence = sequencer.sequence_2;
        halted = (data & 0x20) == 0x20;
        envelope.volume = (data & 0x0F);
        envelope.disabled = (data & 0x10) == 0x10;
    }

    public void writeSweep(int data) {
        sweeper.enabled = (data & 0x80) == 0x80;
        sweeper.period = (data & 0x70) >> 4;
        sweeper.down = (data & 0x08) == 0x08;
        sweeper.shift = data & 0x07;
        sweeper.reload = true;
    }
    
    public void writeTimerLow(int data) {
        sequencer.reload.value = (sequencer.reload.value & 0xFF00) | data;
    }
    
    public void writeTimerHigh(int data) {
        sequencer.reload.value = (sequencer.reload.value & 0x00FF) | ((data & 0x7) << 8);
        sequencer.timer = sequencer.reload.value;
        sequencer.sequence = sequencer.sequence_2;
    }

    public void writeLengthCounterLoad(int data) {
        lengthCounter.counter = APU_2A03.length_table[(data & 0xF8) >> 3];
        envelope.started = true;
    }
    
    public void compute(double time) {
        oscillator.frequency = 1789773.0f / (16.0f * (sequencer.reload.value + 1));
        oscillator.amplitude = (envelope.output - 1) / 16.0f;
        sample = oscillator.sample(time);

        if (lengthCounter.counter > 0 && sequencer.timer >= 8 && !sweeper.muted && envelope.output > 2)
            output += (sample - output) * 0.5;
        else
            output = 0;
        if (!enabled) output = 0;
    }
}
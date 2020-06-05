package core.apu.components;

public class Oscillator {

    private static float PI = 3.14159265f;

    public float frequency = 0;
    public float duty_cycle = 0;
    public float amplitude = 1;

    private float harmonics = 15;

    public float sample(double t)
    {
        float a = 0;
        float b = 0;
        float p = duty_cycle * 2.0f * PI;

        for (float n = 1; n < harmonics; n++) {
            float c = (float) (n * frequency * 2.0f * PI * t);
            a += -sin(c) / n;
            b += -sin(c - p * n) / n;
        }

        return (2.0f * amplitude / PI) * (a - b);
    }

    private float sin(float t) {
        float j = t * 0.15915f;
        j = j - (int)j;
        return 20.785f * j * (j - 0.5f) * (j - 1.0f);
    }
}

package core.apu.components;

/**
 * This class represents an Oscillator and is used to generate a Square Wave of a known frequency, amplitude and duty cycle
 */
public class Oscillator {

    private static float harmonics = 10;

    private static float PI = 3.14159265f;

    public float frequency = 0;
    public float duty_cycle = 0;
    public float amplitude = 1;

    /**
     * Return the number of harmonics of the Oscillator
     *
     * @return the number of harmonics of the Oscillator
     */
    public static float getHarmonics() {
        return harmonics;
    }

    /**
     * Set the number of harmonics of the Oscillator
     *
     * @param harmonics the number of harmonics to set
     */
    public synchronized static void setHarmonics(float harmonics) {
        Oscillator.harmonics = harmonics;
    }

    /**
     * Get the sample a time t
     *
     * @param t the time to sample from
     * @return the sampled value
     */
    public float sample(double t) {
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

    /**
     * A faster methods of approximating a sinus
     *
     * @param t the value to calculate the sinus of
     * @return the sinus of t
     */
    private float sin(float t) {
        float j = t * 0.15915f;
        j = j - (int) j;
        return 20.785f * j * (j - 0.5f) * (j - 1.0f);
    }
}

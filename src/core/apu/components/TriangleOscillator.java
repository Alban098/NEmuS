package core.apu.components;

/**
 * This class represents an Oscillator and is used to generate a Triangle Wave of a known frequency
 */
public class TriangleOscillator {

    private static float harmonics = 5;

    private static float PI = 3.14159265f;

    public float frequency = 0;

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
        TriangleOscillator.harmonics = harmonics;
    }

    /**
     * Get the sample a time t
     *
     * @param t the time to sample from
     * @return the sampled value
     */
    public float sample(double t) {
        float a = 0;

        for (int i = 0; i < harmonics; i++) {
            float c = (float) (frequency * 2.0f * PI * t * (2 * i + 1));
            a += ((i & 1) == 0 ? 1 : -1) * (1.0/((2 * i + 1)*(2 * i + 1))) * sin(c);
        }

        return (8.0f  / PI / PI) * a;
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

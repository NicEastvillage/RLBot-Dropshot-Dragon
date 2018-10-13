package eastvillage.dsdragon.math;

public class RLMath {

    public static double clamp(double value, double min, double max) {
        if (value > max) value = max;
        if (value < min) value = min;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value > max) value = max;
        if (value < min) value = min;
        return value;
    }

    public static double clamp01(double value) {
        return clamp(value, 0, 1);
    }

    public static double clamp01(float value) {
        return clamp(value, 0, 1);
    }

    public static double lerp(double a, double b, double t) {
        return (1 - t) * a + t * b;
    }

    public static int sign(double v) {
        return v > 0 ? 1 : -1;
    }
}

package eastvillage.dsdragon.math;

public class RLMath {

    public static double clamp(double value, double min, double max) {
        if (value > max) value = max;
        if (value < min) value = min;
        return value;
    }
}

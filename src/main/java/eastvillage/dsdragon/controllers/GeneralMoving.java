package eastvillage.dsdragon.controllers;

import eastvillage.dsdragon.math.RLMath;

public class GeneralMoving {

    public static float smoothSteer(double angle) {
        double steer = angle + 1.3 * angle * angle * angle;
        return (float)RLMath.clamp(steer, -1, 1);
    }

    public static float hardSteer(double angle) {
        if (angle > 0) return 1f;
        return -1f;
    }
}

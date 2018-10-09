package eastvillage.dsdragon.controllers;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.Car;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;

public class MovementHelpers {

    public static float smoothSteer(double angle) {
        if (Math.abs(angle) > 3.05) return 1f;
        double steer = angle + 1.3 * angle * angle * angle;
        return (float) RLMath.clamp(steer, -1, 1);
    }

    public static float hardSteer(double angle) {
        if (angle > 0) return 1f;
        return -1f;
    }

    public static double turnRadius(double vel) {
        if (vel == 0) return 0;
        return 1.0 / turnKappa(vel);
    }

    private static double turnKappa(double vel) {
        if (vel < 500) return 0.006900 - 5.84e-6 * vel;
        if (vel < 1000) return 0.005610 - 3.26e-6 * vel;
        if (vel < 1500) return 0.004300 - 1.95e-6 * vel;
        if (vel < 1750) return 0.003025 - 1.10e-6 * vel;
        if (vel < 2500) return 0.001800 - 0.40e-6 * vel;
        return 1e-6;
    }

    /** Returns true when the car cannot turn to the point without slowing down */
    public static boolean isInTurnRadiusDeadzone(Car car, Vector3 point) {
        Vector3 pointRelative = car.relativeLocation(point);
        double turnRadius = turnRadius(car.velForwards + 40); // small bias
        double turnSide = point.angleXY() > 0 ? 1.0 : -1.0;
        Vector3 turnCenterRelative = new Vector3(0, turnRadius * turnSide, 0);
        return pointRelative.distance(turnCenterRelative) < turnRadius;
    }

    public static boolean isHeadingTowards(double angle, double distance) {
        double reqAng = (Math.PI / 3) * (distance / 6000);
        return Math.abs(angle) <= reqAng;
    }

    /** Set controls to slide if the angle is big enough, otherwise, it set controls to smooth steer */
    public static ControlsOutput setNormalSteeringAndSlide(ControlsOutput constrols, double angle, double distance, boolean slide) {
        final double REQ_SLIDE_ANGLE = 1.6;
        if (slide && distance > 300 && Math.abs(angle) > REQ_SLIDE_ANGLE) {
            constrols.withSlide();
        }
        constrols.withSteer(constrols.isSliding() ? hardSteer(angle) : smoothSteer(angle));
        return constrols;
    }
}

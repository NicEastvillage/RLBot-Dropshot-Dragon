package eastvillage.dsdragon.controllers;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.Car;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;

import java.awt.*;

import static eastvillage.dsdragon.controllers.MovementHelpers.*;

public class GeneralMovement {

    public static ControlsOutput goTowardsPoint(DataPacket data, Vector3 point, boolean boost, boolean slide, double targetVel, boolean canGoFaster) {
        ControlsOutput controls = new ControlsOutput();

        // Get down from wall by choosing a point beneath the car
        if (data.self.isOnWall) {
            point = data.self.getLocation().flat().lerp(point.flat(), 0.5);
        }

        Vector3 carToBall = point.sub(data.self.getLocation());
        Vector3 pointRelative = data.self.relativeLocation(point);
        double angle = pointRelative.angleXY();
        double distance = pointRelative.magnitude();
        double velTowardsTarget = data.self.getVelocity().projectOntoSize(carToBall);

        if (isInTurnRadiusDeadzone(data.self, point)) {
            // Must turn sharp
            controls.withThrottle(0.1f);
            controls.withSteer(hardSteer(angle));
        } else if (canGoFaster) {
            // TargetVel is just a minimum, so let's go faster!
            controls.withThrottle(1f);
            setNormalSteeringAndSlide(controls, angle, distance, slide);

            // Boost if targetVel is not reached yet; don't if we're going fast enough
            if (boost && velTowardsTarget < targetVel) {
                if (!controls.isSliding() && data.self.getVelocity().magnitude() < targetVel) {
                    if (isHeadingTowards(angle, distance)) {
                        controls.withBoost();
                    }
                }
            }
        } else {
            setNormalSteeringAndSlide(controls, angle, distance, slide);

            // Overshoot targetVel for quicker adjustment
            targetVel = RLMath.lerp(velTowardsTarget, targetVel, 1.2);

            // Find appropriate throttle/boost
            if (velTowardsTarget < targetVel) {
                controls.withThrottle(1f);
                if (boost && targetVel > Car.MAX_VEL_THROTTLE) {
                    if (!controls.isSliding() && data.self.getVelocity().magnitude() < targetVel) {
                        if (isHeadingTowards(angle, distance)) {
                            controls.withBoost();
                        }
                    }
                }
            } else if (velTowardsTarget - targetVel < 30) {
                controls.withThrottle(0.6f);
            } else if (velTowardsTarget - targetVel > 80) {
                controls.withThrottle(-0.6f);
            } else if (velTowardsTarget - targetVel > 120) {
                controls.withThrottle(-1f);
            }
        }

        if (data.self.isOnWall) controls.withSlide(false);

        return controls;
    }
}

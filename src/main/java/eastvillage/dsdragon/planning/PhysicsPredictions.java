package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.math.Vector3;

public class PhysicsPredictions {

    public static final Vector3 GRAVITY = Vector3.UNIT_Z.scale(-650);


    public static UncertainEvent timeOfArrivalAtHeight(RLObject object, double height, boolean affectedByGravity) {
        // If already at height, return 0
        if (height == object.location.z) return new UncertainEvent(true, 0);
        if (!affectedByGravity) {
            return timeOfArrivalAtHeightLinear(object, height);
        } else {
            return timeOfArrivalAtHeightQuadratic(object, height, GRAVITY.z);
        }
    }

    private static UncertainEvent timeOfArrivalAtHeightQuadratic(RLObject object, double height, double acc) {
        double loc = object.location.z;
        double vel = object.velocity.z;

        // Check if height is above current z, because then the body may never get there
        if (height > loc) {
            // Elapsed time when arriving at the turning point
            double turnTime = -vel / acc;
            double turnPointHeight = 0.5 * acc * turnTime * turnTime + vel * turnTime + loc;

            // Return false if height is never reached, or was in the past
            if (turnPointHeight < height || turnTime < 0)
                return new UncertainEvent(false, UncertainEvent.NEVER);

            // The height is reached on the way up
            if (loc < height) {
                // t = (-v + sqrt(2*a*h - 2*a*p + v^2)) / a
                double time = (-vel + Math.sqrt(2 * acc * height - 2 * acc * loc + vel * vel)) / acc;
                return new UncertainEvent(true, time);
            }
        }

        // t = -(v + sqrt(2*a*h - 2*a*p + v^2)) / a
        double time = -(vel + Math.sqrt(2 * acc * height - 2 * acc * loc + vel * vel)) / acc;
        return new UncertainEvent(true, time);
    }

    private static UncertainEvent timeOfArrivalAtHeightLinear(RLObject object, double height) {
        if (object.velocity.z == 0 && object.location.z != height)
            return new UncertainEvent(false, UncertainEvent.NEVER);

        double time = (height - object.location.z) / object.velocity.z;
        return new UncertainEvent(time >= 0, time);
    }

    /** Exclusively for ball objects. Not guaranteed to return an event that happens - the ball could be laying still. */
    public static WallHitEvent timeOfArrivalAnyWall(RLObject ball) {
        Wall[] walls = DropshotWalls.ALL;
        int wallIndex = -1;
        double earliestTime = 1000d;
        for (int i = 0; i < walls.length; i++) {
            Wall w = walls[i];
            UncertainEvent hit = w.nextBallHit(ball);
            if (hit.doesHappen() && hit.getTime() <= earliestTime) {
                wallIndex = i;
                earliestTime = hit.getTime();
            }
        }
        if (wallIndex == -1) return new WallHitEvent(false, UncertainEvent.NEVER, Vector3.UNIT_Z);
        return new WallHitEvent(true, earliestTime, walls[wallIndex].getNormal());
    }
}

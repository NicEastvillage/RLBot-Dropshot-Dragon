package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.game.Ball;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.game.TinyRLObject;
import eastvillage.dsdragon.math.Vector3;

public class PhysicsPredictions {

    public static final Vector3 GRAVITY = Vector3.UNIT_Z.scale(-650);


    /** Move the object to the position it will be in some time. The object will be affected by gravity.
     * This method mutates the object. */
    public static <T extends TinyRLObject> T moveFallingObject(T object, double time) {
        object.setLocation(GRAVITY.scale(0.5 * time * time).add(object.getVelocity().scale(time)).add(object.getLocation()));
        object.setVelocity(GRAVITY.scale(time).add(object.getVelocity()));
        return object;
    }

    /** Move the object to the position it will be in some time with it. The object will NOT be affected by gravity
     * nor change it's velocity. However, this method mutates the object's location. */
    public static <T extends TinyRLObject> T moveObjectStraight(T object, double time) {
        object.setLocation(object.getVelocity().scale(time).add(object.getLocation()));
        return object;
    }

    /** Moves the object to the given height. If the height is never reached, the object will not move.
     * This method mutates the object. */
    public static <T extends TinyRLObject> T moveFallingObjectToHeight(T object, double height, double radius) {
        UncertainEvent heightReached = arrivalAtHeight(object, height, true);
        if (heightReached.doesHappen()) {
            moveFallingObject(object, heightReached.getTime());
        }
        return object;
    }

    public static UncertainEvent arrivalAtHeight(TinyRLObject object, double height, boolean affectedByGravity) {
        if (!affectedByGravity) {
            return arrivalAtHeightLinear(object, height);
        } else {
            return arrivalAtHeightQuadratic(object, height, GRAVITY.z);
        }
    }

    private static UncertainEvent arrivalAtHeightQuadratic(TinyRLObject object, double height, double acc) {
        double loc = object.getLocation().z;
        double vel = object.getVelocity().z;

        // Check if height is above current z, because then the body may never get there
        if (height >= loc) {
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

    private static UncertainEvent arrivalAtHeightLinear(TinyRLObject object, double height) {
        if (height == object.getLocation().z) return new UncertainEvent(true, 0);
        if (object.getVelocity().z == 0 && object.getLocation().z != height)
            return new UncertainEvent(false, UncertainEvent.NEVER);

        double time = (height - object.getLocation().z) / object.getVelocity().z;
        return new UncertainEvent(time >= 0, time);
    }

    /** Exclusively for ball objects. Not guaranteed to return an event that happens - the ball could be laying still. */
    public static WallHitEvent arrivalAtAnyWall(RLObject ball) {
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

    /** Change the balls velocity and angularVelocity as if just hit a wall with the given surface normal.
     * This method mutates the ball. */
    public static <T extends RLObject> T bounceBall(T ball, Vector3 normal) {
        // See https://samuelpmish.github.io/notes/RocketLeague/ball_bouncing/
        final double MU = 0.285;
        final double A = 0.0003;

        Vector3 v_perp = normal.scale(ball.getVelocity().dot(normal));
        Vector3 v_para = ball.getVelocity().sub(v_perp);
        Vector3 v_spin = normal.cross(ball.getAngularVelocity()).scale(Ball.RADIUS);
        Vector3 s = v_para.add(v_spin);

        Vector3 delta_v_para = Vector3.ZERO;
        if (s.magnitude() != 0) {
            double ratio = v_perp.magnitude() / s.magnitude();
            delta_v_para = s.scale(-MU * Math.min(1d, 2d * ratio));
        }

        Vector3 delta_v_perp = v_perp.scale(-1.6);

        ball.setVelocity(ball.getVelocity().add(delta_v_perp));
        ball.setAngularVelocity(ball.getAngularVelocity().add(delta_v_para.cross(normal).scale(A * Ball.RADIUS)));
        return ball;
    }

    /** Move the ball the given amount of seconds into the future. This accounts for all flat walls, floor, or ceiling,
     * but will move incorrectly if ball hits any rounded areas (or edges in open tiles).
     * This method mutates the ball. */
    public static RLObject moveBall(RLObject ball, double time) {
        if (time <= 0) return ball;

        int limit = 30;
        double timeSpent = 0;

        while (time - timeSpent > 0.001d && limit >= 0) {
            limit--;
            double timeLeft = time - timeSpent;

            WallHitEvent wallHit = arrivalAtAnyWall(ball);
            UncertainEvent groundHit = arrivalAtHeight(ball, Ball.RADIUS, true);

            // Check if ball hit anything
            if (groundHit.happensAfter(timeLeft) && wallHit.happensAfter(timeLeft)) {
                return moveFallingObject(ball, timeLeft);
            }
            else if (wallHit.happensBefore(groundHit)) {
                // Move ball until it hits wall
                timeSpent += wallHit.getTime();
                moveFallingObject(ball, wallHit.getTime());
                bounceBall(ball, wallHit.getNormal());
            }
            else if (groundHit.getTime() == 0 && Math.abs(ball.getVelocity().z) < 1.0) {
                // Ball is rolling. Move it until it hits wall or out of time
                ball.setVelocity(ball.getVelocity().withZ(0));

                if (!wallHit.doesHappen()) {
                    // Ball is laying still
                    return ball;
                }
                if (timeLeft < wallHit.getTime()) {
                    // Out of time happens first
                    return moveObjectStraight(ball, time);
                }

                // Just roll
                timeSpent += wallHit.getTime();
                moveObjectStraight(ball, wallHit.getTime());
                bounceBall(ball, wallHit.getNormal());
            }
            else {
                // Move ball to ground hit
                timeSpent += groundHit.getTime();
                moveFallingObject(ball, groundHit.getTime());
                bounceBall(ball, Vector3.UNIT_Z);
            }
        }

        return ball;
    }
}

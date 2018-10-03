package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.game.Arena;
import eastvillage.dsdragon.game.Ball;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.game.TinyRLObject;
import eastvillage.dsdragon.math.Vector3;

import javax.swing.*;

public class PhysicsPredictions {

    public enum QuadDirection { ANY, DOWN, UP }
    public static final Vector3 GRAVITY = Vector3.UNIT_Z.scale(-650);
    public static double DRAG = 0.015; // for some reason not 0.0305 as expected
    public static Vector3 VEL_AT_INF = GRAVITY.scale((1 - DRAG) / DRAG);


    /** Move the object to the position it will be in some time. The object will be affected by gravity and drag.
     * This method mutates the object. */
    public static <T extends TinyRLObject> T moveFallingObject(T object, double time) {
        // U * t + (v0 + U) * (1 - d)^(t) * t + z0
        object.setLocation(VEL_AT_INF.scale(time).add(object.getVelocity().sub(VEL_AT_INF).scale(Math.pow(1 - DRAG, time) * time)).add(object.getLocation()));
        // U + (v0 - U) * (1 - d)^(t)
        object.setVelocity(VEL_AT_INF.add(object.getVelocity().sub(VEL_AT_INF).scale(Math.pow(1 - DRAG, time))));
        return object;
    }

    /** Move the object to the position it will be in some time with it. The object will NOT be affected by gravity
     * nor drag, nor change it's velocity. However, this method mutates the object's location. */
    public static <T extends TinyRLObject> T moveObjectStraight(T object, double time) {
        object.setLocation(object.getVelocity().scale(time).add(object.getLocation()));
        return object;
    }

    /** Moves the object to the given height. If the height is never reached, the object will not move.
     * This method mutates the object. */
    public static <T extends TinyRLObject> T moveFallingObjectToHeight(T object, double height, double radius) {
        UncertainEvent heightReached = arrivalAtHeight(object, height);
        if (heightReached.doesHappen()) {
            moveFallingObject(object, heightReached.getTime());
        }
        return object;
    }

    /** Returns an event describing when a RLObject reaches a certain height. */
    public static UncertainEvent arrivalAtHeight(TinyRLObject object, double height) {
        return arrivalAtHeight(object, height, QuadDirection.ANY);
    }

    /** Returns an event describing when a RLObject reaches a certain height. Use QuadDirection to specify which
     * a certain direction the object should be falling. */
    public static UncertainEvent arrivalAtHeight(TinyRLObject object, double height, QuadDirection direction) {
        double loc = object.getLocation().z;
        double vel = object.getVelocity().z;
        double acc = GRAVITY.z;
        double U = VEL_AT_INF.z;

        boolean insignificantDistance = Math.abs(height - loc) < 4;
        if (insignificantDistance && Math.abs(vel) < 8) return new UncertainEvent(true, 0);

        // Check if height is above current z, because then the body may never get there
        if (height > loc && !insignificantDistance && direction != QuadDirection.DOWN) {
            // Elapsed time when arriving at the turning point (if we pretend the ball moves in a perfect parable)
            double turnTime = -vel / acc;
            double turnPointHeight = moveFallingObject(object.clone(), turnTime).getLocation().z;

            // Return false if height is never reached, or was in the past
            if (turnPointHeight < height || turnTime < 0)
                return new UncertainEvent(false, UncertainEvent.NEVER);

            // The height is reached on the way up
            if (loc < height) {
                // t = -(-v0 + sqrt(-4dh^2 + 8dhz0 - 4dz^2 + 2gh - 2gz + v^2)) / (2dh - 2dz - g)
                double time = -(-vel + Math.sqrt(-4*DRAG*height*height + 8*DRAG*height*loc - 4*DRAG*loc*loc + 2*acc*height - 2*acc*loc + vel*vel)) / (2*DRAG*height - 2*DRAG*loc - acc);
                return new UncertainEvent(!Double.isNaN(time) && time >= 0, time);
            }
        }

        if (direction != QuadDirection.UP) {

            // t = (v0 + sqrt(-4dh^2 + 8dhz0 - 4dz^2 + 2gh - 2gz + v^2)) / (2dh - 2dz - g)
            double time = (vel + Math.sqrt(-4*DRAG*height*height + 8*DRAG*height*loc - 4*DRAG*loc*loc + 2*acc*height - 2*acc*loc + vel*vel)) / (2*DRAG*height - 2*DRAG*loc - acc);
            return new UncertainEvent(true, time);
        } else {
            return new UncertainEvent(false, UncertainEvent.NEVER);
        }
    }

    /** Returns an event describing when a RLObject reaches a certain height, assuming it is unaffected by gravity and drag. */
    public static UncertainEvent arrivalAtHeightLinear(TinyRLObject object, double height) {
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
        double earliestTime = 10000d; // crazy high number
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

    public static double MU = 0.285;
    public static double A = 0.0003;
    public static double Y = 2.0;

    /** Change the balls velocity and angularVelocity as if just hit a wall with the given surface normal.
     * This method mutates the ball. */
    public static <T extends RLObject> T bounceBall(T ball, Vector3 normal) {
        // See https://samuelpmish.github.io/notes/RocketLeague/ball_bouncing/
        /*final double MU = 0.285; // was 0.285 in chip's code
        final double A = 0.0003; // was 0.0003 in chip's code
        final double Y = 2.0; // was 2.0 in chip's code*/

        Vector3 v_perp = normal.scale(ball.getVelocity().dot(normal));
        Vector3 v_para = ball.getVelocity().sub(v_perp);
        Vector3 v_spin = normal.cross(ball.getAngularVelocity()).scale(Ball.RADIUS);
        Vector3 s = v_para.add(v_spin);

        Vector3 delta_v_para = Vector3.ZERO;
        if (s.magnitude() != 0) {
            double ratio = v_perp.magnitude() / s.magnitude();
            delta_v_para = s.scale(-MU * Math.min(1d, Y * ratio));
        }

        Vector3 delta_v_perp = v_perp.scale(-1.6);

        ball.setVelocity(ball.getVelocity().add(delta_v_perp).add(delta_v_para));
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
            UncertainEvent groundHit = arrivalAtHeight(ball, Ball.RADIUS + Arena.TILE_ELEVATION, QuadDirection.DOWN);

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
            else if (groundHit.getTime() < 0.1 && Math.abs(ball.getVelocity().z) < 10.0) {
                // Ball is rolling. Move it until it hits wall or out of time
                ball.setVelocity(ball.getVelocity().withZ(0));

                if (!wallHit.doesHappen()) {
                    // Ball is laying still
                    return ball;
                }
                if (timeLeft < wallHit.getTime()) {
                    // Out of time happens first, just roll
                    return moveObjectStraight(ball, time);
                }

                // Roll to wall
                timeSpent += wallHit.getTime();
                moveObjectStraight(ball, wallHit.getTime());
                bounceBall(ball, wallHit.getNormal());
            }
            else {
                // Move ball to ground hit or to time out if ball is below floor level
                double t = Double.isNaN(groundHit.getTime()) ? timeLeft : Math.min(groundHit.getTime(), timeLeft);
                timeSpent += t;
                moveFallingObject(ball, t);
                bounceBall(ball, Vector3.UNIT_Z);
            }
        }

        return ball;
    }
}

package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.game.Arena;
import eastvillage.dsdragon.game.Ball;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.math.Vector3;

public class DropshotWalls {

    public static Wall ORANGE_BACK_WALL = new Wall() {

        public final Vector3 normal = new Vector3(0, -1, 0);

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            if (ball.getVelocity().y == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            double dist = Arena.TO_WALL - ball.getLocation().y - Ball.RADIUS;
            double time = dist / ball.getVelocity().y;
            return new UncertainEvent(time > 0, time);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    };

    public static Wall BLUE_BACK_WALL = new Wall() {

        public final Vector3 normal = new Vector3(0, 1, 0);

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            if (ball.getVelocity().y == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            double dist = Arena.TO_WALL + ball.getLocation().y - Ball.RADIUS;
            double time = -dist / ball.getVelocity().y;
            return new UncertainEvent(time > 0, time);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    };

    public static Wall NORTH_WEST_WALL = new NAAWall(new Vector3(-Arena.TO_CORNER, 0, 0), new Vector3(Arena.TO_WALL, -0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall NORTH_EAST_WALL = new NAAWall(new Vector3(Arena.TO_CORNER, 0, 0), new Vector3(-Arena.TO_WALL, -0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall SOUTH_EAST_WALL = new NAAWall(new Vector3(Arena.TO_CORNER, 0, 0), new Vector3(-Arena.TO_WALL, 0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall SOUTH_WEST_WALL = new NAAWall(new Vector3(-Arena.TO_CORNER, 0, 0), new Vector3(Arena.TO_WALL, 0.5 * Arena.TO_ROUND_CORNER, 0));

    public static Wall CEILING = new Wall() {

        public final Vector3 normal = new Vector3(0, 0, -1);

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            return PhysicsPredictions.arrivalAtHeightQuadratic(ball, Arena.HEIGHT - Ball.RADIUS, PhysicsPredictions.GRAVITY.z, PhysicsPredictions.QuadDirection.UP);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    };

    public static Wall[] ALL = new Wall[] {
            ORANGE_BACK_WALL, BLUE_BACK_WALL,
            NORTH_WEST_WALL, NORTH_EAST_WALL,
            SOUTH_EAST_WALL, SOUTH_WEST_WALL,
            CEILING
    };


    /** Non-axis-aligned wall */
    public static class NAAWall implements Wall {

        public final Vector3 anchor, normal, offsetAnchor;

        public NAAWall(Vector3 anchor, Vector3 normal) {
            this.anchor = anchor;
            this.normal = normal.normalized();
            offsetAnchor = anchor.add(this.normal.scale(Ball.RADIUS));
        }

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            double dotvel = ball.getVelocity().dot(normal);
            if (dotvel == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            Vector3 dist = normal.scale(ball.getLocation().sub(anchor));
            double time = (dist.x + dist.y) / -dotvel;
            return new UncertainEvent(time > 0 && dotvel < 0, time);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    }
}

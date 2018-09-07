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
            if (ball.velocity.y == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            double dist = Arena.TO_WALL - ball.location.y - Ball.RADIUS;
            double time = dist / ball.velocity.y;
            return new UncertainEvent(time >= 0, time);
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
            if (ball.velocity.y == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            double dist = -Arena.TO_WALL + ball.location.y - Ball.RADIUS;
            double time = -dist / ball.velocity.y;
            return new UncertainEvent(time >= 0, time);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    };

    public static Wall ORANGE_LEFT_WALL = new NAAWall(new Vector3(Arena.TO_CORNER, 0, 0), new Vector3(-Arena.TO_WALL, -0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall ORANGE_RIGHT_WALL = new NAAWall(new Vector3(-Arena.TO_CORNER, 0, 0), new Vector3(Arena.TO_WALL, -0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall BLUE_LEFT_WALL = new NAAWall(new Vector3(-Arena.TO_CORNER, 0, 0), new Vector3(Arena.TO_WALL, 0.5 * Arena.TO_ROUND_CORNER, 0));
    public static Wall BLUE_RIGHT_WALL = new NAAWall(new Vector3(Arena.TO_CORNER, 0, 0), new Vector3(-Arena.TO_WALL, 0.5 * Arena.TO_ROUND_CORNER, 0));

    public static Wall CEILING = new Wall() {

        public final Vector3 normal = new Vector3(0, 0, -1);

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            return PhysicsPredictions.arrivalAtHeight(ball, Arena.HEIGHT - Ball.RADIUS, true);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    };

    public static Wall[] ALL = new Wall[] {
            ORANGE_BACK_WALL, BLUE_BACK_WALL,
            ORANGE_LEFT_WALL, ORANGE_RIGHT_WALL,
            BLUE_LEFT_WALL, BLUE_RIGHT_WALL,
            CEILING
    };


    /** Non-axis-aligned wall */
    public static class NAAWall implements Wall {

        public final Vector3 anchor, normal, offsetAnchor;

        public NAAWall(Vector3 anchor, Vector3 normal) {
            this.anchor = anchor;
            this.normal = normal.normalized();
            offsetAnchor = anchor.add(normal.scale(Ball.RADIUS));
        }

        @Override
        public UncertainEvent nextBallHit(RLObject ball) {
            double dotvel = ball.velocity.dot(normal);
            if (dotvel == 0) return new UncertainEvent(false, UncertainEvent.NEVER);
            Vector3 dist = normal.scale(ball.location.sub(anchor));
            double time = (dist.x + dist.y) / -dotvel;
            return new UncertainEvent(time >= 0, time);
        }

        @Override
        public Vector3 getNormal() {
            return normal;
        }
    }
}

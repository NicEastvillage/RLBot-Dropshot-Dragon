package eastvillage.dsdragon.game;

import rlbot.flat.BallInfo;

public class Ball extends RLObject {

    public static final double RADIUS = 91.21;

    public Ball(BallInfo ball) {
        super(ball.physics());
    }
}

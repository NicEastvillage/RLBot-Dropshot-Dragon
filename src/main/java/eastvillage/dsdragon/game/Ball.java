package eastvillage.dsdragon.game;

import rlbot.flat.BallInfo;

public class Ball extends RLObject {

    public static final double RADIUS = 102.0;

    public final Team lastTouchTeam;

    public Ball(BallInfo ball, Team lastTouchTeam) {
        super(ball.physics());
        this.lastTouchTeam = lastTouchTeam;
    }
}

package eastvillage.dsdragon.game;

import rlbot.flat.BallInfo;

public class Ball extends RLObject {

    public static final double RADIUS = 102.0;

    public enum Phase {
        NORMAL, CHARGED, ULTRA_CHARGED;

        public static Phase get(int dmgIndex) {
            switch (dmgIndex) {
                case 0: return NORMAL;
                case 1: return CHARGED;
                default: return ULTRA_CHARGED;
            }
        }
    }

    public final Team lastTouchTeam;
    public final Phase phase;

    public Ball(BallInfo ball, Team lastTouchTeam) {
        super(ball.physics());
        this.lastTouchTeam = lastTouchTeam;
        this.phase = Phase.get(0);
    }
}

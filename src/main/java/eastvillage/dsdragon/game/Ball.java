package eastvillage.dsdragon.game;

import rlbot.flat.BallInfo;

public class Ball extends RLObject {

    public static final double RADIUS = 102.0;
    public static final double MAX_FORCE_ACCUM_RECENT = 2500;

    public enum Phase {
        NORMAL(0), CHARGED(2500), ULTRA_CHARGED(11_000);

        public final float requiredCharge;

        Phase(float requireCharge) {
            this.requiredCharge = requireCharge;
        }

        public static Phase getFromIndex(int dmgIndex) {
            switch (dmgIndex) {
                case 0: return NORMAL;
                case 1: return CHARGED;
                default: return ULTRA_CHARGED;
            }
        }

        public static Phase getFromForce(double force) {
            if (force > ULTRA_CHARGED.requiredCharge) return ULTRA_CHARGED;
            if (force > CHARGED.requiredCharge) return CHARGED;
            return NORMAL;
        }
    }

    public final Team lastTouchTeam;
    public final Phase phase;
    public final float forceAbsorbed;
    public final float forceAccumRecent;

    public Ball(BallInfo ball, Team lastTouchTeam) {
        super(ball.physics());
        this.lastTouchTeam = lastTouchTeam;
        this.phase = Phase.getFromIndex(ball.dropShotInfo().damageIndex());
        this.forceAbsorbed = ball.dropShotInfo().absorbedForce();
        this.forceAccumRecent = ball.dropShotInfo().forceAccumRecent();
    }

    public Phase getPhaseIfHit(double impulse) {
        float newForce = (float) (forceAbsorbed + Math.min(impulse, MAX_FORCE_ACCUM_RECENT - forceAccumRecent));
        return Phase.getFromForce(newForce);
    }
}

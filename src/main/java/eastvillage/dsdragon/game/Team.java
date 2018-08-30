package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;

public enum Team {
    BLUE(-1), ORANGE(1);

    public static Team get(int index) {
        return index == 0 ? BLUE : ORANGE;
    }

    public final int sign;

    Team(int sign) {
        this.sign = sign;
    }

    public boolean isOnHalf(Vector3 location) {
        return Math.signum(location.y) == sign;
    }
}

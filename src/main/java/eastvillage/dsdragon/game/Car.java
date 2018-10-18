package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;

public class Car extends RLObject {

    public static final double MAX_VEL_BOOSTING = 2300;
    public static final double MAX_VEL_THROTTLE = 1400;

    public final String name;
    public final Team team;
    public final double boost;
    public final boolean hasWheelContact;
    public final boolean hasJumped, hasDoubleJumped;
    public final boolean isSupersonic;
    public final boolean isOnWall;
    public final double velForwards;

    public Car(rlbot.flat.PlayerInfo playerInfo) {
        super(playerInfo.physics());
        this.name = playerInfo.name();
        this.team = Team.get(playerInfo.team());
        this.boost = playerInfo.boost();
        this.hasJumped = playerInfo.jumped();
        this.hasDoubleJumped = playerInfo.doubleJumped();
        this.hasWheelContact = playerInfo.hasWheelContact();
        this.isSupersonic = playerInfo.isSupersonic();

        isOnWall = hasWheelContact && orientation.up.angle(Vector3.UNIT_Z) > 0.3;
        velForwards = velocity.projectOntoSize(orientation.front);
    }
}

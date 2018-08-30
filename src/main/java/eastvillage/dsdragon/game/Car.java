package eastvillage.dsdragon.game;

public class Car extends RLObject {

    public final Team team;
    public final double boost;
    public final boolean hasWheelContact;
    public final boolean hasJumped, hasDoubleJumped;
    public final boolean isSupersonic;

    public Car(rlbot.flat.PlayerInfo playerInfo) {
        super(playerInfo.physics());
        this.team = Team.get(playerInfo.team());
        this.boost = playerInfo.boost();
        this.hasJumped = playerInfo.jumped();
        this.hasDoubleJumped = playerInfo.doubleJumped();
        this.hasWheelContact = playerInfo.hasWheelContact();
        this.isSupersonic = playerInfo.isSupersonic();
    }
}

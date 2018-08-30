package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.GoalInfo;

public class Tile {

    public final Vector3 location;
    public final Team team;

    public State state;

    public Tile(GoalInfo info) {
        this.location = Vector3.fromFlatbuffer(info.location());
        this.team = Team.get(info.teamNum());
        this.state = State.UNKNOWN;
    }

    public enum State {
        UNKNOWN, FILLED, DAMAGED, OPEN
    }
}

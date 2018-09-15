package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Hex;
import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.GoalInfo;

public class Tile {

    public final Vector3 location;
    public final Team team;
    public final Hex hex;

    public State state;

    public Tile(Hex hex, GoalInfo info) {
        this.hex = hex;
        this.location = Vector3.fromFlatbuffer(info.location()).withZ(Arena.TILE_ELEVATION);
        this.team = Team.get(info.teamNum());
        this.state = State.UNKNOWN;
    }

    public enum State {
        UNKNOWN, FILLED, DAMAGED, OPEN
    }
}

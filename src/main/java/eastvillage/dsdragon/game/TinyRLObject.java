package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.Physics;

/** An object with a location and velocity. */
public class TinyRLObject {

    public final Vector3 location, velocity;

    public TinyRLObject(Physics physics) {
        location = Vector3.fromFlatbuffer(physics.location());
        velocity = Vector3.fromFlatbuffer(physics.location());
    }

    public TinyRLObject(Vector3 location, Vector3 velocity) {
        this.location = location;
        this.velocity = velocity;
    }
}

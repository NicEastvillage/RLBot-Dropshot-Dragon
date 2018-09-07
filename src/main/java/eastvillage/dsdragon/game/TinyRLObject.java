package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.Physics;

/** An object with a location and velocity. */
public class TinyRLObject implements Cloneable {

    protected Vector3 location, velocity;

    public TinyRLObject(Physics physics) {
        location = Vector3.fromFlatbuffer(physics.location());
        velocity = Vector3.fromFlatbuffer(physics.location());
    }

    public TinyRLObject(Vector3 location, Vector3 velocity) {
        this.location = location;
        this.velocity = velocity;
    }

    public TinyRLObject clone() {
        return new TinyRLObject(location, velocity);
    }

    public Vector3 getLocation() {
        return location;
    }

    public void setLocation(Vector3 location) {
        this.location = location;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
}

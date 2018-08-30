package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.Physics;

public class RLObject extends TinyRLObject {

    public Vector3 angularVelocity;
    public Orientation orientation;

    public RLObject(Physics physics) {
        super(physics);
        angularVelocity = Vector3.fromFlatbuffer(physics.angularVelocity());
        orientation = Orientation.fromFlatbuffer(physics.rotation());
    }

    /** Returns a vector that describes how the target location is positioned relative to this object.
     * The returned vector's x os how far in front, y is how far right, and z is how far above. */
    public Vector3 relativeLocation(Vector3 target) {
        return relativeLocation(location, orientation, target);
    }

    /** Returns a vector that describes how the target location is positioned relative to the from location using the
     * given orientation. The returned vector's x os how far in front, y is how far right, and z is how far above. */
    public static Vector3 relativeLocation(Vector3 from, Orientation orientation, Vector3 target) {
        Vector3 diff = target.minus(from);
        double x = diff.dot(orientation.front);
        double y = diff.dot(orientation.right);
        double z = diff.dot(orientation.up);
        return new Vector3(x, y, z);
    }
}

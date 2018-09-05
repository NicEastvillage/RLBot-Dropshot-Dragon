package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.math.Vector3;

public class WallHitEvent extends UncertainEvent {

    private Vector3 normal;

    public WallHitEvent(boolean happens, double time, Vector3 normal) {
        super(happens, time);
        this.normal = normal;
    }

    public Vector3 getNormal() {
        return normal;
    }
}

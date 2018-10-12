package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.math.Vector3;

public class LocatedUncertainEvent extends UncertainEvent {

    private Vector3 location;

    public LocatedUncertainEvent(boolean happens, double time, Vector3 location) {
        super(happens, time);
        this.location = location;
    }

    public Vector3 getLocation() {
        return location;
    }
}

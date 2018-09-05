package eastvillage.dsdragon.planning;

public class UncertainEvent {

    public static final double NEVER = Double.MAX_VALUE;

    private boolean happens;
    private double time;

    public UncertainEvent(boolean happens, double time) {
        this.happens = happens;
        this.time = time;
    }

    public boolean doesHappen() {
        return happens;
    }

    public double getTime() {
        return time;
    }

    /** Returns true if this event happens before arg time.
     * Returns false if this event does not happen. */
    public boolean happensBefore(double time) {
        return happens && this.time < time;
    }

    /** Returns true if this event happens before the other.
     * Always returns false, if this event does not happen.
     * If other event does not happen, but this does, true is returned. */
    public boolean happensBefore(UncertainEvent other) {
        return (happens && !other.happens) || (other.happens && this.happensBefore(other.time));
    }
}

package eastvillage.dsdragon.math;

import java.util.Objects;

/** A Hex coordinate. Works a lot like a vector, but the axis are q, r, and s with the constraint q + r + s = 0.
 * The direction of the axis is defined by the HexDirection class.
 * Read more about axial coordinates here: https://www.redblobgames.com/grids/hexagons/ */
public class Hex {

    public final int q, r, s;

    public Hex() {
        this(0, 0);
    }

    public Hex(int q, int r) {
        this.q = q;
        this.r = r;
        this.s = -q - r;
    }

    /** Construct a Hex from rounding two floating point q and r coordinates. */
    public static Hex fromRounding(float fq, float fr) {
        float fs = -fq - fr;

        int rx = Math.round(fq);
        int ry = Math.round(fr);
        int rz = Math.round(fs);

        float x_diff = Math.abs(rx - fq);
        float y_diff = Math.abs(ry - fr);
        float z_diff = Math.abs(rz - fs);

        // So we reset the component with the largest change back to what the constraint rx + ry + rz = 0 requires
        if (x_diff > y_diff && x_diff > z_diff) {
            rx = -ry - rz;
        } else if (y_diff > z_diff) {
            ry = -rx - rz;
        }

        return new Hex(rx, ry);
    }

    public Hex add(Hex h) {
        return new Hex(q + h.q, r + h.r);
    }

    public Hex sub(Hex h) {
        return new Hex(q - h.q, r - h.r);
    }

    public Hex scale(int scalar) {
        return new Hex(q * scalar, r * scalar);
    }

    /** The manhattan distance to (0, 0), also known as taxi-cap distance. */
    public float lengthManhattan() {
        return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
    }

    /** The manhattan distance to another hex, also known as taxi-cap distance. */
    public float distanceManhattan(Hex h) {
        return sub(h).lengthManhattan();
    }

    public boolean isZero() {
        return q == 0 && r == 0 && s == 0;
    }

    /** Rotates the Hex by 60 degrees the specified amount of times, where > 0 is counter-clockwise and < 0 is clockwise. */
    public Hex rotate60(int dir) {
        dir = dir % 6;
        if (dir < 0) dir += 6;
        switch (dir) {
            case 1: return new Hex(-r, -s);
            case 2: return new Hex(s, q);
            case 3: return new Hex(-q, -r);
            case 4: return new Hex(r, s);
            case 5: return new Hex(-s, -q);
            default: return this;
        }
    }

    @Override
    public String toString() {
        return "Hex(" + q + ", " + r + ", " + s + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hex that = (Hex) o;
        return q == that.q &&
                r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }
}

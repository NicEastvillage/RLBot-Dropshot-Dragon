package eastvillage.dsdragon.math;

import java.util.Objects;

/** A vector with three components: x, y, z */
public class Vector3 {

    public static final Vector3 ZERO = new Vector3(0, 0, 0);
    public static final Vector3 ONE = new Vector3(1, 1, 1);
    public static final Vector3 UNIT_X = new Vector3(1, 0, 0);
    public static final Vector3 UNIT_Y = new Vector3(0, 1, 0);
    public static final Vector3 UNIT_Z = new Vector3(0, 0, 1);

    public final double x;
    public final double y;
    public final double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double x, double y) {
        this(x, y, 0);
    }

    public static Vector3 fromFlatbuffer(rlbot.flat.Vector3 vec) {
        return new Vector3(-vec.x(), vec.y(), vec.z());
    }

    public rlbot.vector.Vector3 toRlbotVector() {
        return new rlbot.vector.Vector3((float) -x, (float) y, (float) z);
    }

    public Vector3 withX(double x) {
        return new Vector3(x, y, z);
    }

    public Vector3 withY(double y) {
        return new Vector3(x, y, z);
    }

    public Vector3 withZ(double z) {
        return new Vector3(x, y, z);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 sub(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 scale(double scale) {
        return new Vector3(x * scale, y * scale, z * scale);
    }

    public Vector3 scale(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public Vector3 scaleToMagnitude(double magnitude) {
        if (isZero()) {
            throw new IllegalStateException("Cannot scale up a vector with length zero!");
        }
        double scaleRequired = magnitude / magnitude();
        return scale(scaleRequired);
    }

    public double distance(Vector3 other) {
        double xDiff = x - other.x;
        double yDiff = y - other.y;
        double zDiff = z - other.z;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3 normalized() {
        if (isZero()) {
            throw new IllegalStateException("Cannot normalize a vector with length zero!");
        }
        return this.scale(1 / magnitude());
    }

    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vector3 flat() {
        return new Vector3(x, y, 0);
    }

    /** Returns the angle between this vector in 2d and the vector (1, 0). */
    public double angleXY() {
        return Math.atan2(y, x);
    }

    public double angle(Vector3 v) {
        double mag2 = magnitudeSquared();
        double vmag2 = v.magnitudeSquared();
        double dot = dot(v);
        return Math.acos(dot / Math.sqrt(mag2 * vmag2));
    }

    public Vector3 cross(Vector3 v) {
        double tx = y * v.z - z * v.y;
        double ty = z * v.x - x * v.z;
        double tz = x * v.y - y * v.x;
        return new Vector3(tx, ty, tz);
    }

    public Vector3 lerp(Vector3 other, double t) {
        return this.scale(1 - t).add(other.scale(t));
    }

    /** Returns the component of this that is parallel with the other. */
    public Vector3 projectOnto(Vector3 other) {
        if (other.isZero()) {
            throw new IllegalArgumentException("Cannot project onto a zero vector!");
        } else {
            double thisdot = this.dot(other);
            double otherdot = other.dot(other);
            return other.scale(thisdot / otherdot);
        }
    }

    /** Returns the size of this vector component parallel with the other vector */
    public double projectOntoSize(Vector3 other) {
        if (other.isZero()) {
            throw new IllegalArgumentException("Cannot project onto a zero vector!");
        } else {
            other = other.normalized();
            return this.dot(other) / other.dot(other);
        }
    }

    @Override
    public String toString() {
        return "Vec(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Double.compare(vector3.x, x) == 0 &&
                Double.compare(vector3.y, y) == 0 &&
                Double.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

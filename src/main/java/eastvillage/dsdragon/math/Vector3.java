package eastvillage.dsdragon.math;

public class Vector3 {

    public static final Vector3 ZERO = new Vector3();
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

    public Vector3() {
        this(0, 0, 0);
    }

    public static Vector3 fromFlatbuffer(rlbot.flat.Vector3 vec) {
        return new Vector3(vec.x(), vec.y(), vec.z());
    }

    public rlbot.vector.Vector3 toRlbotVector() {
        return new rlbot.vector.Vector3((float) x, (float) y, (float) z);
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

    public Vector3 plus(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 minus(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 scaled(double scale) {
        return new Vector3(x * scale, y * scale, z * scale);
    }

    public Vector3 scaledToMagnitude(double magnitude) {
        if (isZero()) {
            throw new IllegalStateException("Cannot scale up a vector with length zero!");
        }
        double scaleRequired = magnitude / magnitude();
        return scaled(scaleRequired);
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
        return this.scaled(1 / magnitude());
    }

    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 multiplyComponents(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vector3 flat() {
        return new Vector3(x, y, 0);
    }

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
        return this.scaled(1 - t).plus(other.scaled(t));
    }

    public Vector3 projectOnto(Vector3 other) {
        if (other.isZero()) {
            throw new IllegalArgumentException("Cannot project onto a zero vector!");
        } else {
            double thisdot = this.dot(other);
            double otherdot = other.dot(other);
            return other.scaled(thisdot / otherdot);
        }
    }

    /**
     * Returns the size of this vector component parallel with the other vector
     */
    public double projectOntoSize(Vector3 other) {
        if (other.isZero()) {
            throw new IllegalArgumentException("Cannot project onto a zero vector!");
        } else {
            other = other.normalized();
            return this.dot(other) / other.dot(other);
        }
    }
}

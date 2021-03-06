package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Vector3;
import rlbot.flat.Rotator;

public class Orientation {

    public final Vector3 front;
    public final Vector3 up;
    public final Vector3 right;

    private Orientation(Vector3 front, Vector3 up) {
        this.front = front;
        this.up = up;
        this.right = front.cross(up);
    }

    private static Orientation convert(double pitch, double yaw, double roll) {

        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);

        double noseX = - cp * cy;
        double noseY = cp * sy;
        double noseZ = sp;

        double roofX = cr * cy * sp + sr * sy;
        double roofY = - cr * sy * sp + sr * cy;
        double roofZ = cp * cr;

        return new Orientation(new Vector3(noseX, noseY, noseZ), new Vector3(roofX, roofY, roofZ));
    }

    public static Orientation fromFlatbuffer(Rotator rotator) {
        return convert(rotator.pitch(), rotator.yaw(), rotator.roll());
    }
}

package eastvillage.dsdragon.planning;

import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.math.Vector3;

public interface Wall {
    UncertainEvent nextBallHit(RLObject ball);
    Vector3 getNormal();
}
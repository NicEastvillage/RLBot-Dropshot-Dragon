package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class DefensiveState implements State {

    @Override
    public String getName() {
        return "Defensive";
    }

    @Override
    public void reset() {

    }

    @Override
    public void init(DataPacket data) {

    }

    @Override
    public ControlsOutput process(DataPacket data) {
        Vector3 target = data.ball.getLocation().scaleY(-1).withZ(0);
        double distance = data.self.getLocation().distance(target);
        double velocity = distance / 5;
        return GeneralMovement.goTowardsPoint(data, target, false, true, velocity, false);
    }

    @Override
    public boolean isDone(DataPacket data) {
        Vector3 location = PhysicsPredictions.moveBall(data.ball.clone(), 1).getLocation();
        return RLMath.sign(location.y) == data.self.team.sign;
    }
}

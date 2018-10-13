package eastvillage.dsdragon.ai.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.ai.State;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class WaitAtHomeState implements State {

    @Override
    public String getName() {
        return "WaitAtHome";
    }

    @Override
    public void reset() {

    }

    @Override
    public void init(DataPacket data) {

    }

    @Override
    public double utilityScore(DataPacket data) {
        Vector3 location1sec = PhysicsPredictions.moveBall(data.ball.clone(), 1).getLocation();
        double ballY01 = 1 / (1 + Math.pow(2, data.self.team.sign * location1sec.y / 400));
        return ballY01;
    }

    @Override
    public ControlsOutput process(DataPacket data) {
        Vector3 target = data.ball.getLocation().scaleY(-1).withZ(0);
        double distance = data.self.getLocation().distance(target);
        double velocity = distance / 3;
        return GeneralMovement.goTowardsPoint(data, target, false, true, velocity, false);
    }
}

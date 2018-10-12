package eastvillage.dsdragon.ai.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.ai.State;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.LocatedUncertainEvent;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class DribbleState implements State {

    @Override
    public String getName() {
        return "Dribbling";
    }

    @Override
    public void reset() {

    }

    @Override
    public void init(DataPacket data) {

    }

    @Override
    public double utilityScore(DataPacket data) {
        Vector3 carToBall = data.ball.getLocation().sub(data.self.getLocation());

        double dist01 = 1 - data.self.getLocation().distance(data.ball.getLocation()) / 3000;
        double ang01 = 1 - (Vector3.UNIT_Z.lerp(data.self.getOrientation().front, 0.1).angle(carToBall) / (Math.PI / 2));
        double onMySide01 = RLMath.sign(data.ball.getLocation().y) == data.self.team.sign ? 1. : 0.;
        return 0.1 + 0.3 * onMySide01 + 0.3 * ang01 + 0.3 * dist01;
    }

    @Override
    public ControlsOutput process(DataPacket data) {
        final double BIAS = 15;
        LocatedUncertainEvent ballLanding = PhysicsPredictions.nextBallLanding(data.ball);
        Vector3 biasedLocation = ballLanding.getLocation().add(new Vector3(0, data.self.team.sign * BIAS, 0));
        double distance = data.self.getLocation().distance(ballLanding.getLocation());
        double velocity = distance / ballLanding.getTime();
        return GeneralMovement.goTowardsPoint(data, biasedLocation, true, true, velocity, false);
    }
}

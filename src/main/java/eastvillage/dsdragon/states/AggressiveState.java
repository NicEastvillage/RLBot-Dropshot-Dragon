package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.LocatedUncertainEvent;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class AggressiveState implements State {

    @Override
    public String getName() {
        return "Aggressive";
    }

    @Override
    public void reset() {

    }

    @Override
    public void init(DataPacket data) {

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

    @Override
    public boolean isDone(DataPacket data) {
        boolean onMySide = RLMath.sign(data.ball.getLocation().y) == data.self.team.sign;
        boolean amClose = data.self.getLocation().distance(data.ball.getLocation()) < 2000;
        return !onMySide && !amClose;
    }
}

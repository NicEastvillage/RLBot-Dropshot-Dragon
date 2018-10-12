package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.LocatedUncertainEvent;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class AggressiveState implements State {

    private boolean ballIsHigh = false;

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
        Vector3 carToBall = data.ball.getLocation().sub(data.self.getLocation());
        double velToBall = data.self.getVelocity().projectOntoSize(carToBall);
        double eta = carToBall.magnitude() / velToBall;
        RLObject movedBall = PhysicsPredictions.moveBall(data.ball.clone(), eta);
        ballIsHigh = movedBall.getLocation().z > 600;
        return GeneralMovement.goTowardsPoint(data, movedBall.getLocation(), true, true, 2200, true);
    }

    @Override
    public boolean isDone(DataPacket data) {
        boolean onEnemySide = RLMath.sign(data.ball.getLocation().y) != data.self.team.sign;
        boolean amClose = data.self.getLocation().distance(data.ball.getLocation()) < 1000;
        boolean ballIsBehind = data.self.relativeLocation(data.ball.getLocation()).x < -300;
        return onEnemySide && (ballIsHigh || ballIsBehind);
    }
}

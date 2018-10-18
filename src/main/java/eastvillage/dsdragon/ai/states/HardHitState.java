package eastvillage.dsdragon.ai.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.ai.State;
import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.Ball;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class HardHitState implements State {

    private boolean ballIsHigh = false;

    @Override
    public String getName() {
        return "HardHit";
    }

    @Override
    public void reset() {

    }

    @Override
    public void init(DataPacket data) {

    }

    @Override
    public double utilityScore(DataPacket data) {
        double onMySide01 = RLMath.sign(data.ball.getLocation().y) == data.self.team.sign ? 1. : 0.;
        double amClose01 = data.self.getLocation().distance(data.ball.getLocation()) < 1300 ? 1. : 0.;
        double ballIsInFront01 = data.self.relativeLocation(data.ball.getLocation()).x > 300 ? 1. : 0.;
        double boost01 = data.self.boost / 100;
        double ballHasDiffVel = data.self.getVelocity().distance(data.ball.getVelocity()) > 1200 ? 1. : 0.;
        return 0.2 * onMySide01 + 0.2 * amClose01 + 0.2 * ballIsInFront01 + 0.2 * boost01 + 0.2 * ballHasDiffVel;
    }

    @Override
    public ControlsOutput process(DataPacket data) {
        Vector3 carToBall = data.ball.getLocation().sub(data.self.getLocation());
        double velToBall = data.self.getVelocity().projectOntoSize(carToBall);
        double eta = (carToBall.magnitude() - Ball.RADIUS) / velToBall;
        RLObject movedBall = PhysicsPredictions.moveBall(data.ball.clone(), 0.7 * eta);
        ballIsHigh = movedBall.getLocation().z > 600;
        return GeneralMovement.goTowardsPoint(data, movedBall.getLocation(), true, true, 2200, true);
    }
}

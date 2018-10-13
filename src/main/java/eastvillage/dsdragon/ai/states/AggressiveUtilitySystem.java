package eastvillage.dsdragon.ai.states;

import eastvillage.dsdragon.ai.State;
import eastvillage.dsdragon.ai.UtilitySystem;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class AggressiveUtilitySystem extends UtilitySystem {

    public AggressiveUtilitySystem(String name, double continueBias, State... states) {
        super(name, continueBias, states);
    }

    @Override
    public double utilityScore(DataPacket data) {
        Vector3 location1sec = PhysicsPredictions.moveBall(data.ball.clone(), 1).getLocation();
        double ballY01 = 1 / (1 + Math.pow(2, -data.self.team.sign * location1sec.y / 400));

        double offsetDist01 = RLMath.clamp01(data.self.getLocation().add(data.self.getOrientation().front.scale(600)).distance(data.ball.getLocation()) / 5000);
        offsetDist01 = 1 - offsetDist01;

        return RLMath.clamp01(0.6 * ballY01 + 0.8 * offsetDist01);
    }
}

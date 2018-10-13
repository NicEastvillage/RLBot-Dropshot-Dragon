package eastvillage.dsdragon.ai.states;

import eastvillage.dsdragon.ai.State;
import eastvillage.dsdragon.ai.UtilitySystem;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;

public class DefensiveUtilitySystem extends UtilitySystem {

    public DefensiveUtilitySystem(String name, double continueBias, State... states) {
        super(name, continueBias, states);
    }

    @Override
    public double utilityScore(DataPacket data) {
        Vector3 location1sec = PhysicsPredictions.moveBall(data.ball.clone(), 1).getLocation();
        double ballY01 = 1 / (1 + Math.pow(2, data.self.team.sign * location1sec.y / 400));

        return 0.7 * ballY01;
    }
}

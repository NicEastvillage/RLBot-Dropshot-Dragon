package eastvillage.dsdragon.ai;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

import java.awt.*;

public class RootUtilitySystem extends UtilitySystem {

    public RootUtilitySystem(String name, double continueBias, State... states) {
        super(name, continueBias, states);
    }

    @Override
    public ControlsOutput process(DataPacket data) {
        if (currentState != -1) {
            data.renderer.drawString3d(states[currentState].getName(), Color.white, data.self.getLocation().toRlbotVector(), 1, 1);
        }
        return super.process(data);
    }

    @Override
    protected void onStateChange(DataPacket data, int prevState) {
        System.out.println("Dragon " + data.playerIndex + ": " + states[currentState].getName());
    }

    @Override
    public double utilityScore(DataPacket data) {
        return 0;
    }
}

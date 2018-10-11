package eastvillage.dsdragon;

import eastvillage.dsdragon.game.*;
import eastvillage.dsdragon.states.AggressiveState;
import eastvillage.dsdragon.states.DefensiveState;
import eastvillage.dsdragon.states.StateSequencer;
import eastvillage.dsdragon.util.RenderHelp;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.flat.GameTickPacket;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;

import java.awt.*;

public class DSDragonBot implements Bot {

    private final int playerIndex;
    private final Renderer renderer;
    private final StateSequencer states;

    public DSDragonBot(int playerIndex) {
        this.playerIndex = playerIndex;
        renderer = BotLoopRenderer.forBotLoop(this);
        states = new StateSequencer(new AggressiveState(), new DefensiveState());
    }

    private ControlsOutput processInput(DataPacket data) {
        if (data.playerIndex == 1) {
            // My own ball prediction
            RenderHelp.drawBallPrediction(data.renderer, data.ball, 4.0, 0.08, Color.red);
        }
        return states.process(data);
    }

    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    @Override
    public ControllerState processInput(GameTickPacket packet) {
        if (packet.playersLength() <= playerIndex || packet.ball() == null) {
            return new ControlsOutput();
        }
        DataPacket dataPacket = new DataPacket(packet, playerIndex, renderer);
        Arena.updateTiles(packet);
        return processInput(dataPacket);
    }

    public void retire() {
        System.out.println("Retiring Dragon " + playerIndex);
    }
}

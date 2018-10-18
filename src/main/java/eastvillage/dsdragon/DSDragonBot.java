package eastvillage.dsdragon;

import eastvillage.dsdragon.ai.RootUtilitySystem;
import eastvillage.dsdragon.ai.UtilitySystem;
import eastvillage.dsdragon.ai.states.*;
import eastvillage.dsdragon.game.*;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;
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
    private final UtilitySystem states;

    public DSDragonBot(int playerIndex) {
        this.playerIndex = playerIndex;
        renderer = BotLoopRenderer.forBotLoop(this);
        states = new RootUtilitySystem("Behaviour", 0.2,
                new AggressiveUtilitySystem("Aggressive", 0.2, new HardHitState(), new DribbleState()),
                new DefensiveUtilitySystem("Defensive", 0.1, new WaitAtHomeState(), new DribbleState()));
    }

    private ControlsOutput processInput(DataPacket data) {
        if (data.playerIndex == 1) {
            // My own ball prediction
            RenderHelp.drawBallPrediction(data.renderer, data.ball, 4.0, 0.08, Color.magenta);
            // Highlight tile where ball lands
            Vector3 ballLandLocation = PhysicsPredictions.nextBallLanding(data.ball).getLocation();
            Tile tile = Arena.pointToTile(ballLandLocation);
            if (tile != null) {
                RenderHelp.highlightTile(renderer, tile, Color.magenta);
            }
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

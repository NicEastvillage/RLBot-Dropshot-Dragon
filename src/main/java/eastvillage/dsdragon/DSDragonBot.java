package eastvillage.dsdragon;

import eastvillage.dsdragon.controllers.GeneralMoving;
import eastvillage.dsdragon.game.*;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.flat.GameTickPacket;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;

import java.awt.*;

public class DSDragonBot implements Bot {

    private final int playerIndex;
    private final Renderer renderer;

    public DSDragonBot(int playerIndex) {
        this.playerIndex = playerIndex;
        renderer = BotLoopRenderer.forBotLoop(this);
    }

    private ControlsOutput processInput(DataPacket input) {
        // My own ball prediction
        if (input.playerIndex == 0) {
            RLObject ball = input.ball.clone();
            rlbot.vector.Vector3 last = null;
            for (int i = 0; i < 40; i++) {
                PhysicsPredictions.moveBall(ball, 0.1f);
                rlbot.vector.Vector3 loc = ball.getLocation().toRlbotVector();
                if (last != null) {
                    renderer.drawLine3d(Color.green, last, loc);
                }
                last = loc;
            }
        }

        ControlsOutput controls = new ControlsOutput();
        Vector3 ballRelative = input.self.relativeLocation(input.ball.getLocation());
        controls.withSteer(GeneralMoving.smoothSteer(ballRelative.angleXY()));
        return controls.withThrottle(1f);
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

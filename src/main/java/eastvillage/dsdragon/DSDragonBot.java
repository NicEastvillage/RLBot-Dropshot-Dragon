package eastvillage.dsdragon;

import eastvillage.dsdragon.controllers.GeneralMoving;
import eastvillage.dsdragon.game.Arena;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.math.Vector3;
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
        // Are points within?
        if (playerIndex == 0) {
            for (float x = 0; x <= 5200; x += 400) {
                for (float y = 0; y <= 5200; y += 400) {
                    Vector3 vec = new Vector3(x, y, 0);
                    if (vec.magnitude() < 4300) continue;
                    Color color = Arena.contains(vec) ? Color.GREEN : Color.RED;
                    renderer.drawCenteredRectangle3d(color, vec.toRlbotVector(), 6, 6, true);
                }
            }
        }

        ControlsOutput controls = new ControlsOutput();
        Vector3 ballRelative = input.self.relativeLocation(input.ball.location);
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
        if (Arena.getOrderedTiles().size() != 140) {
            return new ControlsOutput();
        } else {
            return processInput(dataPacket);
        }
    }

    public void retire() {
        System.out.println("Retiring Dragon " + playerIndex);
    }
}

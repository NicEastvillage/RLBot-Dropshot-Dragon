package eastvillage.dsdragon;

import eastvillage.dsdragon.game.Arena;
import eastvillage.dsdragon.game.DataPacket;
import eastvillage.dsdragon.game.Team;
import eastvillage.dsdragon.game.Tile;
import eastvillage.dsdragon.math.RLMath;
import eastvillage.dsdragon.math.Vector3;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.cppinterop.RLBotDll;
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
        if (Arena.getOrderedTiles().size() != 0) {
            // self tile
            Color color = input.car.team == Team.BLUE ? Color.RED : Color.CYAN;
            Tile tile = Arena.pointToTile(input.car.location);
            renderer.drawLine3d(color, input.car.location.toRlbotVector(), input.car.location.withZ(0).toRlbotVector());
            if (tile != null) {
                renderer.drawCenteredRectangle3d(color, tile.location.toRlbotVector(), 10, 10, true);
                renderer.drawLine3d(color, input.car.location.withZ(0).toRlbotVector(), tile.location.toRlbotVector());
            }

            // ball tile
            if (playerIndex == 0) {
                tile = Arena.pointToTile(input.ball.location);
                renderer.drawLine3d(Color.YELLOW, input.ball.location.toRlbotVector(), input.ball.location.withZ(0).toRlbotVector());
                if (tile != null) {
                    renderer.drawCenteredRectangle3d(Color.YELLOW, tile.location.toRlbotVector(), 10, 10, true);
                    renderer.drawLine3d(Color.YELLOW, input.ball.location.withZ(0).toRlbotVector(), tile.location.toRlbotVector());
                }
            }
        }

        ControlsOutput controls = new ControlsOutput();
        Vector3 ballRelative = input.car.relativeLocation(input.ball.location);
        controls.withSteer((float) RLMath.clamp(ballRelative.angleXY(), -1, 1));
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
        DataPacket dataPacket = new DataPacket(packet, playerIndex);
        Arena.updateTiles(packet);
        if (Arena.getOrderedTiles().size() != 140) {
            return new ControlsOutput();
        } else {
            return processInput(dataPacket);
        }
    }

    public void retire() {
        System.out.println("Retiring sample bot " + playerIndex);
    }
}

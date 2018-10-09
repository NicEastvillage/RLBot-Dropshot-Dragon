package eastvillage.dsdragon;

import eastvillage.dsdragon.controllers.GeneralMovement;
import eastvillage.dsdragon.game.*;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.flat.GameTickPacket;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;

import java.awt.*;
import java.util.ArrayList;

public class DSDragonBot implements Bot {

    private final int playerIndex;
    private final Renderer renderer;

    private long lastResetTime = 0;
    private long lastTrajectoryTime = 0;
    private ArrayList<Vector3> trajectory = new ArrayList<>(6 * 10);

    public DSDragonBot(int playerIndex) {
        this.playerIndex = playerIndex;
        renderer = BotLoopRenderer.forBotLoop(this);

        lastResetTime = System.currentTimeMillis();
    }

    /** Render the trajectory for the ball using the custom ball prediction in PhysicsPredictions. */
    public void drawBallPrediction(RLObject ball, double duration, double stepsize, Color color) {
        int steps = (int)(duration/stepsize);
        ball = ball.clone();
        rlbot.vector.Vector3 last = null;
        for (int i = 0; i < steps; i++) {
            PhysicsPredictions.moveBall(ball, stepsize);
            rlbot.vector.Vector3 loc = ball.getLocation().toRlbotVector();
            if (last != null) {
                renderer.drawLine3d(color, last, loc);
            }
            last = loc;
        }
    }

    private ControlsOutput processInput(DataPacket data) {
        // My own ball prediction
        if (data.playerIndex == 1) {
            drawBallPrediction(data.ball, 4.0, 0.8, Color.red);
        }

        renderer.drawCenteredRectangle3d(data.self.isOnWall ? Color.red : Color.green, data.self.getLocation().toRlbotVector(), 10, 10, true);

        return GeneralMovement.goTowardsPoint(data, data.ball.getLocation(), true, true, 1700, true);
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

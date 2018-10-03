package eastvillage.dsdragon;

import eastvillage.dsdragon.controllers.GeneralMoving;
import eastvillage.dsdragon.game.*;
import eastvillage.dsdragon.math.Vector3;
import eastvillage.dsdragon.planning.PhysicsPredictions;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.BallPrediction;
import rlbot.flat.DesiredGameState;
import rlbot.flat.GameTickPacket;
import rlbot.gamestate.*;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;

import java.awt.*;
import java.io.IOException;
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

    public void drawDllBallPrediction(int steps, int stepsize, Color color) {
        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            rlbot.vector.Vector3 last = null;
            steps = Math.min(steps, 6 * 60 / stepsize);
            for (int i = 0; i < steps; i++) {
                rlbot.vector.Vector3 loc = Vector3.fromFlatbuffer(ballPrediction.slices(i*stepsize).physics().location()).toRlbotVector();
                if (last != null) {
                    renderer.drawLine3d(color, last, loc);
                }
                last = loc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ControlsOutput processInput(DataPacket input) {
        // My own ball prediction
        if (input.playerIndex == 1) {
            drawBallPrediction(input.ball, 4.3, 0.05, Color.red);
            //drawDllBallPrediction(120, 3, Color.white);
        }

        // Time and recording
        long now = System.currentTimeMillis();
        int index = (int)(120 * (now - lastResetTime) / 6000d);
        if (index >= trajectory.size()) trajectory.add(input.ball.getLocation());
        else trajectory.set(index, input.ball.getLocation());
        // State setting
        if (now > lastResetTime + 1000 * 6) {
            lastResetTime = now;
            if (input.enemies.get(0).getLocation().y > 0) {
                // test case
                GameState state = new GameState()
                        .withBallState(new BallState()
                                .withPhysics(new PhysicsState()
                                        .withLocation(new DesiredVector3(0f, -50f, 1000f))
                                        .withVelocity(new DesiredVector3(-200f, 500f, -550f))
                                        .withAngularVelocity(new DesiredVector3(2f, 1.5f, -1.2f))));
                RLBotDll.setGameState(state.buildPacket());
            } else {
                // place ball on top of orange
                GameState state = new GameState()
                        .withBallState(new BallState()
                                .withPhysics(new PhysicsState()
                                        .withLocation(input.self.getLocation().withZ(115).toRlbotStateVector())
                                        .withVelocity(new DesiredVector3(0f, 0f, -500f))));
                RLBotDll.setGameState(state.buildPacket());
            }
        }
        // draw trajectory
        rlbot.vector.Vector3 last = null;
        for (Vector3 vector3 : trajectory) {
            if (vector3 != null) {
                rlbot.vector.Vector3 loc = vector3.toRlbotVector();
                if (last != null) {
                    renderer.drawLine3d(Color.green, last, loc);
                }
                last = loc;
            }
        }

        ControlsOutput controls = new ControlsOutput();
        Vector3 ballRelative = input.self.relativeLocation(input.ball.getLocation());
        controls.withSteer(GeneralMoving.smoothSteer(ballRelative.angleXY()));
        if (input.enemies.get(0).getLocation().y < 0) {
            return controls.withThrottle(1f);
        }
        return new ControlsOutput();
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

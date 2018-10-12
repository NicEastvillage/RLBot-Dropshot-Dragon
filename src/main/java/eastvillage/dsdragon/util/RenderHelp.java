package eastvillage.dsdragon.util;

import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.planning.PhysicsPredictions;
import rlbot.render.Renderer;

import java.awt.*;

public class RenderHelp {

    /** Render the trajectory for the ball using the custom ball prediction in PhysicsPredictions. */
    public static void drawBallPrediction(Renderer renderer, RLObject ball, double duration, double stepsize, Color color) {
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
}

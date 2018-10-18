package eastvillage.dsdragon.util;

import eastvillage.dsdragon.game.Arena;
import eastvillage.dsdragon.game.RLObject;
import eastvillage.dsdragon.game.Tile;
import eastvillage.dsdragon.math.Vector3;
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

    public static void highlightTile(Renderer renderer, Tile tile, Color color) {
        rlbot.vector.Vector3[] vecs = new rlbot.vector.Vector3[] {
                tile.location.add(new Vector3(Arena.TILE_WIDTH / 2, Arena.TILE_SIZE / 2, 0)).toRlbotVector(),
                tile.location.add(new Vector3(0, Arena.TILE_SIZE, 0)).toRlbotVector(),
                tile.location.add(new Vector3(-Arena.TILE_WIDTH / 2, Arena.TILE_SIZE / 2, 0)).toRlbotVector(),
                tile.location.add(new Vector3(-Arena.TILE_WIDTH / 2, -Arena.TILE_SIZE / 2, 0)).toRlbotVector(),
                tile.location.add(new Vector3(0, -Arena.TILE_SIZE, 0)).toRlbotVector(),
                tile.location.add(new Vector3(Arena.TILE_WIDTH / 2, -Arena.TILE_SIZE / 2, 0)).toRlbotVector()
        };
        for (int i = 0; i < 6; i++) {
            int j = i == 0 ? 5 : i - 1;
            renderer.drawLine3d(color, vecs[i], vecs[j]);
        }
    }
}

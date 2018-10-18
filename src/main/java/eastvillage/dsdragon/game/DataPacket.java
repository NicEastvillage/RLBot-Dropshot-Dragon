package eastvillage.dsdragon.game;

import rlbot.flat.BallInfo;
import rlbot.flat.GameTickPacket;
import rlbot.flat.PlayerInfo;
import rlbot.render.Renderer;

import java.util.ArrayList;
import java.util.List;

public class DataPacket {

    public final Renderer renderer;
    public final Car self;
    public final Ball ball;
    public final int playerIndex;
    public final ArrayList<Car> allies;
    public final ArrayList<Car> enemies;
    public final ArrayList<Car> cars;

    public DataPacket(GameTickPacket packet, int playerIndex, Renderer renderer) {

        this.playerIndex = playerIndex;
        this.renderer = renderer;

        self = new Car(packet.players(playerIndex));

        cars = new ArrayList<>();
        cars.add(self);
        allies = new ArrayList<>(4);
        enemies = new ArrayList<>(5);
        int team = self.team.ordinal();
        for (int i = 0; i < packet.playersLength(); i++) {
            if (i != playerIndex) {
                PlayerInfo player = packet.players(i);
                Car car = new Car(player);
                cars.add(car);
                if (player.team() == team) {
                    allies.add(car);
                } else {
                    enemies.add(car);
                }
            }
        }

        ball = new Ball(packet.ball(), findLastTouchTeam(packet.ball()));
    }

    private Team findLastTouchTeam(BallInfo ballInfo) {
        try {
            String touchersName = ballInfo.latestTouch().playerName();
            for (Car car : cars) {
                if (car.name.equals(touchersName)) {
                    return car.team;
                }
            }
        } catch (NullPointerException e) {
            // no one has touched the ball yet
        }
        // defaults to blue
        return Team.BLUE;
    }
}

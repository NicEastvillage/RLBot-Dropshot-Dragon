package eastvillage.dsdragon.game;

import rlbot.flat.GameTickPacket;
import rlbot.flat.PlayerInfo;

import java.util.ArrayList;

public class DataPacket {

    public final Car car;
    public final Ball ball;
    public final int playerIndex;
    public final ArrayList<Car> allies;
    public final ArrayList<Car> enemies;

    public DataPacket(GameTickPacket packet, int playerIndex) {

        this.playerIndex = playerIndex;
        ball = new Ball(packet.ball());
        car = new Car(packet.players(playerIndex));
        allies = new ArrayList<>(4);
        enemies = new ArrayList<>(5);
        int team = car.team.ordinal();
        for (int i = 0; i < packet.playersLength(); i++) {
            if (i != playerIndex) {
                PlayerInfo player = packet.players(i);
                if (player.team() == team) {
                    allies.add(new Car(player));
                } else {
                    enemies.add(new Car(player));
                }
            }
        }
    }
}

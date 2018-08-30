package eastvillage.dsdragon.game;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.FieldInfo;
import rlbot.flat.GameTickPacket;

import java.io.IOException;
import java.util.ArrayList;

public class Arena {
    public static final double SIZE = 2000; // Not found yet

    private static final ArrayList<Tile> orderedTiles = new ArrayList<>();
    private static final ArrayList<Tile> blueTiles = new ArrayList<>();
    private static final ArrayList<Tile> orangeTiles = new ArrayList<>();

    private static void loadFieldInfo(FieldInfo fieldInfo) {
        synchronized (orderedTiles) {

            orderedTiles.clear();
            blueTiles.clear();
            orangeTiles.clear();

            for (int i = 0; i < fieldInfo.goalsLength(); i++) {
                Tile tile = new Tile(fieldInfo.goals(i));
                orderedTiles.add(tile);
                if (tile.team == Team.BLUE) {
                    blueTiles.add(tile);
                } else {
                    orangeTiles.add(tile);
                }
            }
        }
    }

    public static void updateTiles(GameTickPacket packet) {
        if (packet.tileInformationLength() > orderedTiles.size()) {
            try {
                loadFieldInfo(RLBotDll.getFieldInfo());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        for (int i = 0; i < packet.tileInformationLength(); i++) {
            int intState = packet.tileInformation(i).tileState();
            orderedTiles.get(i).state = Tile.State.values()[intState];
        }
    }

    public static ArrayList<Tile> getOrderedTiles() {
        return orderedTiles;
    }

    public static ArrayList<Tile> getTilesOfTeam(Team team) {
        return team == Team.BLUE ? blueTiles : orangeTiles;
    }
}

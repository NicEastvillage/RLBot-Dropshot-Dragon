package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Hex;
import eastvillage.dsdragon.math.Vector3;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.FieldInfo;
import rlbot.flat.GameTickPacket;

import java.io.IOException;
import java.util.ArrayList;

public class Arena {

    public static final double SIZE = 2000; // Not found yet
    public static final double TILE_SIZE = 382;
    public static final double TILE_WIDTH = 2 * TILE_SIZE;
    public static final double TILE_HEIGHT = Math.sqrt(3) * TILE_SIZE;

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

    public static Vector3 hexToPoint(Hex hex) {
        return HexDirection.Q_VEC.scale(hex.q).add(HexDirection.R_VEC.scale(hex.r));
    }

    public static Hex pointToHex(Vector3 point) {
        double q = (2/3d * point.x) / TILE_SIZE;
        double r = (-1/3d * point.x + Math.sqrt(3)/3 * point.y) / TILE_SIZE;
        return Hex.fromRounding((float)q, (float)r);
    }

    /** Defines the six direction of a hex.
     * We want flat-topped hexes, where q = columns and r = rows.
     * So in RL +r (North) is towards team orange and -r (South) is towards team blue. */
    public enum HexDirection {
        N(0, 1),
        NE(1, 0),
        NW(-1, 1),
        S(0, 1),
        SW(-1, 0),
        SE(1, -1);

        private static Vector3 Q_VEC = new Vector3(0.75 * TILE_WIDTH, TILE_HEIGHT / 2, 0);
        private static Vector3 R_VEC = new Vector3(0, TILE_HEIGHT);

        private Hex hex;

        HexDirection(int x, int y) {
            this.hex = new Hex(x, y);
        }

        public Hex asHex() {
            return hex;
        }
    }
}

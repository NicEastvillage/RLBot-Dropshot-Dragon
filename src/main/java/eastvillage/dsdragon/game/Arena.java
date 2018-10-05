package eastvillage.dsdragon.game;

import eastvillage.dsdragon.math.Hex;
import eastvillage.dsdragon.math.Vector3;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.FieldInfo;
import rlbot.flat.GameTickPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Arena {

    public static final double HEIGHT = 2018;
    public static final double TO_WALL = 4555;
    public static final double TO_CORNER = 2 * TO_WALL / Math.sqrt(3);
    public static final double TO_ROUND_CORNER = 5026;
    public static final double TILE_WIDTH = 768;
    public static final double TILE_SIZE = TILE_WIDTH / Math.sqrt(3);
    public static final double TILE_HEIGHT = TILE_SIZE * 2;
    public static final double TILE_ELEVATION = 2.5;
    public static final int TILE_COUNT = 140;

    private static boolean tilesInitialized = false;
    private static final Tile[] orderedTiles = HardCodedTiles.tiles;
    private static final HashMap<Hex, Tile> blueTileMap = new HashMap<>();
    private static final HashMap<Hex, Tile> orangeTileMap = new HashMap<>();

    private static void loadHardCodedTiles() {
        blueTileMap.clear();
        orangeTileMap.clear();

        for (int i = 0; i < TILE_COUNT; i++) {
            Tile tile = orderedTiles[i];
            if (i < 70) {
                blueTileMap.put(tile.hex, tile);
            } else {
                orangeTileMap.put(tile.hex, tile);
            }
        }

        tilesInitialized = true;
    }

    public static void updateTiles(GameTickPacket packet) {
        if (!tilesInitialized) {
            loadHardCodedTiles();
        }

        try {
            Tile.State[] states = Tile.State.values();
            for (int i = 0; i < TILE_COUNT; i++) {
                int intState = packet.tileInformation(i).tileState();
                orderedTiles[i].state = states[intState];
            }
        } catch (IndexOutOfBoundsException e) {
            // field info needs reload
        } catch (NullPointerException e) {
            System.out.println("NULL POINTER EXCEPTION IN ARENA.UPDATE()");
        }
    }

    private static boolean missingGoalsWorkAroundPrintDone = false;
    private static void missingGoalsWorkAroundPrint() {
        if (!missingGoalsWorkAroundPrintDone) {
            missingGoalsWorkAroundPrintDone = true;

            for (Tile tile : orderedTiles) {
                String vec = "" + tile.location.x + ", " + tile.location.y + ", " + tile.location.z;
                String hex = "" + tile.hex.q + ", " + tile.hex.r;
                int team = tile.team == Team.BLUE ? 0 : 1;
                System.out.println("new Tile(new Vector3(" + vec + "), new Hex(" + hex + "), Team.get(" + team + ")),");
            }
        }
    }

    public static Tile[] getOrderedTiles() {
        return orderedTiles;
    }

    public static Collection<Tile> getTilesOfTeam(Team team) {
        return team == Team.BLUE ? blueTileMap.values() : orangeTileMap.values();
    }

    /** Returns true if point is within the arena. */
    public static boolean contains(Vector3 point) {
        if (point.z < 0 || HEIGHT < point.z) return false;
        double absx = Math.abs(point.x);
        double absy = Math.abs(point.y);
        if (TO_CORNER < absx || TO_WALL < absy) return false;
        return -TO_WALL * absx + TO_WALL * TO_CORNER - 0.5 * TO_CORNER * absy >= 0;
    }

    /** Returns the Tile under the point, or null of none is. */
    public static Tile pointToTile(Vector3 point) {
        if (Team.BLUE.isOnHalf(point)) {
            point = point.sub(new Vector3(0, -128));
            Hex hex = pointToHex(point);
            return blueTileMap.get(hex);
        } else {
            point = point.sub(new Vector3(0, 128));
            Hex hex = pointToHex(point);
            return orangeTileMap.get(hex);
        }
    }

    /** Note: Not the same as tileToPoint. The point has to be offset by +/- 128 depending on
     * which team the tile belongs. */
    private static Vector3 hexToPoint(Hex hex) {
        return HexDirection.Q_VEC.scale(hex.q).add(HexDirection.R_VEC.scale(hex.r));
    }

    /** Note: Not the same as pointToTile. This assumes the Hex(0, 0) is at point (0, 0). */
    private static Hex pointToHex(Vector3 point) {
        double q = point.x / TILE_WIDTH - point.y * 2 / (3 * TILE_HEIGHT);
        double r = point.y * 4 / (3 * TILE_HEIGHT);
        return Hex.fromRounding((float)q, (float)r);
    }

    /** Defines the six direction of a hex.
     * We want pointy-topped hexes, where q = columns and r = rows.
     * So in RL +r (NorthEast) is towards team orange and -r (SouthWest) is towards team blue.
     * q is along the center strip. */
    public enum HexDirection {
        E(0, 1),
        NE(1, 0),
        NW(1, -1),
        W(0, -1),
        SW(-1, 0),
        SE(-1, 1);

        private static Vector3 Q_VEC = new Vector3(TILE_WIDTH, 0, 0);
        private static Vector3 R_VEC = new Vector3(TILE_WIDTH / 2, 0.75 * TILE_HEIGHT);

        private Hex hex;

        HexDirection(int x, int y) {
            this.hex = new Hex(x, y);
        }

        public Hex asHex() {
            return hex;
        }
    }
}

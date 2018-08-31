package eastvillage.dsdragon.math;

/** Defines the six direction of a hex.
 * We want flat-topped hexes, where q = columns and r = rows. */
public enum HexDirection {
    N(0, 1),
    NE(1, 0),
    NW(-1, 1),
    S(0, 1),
    SW(-1, 0),
    SE(1, -1);

    private Hex hex;

    HexDirection(int x, int y) {
        this.hex = new Hex(x, y);
    }

    public Hex asHex() {
        return hex;
    }
}

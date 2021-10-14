package application.model;

public class Tile {
    private final int x;
    private final int y;
    private final int side; // 0 is right, 1 is top, 2 is left, 3 is bottom (4 tiles form a square)
    private Player controllingPlayer;

    public Tile(int x, int y, int side) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.controllingPlayer = null;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getSide() {
        return this.side;
    }

    public Player getControllingPlayer() {
        return this.controllingPlayer;
    }

    public void setControllingPlayer(Player controllingPlayer) {
        this.controllingPlayer = controllingPlayer;
    }
}

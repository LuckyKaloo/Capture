package application.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Board {
    private final Player player1;
    private final Player player2;
    private final ArrayList<ArrayList<Vertex>> vertices = new ArrayList<>();
    private final ArrayList<ArrayList<Tile[]>> tiles = new ArrayList<>();

    public final static int BOARD_WIDTH = 15;
    public final static int BOARD_HEIGHT = 10;

    public Board() {
        this("Player 1", "Player 2");
    }

    public Board(String name1, String name2) {
        this.player1 = new Player(name1, new Vertex(0, 0));
        this.player2 = new Player(name2, new Vertex(BOARD_WIDTH, BOARD_HEIGHT));

        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            ArrayList<Vertex> rowVertices = new ArrayList<>();
            ArrayList<Tile[]> rowTiles = new ArrayList<>();

            for (int x = 0; x <= BOARD_WIDTH; x++) {
                rowVertices.add(new Vertex(x, y));
                if (y < BOARD_HEIGHT &&  x < BOARD_WIDTH) {
                    Tile[] square = new Tile[4];
                    for (int side = 0; side < 4; side++) {
                        square[side] = new Tile(x, y, side);
                    }
                    rowTiles.add(square);
                }
            }

            vertices.add(rowVertices);
            tiles.add(rowTiles);
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player changePlayer(Player player) {
        if (player == player1) {
            return player2;
        } else if (player == player2) {
            return player1;
        }

        return null;
    }

    public ArrayList<ArrayList<Vertex>> getVertices() {
        return vertices;
    }

    public ArrayList<ArrayList<Tile[]>> getTiles() {
        return tiles;
    }

    public void update() {
        if (player1.formsClosedLoop()) {
            capture(player1);
        }

        if (player2.formsClosedLoop()) {
            capture(player2);
        }
    }

    private void capture(Player player) {
        captureAxis(true, player);
        captureAxis(false, player);
        player.closeLoop();
    }

    private void captureAxis(boolean horizontal, Player player) {
        int closeSide;
        int farSide;
        int perpLength;
        int paraLength;
        if (horizontal) {
            closeSide = 2;
            farSide = 0;
            perpLength = BOARD_HEIGHT;
            paraLength = BOARD_WIDTH;
        } else {
            closeSide = 1;
            farSide = 3;
            perpLength = BOARD_WIDTH;
            paraLength = BOARD_HEIGHT;
        }

        // boolean[] is stored as {negative, vertical, positive}
        ArrayList<ArrayList<boolean[]>> axisEdges = new ArrayList<>();
        for (int perp = 0; perp < perpLength; perp++) {
            ArrayList<boolean[]> lineEdges = new ArrayList<>();
            for (int para = 0; para < paraLength; para++) {
                lineEdges.add(new boolean[]{false, false, false});
            }
            axisEdges.add(lineEdges);
        }

        for (Bot bot: player.getBots()) {
            for (int i = 0; i < bot.getVisitedVertices().size() - 1; i++) {
                Vertex start = bot.getVisitedVertices().get(i);
                Vertex end = bot.getVisitedVertices().get(i + 1);

                if (horizontal) {
                    if (start.getY() == end.getY()) {
                        continue;
                    }
                } else {
                    if (start.getX() == end.getX()) {
                        continue;
                    }
                }

                int para;
                int perp;
                int edgeType;
                if (horizontal) {
                    para = Math.min(start.getX(), end.getX());
                    perp = Math.min(start.getY(), end.getY());
                    edgeType = (start.getX() - end.getX()) / (start.getY() - end.getY()) + 1;
                } else {
                    para = Math.min(start.getY(), end.getY());
                    perp = Math.min(start.getX(), end.getX());
                    edgeType = (start.getY() - end.getY()) / (start.getX() - end.getX()) + 1;
                }

                if (para < paraLength && perp < perpLength) {
                    axisEdges.get(perp).get(para)[edgeType] = !axisEdges.get(perp).get(para)[edgeType];
                }
            }
        }

        for (int perp = 0; perp < perpLength; perp++) {
            ArrayList<boolean[]> lineEdges = axisEdges.get(perp);
            boolean inLoop = false;

            for (int para = 0; para < paraLength; para++) {
                boolean[] edgeTypes = lineEdges.get(para);
                Tile[] square;
                if (horizontal) {
                    square = tiles.get(perp).get(para);
                } else {
                    square = tiles.get(para).get(perp);
                }

                boolean closeInLoop = edgeTypes[1] != inLoop;

//                if (!horizontal && player == player2) {
//                    System.out.println(closeInLoop + " " + Arrays.toString(edgeTypes) + "  x: " + y + " y: " + x);
//                }

                if (edgeTypes[0] == edgeTypes[2]) {
                    if (closeInLoop) {
                        square[closeSide].setControllingPlayer(player);
                        square[farSide].setControllingPlayer(player);
                    }
                } else {
                    if (closeInLoop) {
                        square[closeSide].setControllingPlayer(player);
                    } else {
                        square[farSide].setControllingPlayer(player);
                    }
                }

                int sides = 0;
                for (boolean bool: edgeTypes) {
                    if (bool) {
                        sides++;
                    }
                }

                if (sides % 2 == 1) {
                    inLoop = !inLoop;
                }
            }
        }
    }
}

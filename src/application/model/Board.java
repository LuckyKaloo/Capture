package application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Board {
    private final Player player1;
    private final Player player2;
    private final ArrayList<ArrayList<Vertex>> vertices = new ArrayList<>();
    private final ArrayList<ArrayList<ArrayList<Tile>>> tiles = new ArrayList<>();

    public final static int BOARD_WIDTH = 15;
    public final static int BOARD_HEIGHT = 10;

    public Board() {
        this("Player 1", "Player 2");
    }

    public Board(String name1, String name2) {
        this.player1 = new Player(name1);
        this.player2 = new Player(name2);

        for (int row = 0; row <= BOARD_HEIGHT; row++) {
            ArrayList<Vertex> rowVertices = new ArrayList<>();
            ArrayList<ArrayList<Tile>> rowTiles = new ArrayList<>();

            for (int col = 0; col <= BOARD_WIDTH; col++) {
                rowVertices.add(new Vertex(row, col));
                if (row < BOARD_HEIGHT &&  col < BOARD_WIDTH) {
                    ArrayList<Tile> square = new ArrayList<>();
                    for (int side = 0; side < 4; side++) {
                        square.add(new Tile(row, col, side));
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

    public ArrayList<ArrayList<Vertex>> getVertices() {
        return vertices;
    }

    public ArrayList<ArrayList<ArrayList<Tile>>> getTiles() {
        return tiles;
    }

    public void update() {
        if (player1.formsClosedLoop()) {
            capture(player1);
            Vertex vertex = player1.getVisitedVertices().get(player1.getVisitedVertices().size()-1);
            player1.getVisitedVertices().clear();
            player1.getVisitedVertices().add(vertex);
        }

        if (player2.formsClosedLoop()) {
            capture(player2);
            player2.getVisitedVertices().clear();
        }
    }

    private void capture(Player player) {
        captureAxis(true, player);
        captureAxis(false, player);
    }

    private void captureAxis(boolean horizontal, Player player) {
        int closeSide;
        int farSide;
        int perp;
        int para;
        if (horizontal) {
            closeSide = 2;
            farSide = 0;
            perp = BOARD_HEIGHT;
            para = BOARD_WIDTH;
        } else {
            closeSide = 1; // waitdiscord
            farSide = 3;  // ill do it now
            perp = BOARD_WIDTH;
            para = BOARD_HEIGHT;
        }

        // boolean[] is stored as {negative, vertical, positive}
        ArrayList<ArrayList<boolean[]>> axisEdges = new ArrayList<>();
        for (int y = 0; y < perp; y++) {
            ArrayList<boolean[]> lineEdges = new ArrayList<>();
            for (int x = 0; x < para; x++) {
                lineEdges.add(new boolean[]{false, false, false});
            }
            axisEdges.add(lineEdges);
        }

        for (int i = 0; i < player.getVisitedVertices().size(); i++) {
            Vertex start = player.getVisitedVertices().get(i);
            Vertex end;
            if (i < player.getVisitedVertices().size() - 1) {
                end = player.getVisitedVertices().get(i + 1);
            } else {
                end = player.getVisitedVertices().get(0);
            }

            if (horizontal) {
                if (start.getY() == end.getY()) {
                    continue;
                }
            } else {
                if (start.getX() == end.getX()) {
                    continue;
                }
            }

            int xCoord;
            int yCoord;
            int edgeType;
            if (horizontal) {
                xCoord = Math.min(start.getX(), end.getX());
                yCoord = Math.min(start.getY(), end.getY());
                edgeType = (start.getX() - end.getX()) / (start.getY() - end.getY()) + 1;
            } else {
                xCoord = Math.min(start.getY(), end.getY());
                yCoord = Math.min(start.getX(), end.getX());
                edgeType = (start.getY() - end.getY()) / (start.getX() - end.getX()) + 1;
            }

            axisEdges.get(yCoord).get(xCoord)[edgeType] = !axisEdges.get(yCoord).get(xCoord)[edgeType];
        }

        for (int y = 0; y < perp; y++) {
            ArrayList<boolean[]> lineEdges = axisEdges.get(y);
            boolean inLoop = false;

            for (int x = 0; x < para; x++) {
                boolean[] edgeTypes = lineEdges.get(x);
                ArrayList<Tile> square;
                if (horizontal) {
                    square = tiles.get(y).get(x);
                } else {
                    square = tiles.get(x).get(y);
                }

                boolean closeInLoop = edgeTypes[1] != inLoop;

                if (edgeTypes[0] == edgeTypes[2]) {
                    if (closeInLoop) {
                        square.get(closeSide).setControllingPlayer(player);
                        square.get(farSide).setControllingPlayer(player);
                    }
                } else {
                    if (closeInLoop) {
                        square.get(closeSide).setControllingPlayer(player);
                    } else {
                        square.get(farSide).setControllingPlayer(player);
                    }
                }

                if (horizontal) {
                    System.out.println("horizontal  " + closeInLoop + "   " + Arrays.toString(edgeTypes) + "  x: " + x + " y: " + y);
                } else {
                    System.out.println("vertical  " + closeInLoop + "   " + Arrays.toString(edgeTypes) + "  x: " + y + " y: " + x);
                }

                // fixing the problem

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

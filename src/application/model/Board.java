package application.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    private final ArrayList<ArrayList<Tile[]>> tiles = new ArrayList<>();

    public final static int BOARD_WIDTH = 15;
    public final static int BOARD_HEIGHT = 10;

    public Board() {
        this("Player 1", "Player 2");
    }

    public Board(String name1, String name2) {
        this.player1 = new Player(name1, new Vertex(0, 0));
        this.player2 = new Player(name2, new Vertex(BOARD_WIDTH, BOARD_HEIGHT));
        this.currentPlayer = this.player1;

        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            ArrayList<Tile[]> rowTiles = new ArrayList<>();

            for (int x = 0; x <= BOARD_WIDTH; x++) {
                if (y < BOARD_HEIGHT &&  x < BOARD_WIDTH) {
                    Tile[] square = new Tile[4];
                    for (int side = 0; side < 4; side++) {
                        square[side] = new Tile(x, y, side);
                    }
                    rowTiles.add(square);
                }
            }

            tiles.add(rowTiles);
        }
    }

    public ArrayList<ArrayList<Tile[]>> getTiles() {
        return tiles;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
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
        player.setArea(0);
        captureAxis(true, player);
        captureAxis(false, player);
        player.closeLoop();

        // checking if game is over
        if (player1.getArea() + player2.getArea() == BOARD_HEIGHT * BOARD_WIDTH * 4) {
            endGame();
        }
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
                    if (start.Y() == end.Y()) {
                        continue;
                    }
                } else {
                    if (start.X() == end.X()) {
                        continue;
                    }
                }

                int para;
                int perp;
                int edgeType;
                if (horizontal) {
                    para = Math.min(start.X(), end.X());
                    perp = Math.min(start.Y(), end.Y());
                    edgeType = (start.X() - end.X()) / (start.Y() - end.Y()) + 1;
                } else {
                    para = Math.min(start.Y(), end.Y());
                    perp = Math.min(start.X(), end.X());
                    edgeType = (start.Y() - end.Y()) / (start.X() - end.X()) + 1;
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
//                    System.out.println(closeInLoop + " " + Arrays.toString(edgeTypes) + "  X: " + Y + " Y: " + X);
//                }

                if (edgeTypes[0] == edgeTypes[2]) {
                    if (closeInLoop) {
                        player.captureTile(square[closeSide]);
                        player.captureTile(square[farSide]);
                    }
                } else {
                    if (closeInLoop) {
                        player.captureTile(square[closeSide]);
                    } else {
                        player.captureTile(square[farSide]);
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

    public Bot getSelectedBot() {
        return this.currentPlayer.getSelectedBot();
    }

   public void changeBot() {
        this.currentPlayer.changeBot();
   }

    public void moveBot(int x, int y) {
        int newX = this.currentPlayer.getSelectedBot().getX() + x;
        int newY = this.currentPlayer.getSelectedBot().getY() + y;
        if (0 <= newX && newX <= Board.BOARD_WIDTH  &&  0 <= newY && newY <= Board.BOARD_HEIGHT) {
            if (this.currentPlayer.getSelectedBot().move(x, y)) {
                this.update();
                Move move = new Move(this);
                this.changePlayer();
            }
        }
    }

    private void changePlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else if (currentPlayer == player2) {
            currentPlayer = player1;
        }
    }

    private void endGame() {

    }

//    public String getBoard() {
//
//    }
}

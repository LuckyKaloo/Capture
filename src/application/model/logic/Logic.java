package application.model.logic;

import application.controller.Main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Logic {
    private final boolean isPlayer1;
    private final String playerName;
    private Board board;
    private Player player;

    public Logic(Board board, boolean isPlayer1) {
        this.board = board;
        this.isPlayer1 = isPlayer1;
        if (isPlayer1) {
            this.playerName = board.getPlayer1().getName();
            this.player = board.getPlayer1();
        } else {
            this.playerName = board.getPlayer2().getName();
            this.player = board.getPlayer2();
        }
    }

    public void setBoard(Board board) {
        this.board = board;

        if (isPlayer1) {
            this.player = board.getPlayer1();
        } else {
            this.player = board.getPlayer2();
        }
    }

    public void update() {
        if (player.formsClosedLoop()) {
            capture();
        }
    }

    private void capture() {
        captureAxis(true);
        captureAxis(false);
        player.closeLoop();

        // checking if game is over
        int player1Area = 0, player2Area = 0;
        for (ArrayList<int[]> rows: board.getTiles()) {
            for (int[] square: rows) {
                for (int tile: square) {
                    if (tile == board.getPlayer1().getId()) {
                        player1Area++;
                    } else if (tile == board.getPlayer2().getId()) {
                        player2Area++;
                    }
                }
            }
        }
        board.getPlayer1().setArea(player1Area);
        board.getPlayer2().setArea(player2Area);

        if (player1Area + player2Area == Board.BOARD_HEIGHT * Board.BOARD_WIDTH * 4) {
            if (player1Area > player2Area) {
                board.setWinningPlayer(board.getPlayer1());
            } else if (player1Area < player2Area) {
                board.setWinningPlayer(board.getPlayer2());
            }
            endGame();
        }
    }

    private void captureAxis(boolean horizontal) {
        int closeSide, farSide, perpLength, paraLength;
        if (horizontal) {
            closeSide = 2;
            farSide = 0;
            perpLength = Board.BOARD_HEIGHT;
            paraLength = Board.BOARD_WIDTH;
        } else {
            closeSide = 1;
            farSide = 3;
            perpLength = Board.BOARD_WIDTH;
            paraLength = Board.BOARD_HEIGHT;
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

                int para, perp, edgeType;
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
                int[] square;
                if (horizontal) {
                    square = board.getTiles().get(perp).get(para);
                } else {
                    square = board.getTiles().get(para).get(perp);
                }

                boolean closeInLoop = edgeTypes[1] != inLoop;

                if (edgeTypes[0] == edgeTypes[2]) {
                    if (closeInLoop) {
                        square[closeSide] = player.getId();
                        square[farSide] = player.getId();
                    }
                } else {
                    if (closeInLoop) {
                        square[closeSide] = player.getId();
                    } else {
                        square[farSide] = player.getId();
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
        return player.getSelectedBot();
    }

    public void changeBot() {
        if (canMove())
        player.changeBot();
    }

    public void moveBot(int x, int y) {
        if (canMove()) {
            int newX = player.getSelectedBot().getX() + x;
            int newY = player.getSelectedBot().getY() + y;
            if (0 <= newX && newX <= Board.BOARD_WIDTH && 0 <= newY && newY <= Board.BOARD_HEIGHT) {
                if (player.getSelectedBot().move(x, y)) {
                    this.update();
                    this.board.changePlayer(player);
                }
            }
        }
    }

    private boolean canMove() {
        return playerName.equals(board.getCurrentPlayer().getName());
    }

    public Board getBoard() {
        return board;
    }

    private void endGame() {
        Main.endGame();
    }
}

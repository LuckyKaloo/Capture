package application.model.logic;

import com.google.firebase.database.FirebaseDatabase;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Board implements Serializable {
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    private final ArrayList<ArrayList<int[]>> tiles = new ArrayList<>();

    public final transient static int BOARD_WIDTH = 15;
    public final transient static int BOARD_HEIGHT = 10;

    public Board() {
        this("Player 1", "Player 2");
    }

    public Board(String name1, String name2) {
        this.player1 = new Player(name1, new Vertex(0, 0));
        this.player2 = new Player(name2, new Vertex(BOARD_WIDTH, BOARD_HEIGHT));
        this.currentPlayer = this.player1;

        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            ArrayList<int[]> rowTiles = new ArrayList<>();

            for (int x = 0; x <= BOARD_WIDTH; x++) {
                if (y < BOARD_HEIGHT &&  x < BOARD_WIDTH) {
                    int[] square = new int[4];
                    for (int side = 0; side < 4; side++) {
                        square[side] = -1;
                    }
                    rowTiles.add(square);
                }
            }

            tiles.add(rowTiles);
        }
    }

    public ArrayList<ArrayList<int[]>> getTiles() {
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

    public void changePlayer(Player player) {
        if (player.getName().equals(this.player1.getName())) {
            this.currentPlayer = this.player2;
        } else if (player.getName().equals(this.player2.getName())) {
            this.currentPlayer = this.player1;
        }
    }

    public String toData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            return new String(Base64.getEncoder().encode(baos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Board loadData(String str) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(Base64.getDecoder().decode(str.getBytes())));
            return (Board) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

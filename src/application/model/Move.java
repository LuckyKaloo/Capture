package application.model;

import java.io.*;
import java.util.Base64;

public class Move implements Serializable {
    private Board board;

    public Move(Board board) {
        this.board = board;
    }

    public Move(String str) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(str.getBytes())));
        Move move = (Move) ois.readObject();
        this.board = move.board;
    }

    public String getMove() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            return new String(Base64.getEncoder().encodeToString(baos.toByteArray()));
        } catch (IOException e) {
            return null;
        }
    }
}

package application.controller;

import application.model.logic.Board;
import com.google.firebase.database.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    private MFXButton joinBtn;
    @FXML
    private MFXButton newBtn;
    @FXML
    private MFXTextField nameTf;
    @FXML
    private MFXListView<String> gamesLv;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if (child.getKey().equals("games")) {
                        for (DataSnapshot player: child.getChildren()) {
                            gamesLv.getItems().add(player.getKey());
                        }
                    }
                }
                gamesLv.refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @FXML
    private void newLogin() {
        Main.newLogin();
    }

    @FXML
    private void newGame() {
        newBtn.setDisable(true);
        joinBtn.setDisable(true);
        if (!nameTf.getText().equals("")) {
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    handleNew(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    newBtn.setDisable(false);
                    joinBtn.setDisable(false);
                }
            });
        }
    }

    private void handleNew(DataSnapshot snapshot) {
        String name = nameTf.getText() + "-" + nameId(snapshot);

        HashMap<String, String> root = new HashMap<>();
        root.put("player1", name);
        root.put("player2", "");
        root.put("board", "");
        root.put("started", "false");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("games").child(name);
        ref.setValue(root, ((databaseError, databaseReference) -> {}));

        addPlayer(name);
        Platform.runLater(() -> gamesLv.getItems().add(name));

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = (String) dataSnapshot.getValue();
                if (!str.equals("")) {
                    ref.removeEventListener(this);
                    Board board = Board.loadData(str);
                    Main.startGame(board, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.child("board").addValueEventListener(listener);
    }


    @FXML
    private void joinGame() {
        newBtn.setDisable(true);
        joinBtn.setDisable(true);
        if (!nameTf.getText().equals("")) {
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    handleJoin(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    newBtn.setDisable(false);
                    joinBtn.setDisable(false);
                }
            });
        }
    }

    private void handleJoin(DataSnapshot snapshot) {
        String name = nameTf.getText() + "-" + nameId(snapshot);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("games").child(gamesLv.getSelectionModel().getSelectedItem());
        ref.child("player2").setValue(name, ((databaseError, databaseReference) -> {}));
        ref.child("started").setValue("true", ((databaseError, databaseReference) -> {}));


        DataSnapshot dataSnapshot = snapshot.child("games").child(gamesLv.getSelectionModel().getSelectedItem());
        DataSnapshot player1 = dataSnapshot.child("player1");
        DataSnapshot player2 = dataSnapshot.child("player2");

        System.out.println(dataSnapshot.getValue());

        if (player1 != null && player2 != null) {
            Board board = new Board((String) player1.getValue(), (String) player2.getValue());
            System.out.println(board.toData());
            ref.child("board").setValue(board.toData(), ((databaseError, databaseReference) -> {}));
            Main.startGame(board, false);
        }

        addPlayer(name);

        Platform.runLater(() -> gamesLv.getItems().remove(gamesLv.getSelectionModel().getSelectedItem()));
    }

    private int nameId(DataSnapshot snapshot) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (DataSnapshot child: snapshot.getChildren()) {
            if (child.getKey().equals("players")) {
                for (DataSnapshot player: child.getChildren()) {
                    String[] nameInfo = player.getKey().split("-");
                    if (nameInfo[0].equals(nameTf.getText())) {
                        ids.add(Integer.parseInt(nameInfo[1]));
                    }
                }
            }
        }
        Collections.sort(ids);
        int id = 0;
        for (int takenId: ids) {
            if (id == takenId) {
                id++;
            } else {
                break;
            }
        }
        return id;
    }

    private void addPlayer(String name) {
        FirebaseDatabase.getInstance().getReference().child("players").child(name).setValue("", ((databaseError, databaseReference) -> {}));
    }
}

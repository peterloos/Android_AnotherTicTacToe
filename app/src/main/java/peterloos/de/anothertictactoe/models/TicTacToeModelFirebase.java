package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;
import peterloos.de.anothertictactoe.interfaces.OnPlayersChangedListener;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 28.01.2018.
 */

class Cell {

    private String state;

    public Cell() {
        // default constructor required for Firebase
    }

    public Cell(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "State: ==> " + this.state;
    }
}

class Player {

    private String name;

    public Player() {
        // default constructor required for Firebase
    }

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name: " + this.name;
    }
}

public class TicTacToeModelFirebase implements ITicTacToe {

    // Firebase utils
    private FirebaseDatabase database;
    private DatabaseReference refBoard;
    private DatabaseReference refPlayers;

    // member data
    private Context context;
    private HashMap<String, String> board;
    private boolean isFirstPlayer;
    private GameState gameState;

    // TODO: Das sollte ein array von Objekten sein !!!!!!!!!!!!!!!1
    private String[] playerNames;
    private String[] playerKeys;
    private String currentPlayer;

    // listeners
    private OnBoardChangedListener boardListener;
    private OnPlayersChangedListener playersListener;

    // c'tor
    public TicTacToeModelFirebase(Context context) {

        this.context = context;
        this.board = new HashMap<String, String>();

        // init access to database
        this.database = FirebaseDatabase.getInstance();
        this.refBoard = database.getReference("board");
        this.refBoard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    TicTacToeModelFirebase.this.evaluateBoardSnapshot(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // TODO: ERror handling
                Log.w(Globals.Tag, "Failed to read value.", error.toException());
            }
        });

        this.refPlayers = database.getReference("players");

//        this.refPlayers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//
//                    TicTacToeModelFirebase.this.evaluatePlayersSnapshot2(dataSnapshot);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                // TODO: ERror handling
//                Log.w(Globals.Tag, "Failed to read value.", error.toException());
//            }
//        });

        this.refPlayers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // a new player has been added
                Player player = dataSnapshot.getValue(Player.class);
                String name = player.getName();

                Log.d(Globals.Tag, "onChildAdded: " + player.toString() + " [" + dataSnapshot.getKey() + "]");

                if (TicTacToeModelFirebase.this.playerNames[0] == null) {

                    TicTacToeModelFirebase.this.playerKeys[0] = dataSnapshot.getKey();
                    TicTacToeModelFirebase.this.playerNames[0] = name;
                } else if (TicTacToeModelFirebase.this.playerNames[1] == null) {

                    TicTacToeModelFirebase.this.playerKeys[1] = dataSnapshot.getKey();
                    TicTacToeModelFirebase.this.playerNames[1] = name;
                }

                // fire notification
                if (TicTacToeModelFirebase.this.playersListener != null) {

                    TicTacToeModelFirebase.this.playersListener.playersChanged(
                            (TicTacToeModelFirebase.this.playerNames[0] == null) ? "" : TicTacToeModelFirebase.this.playerNames[0],
                            (TicTacToeModelFirebase.this.playerNames[1] == null) ? "" : TicTacToeModelFirebase.this.playerNames[1]);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // removing current player
                Player player = dataSnapshot.getValue(Player.class);
                String name = player.getName();

                Log.d(Globals.Tag, "onChildRemoved: " + name + " [" + dataSnapshot.getKey() + "]");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(Globals.Tag, "refPlayers:onCancelled", databaseError.toException());
                Toast.makeText(TicTacToeModelFirebase.this.context, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        this.initGame();

        // TODO: Warum steht das nicht in initGame
//        this.player1 = "";
//        this.player2 = "";
//        this.currentPlayer = "";

        this.playerNames = new String[2];
        this.playerKeys = new String[2];
        this.currentPlayer = "";
    }

    // implementation of interface 'ITicTacToe'

    @Override
    public void setOnBoardChangedListener(OnBoardChangedListener listener) {

        this.boardListener = listener;
    }

    @Override
    public void setOnPlayersChangedListener(OnPlayersChangedListener listener) {

        this.playersListener = listener;
    }

    @Override
//    public void registerPlayer(String name) {
//
//        Player player = new Player(name);
//
//        if (this.player1.equals("")) {
//
//            this.refPlayers.child("player_01").setValue(player);
//        }
//        else {
//
//            this.refPlayers.child("player_02").setValue(player);
//        }
//    }


    // KÖNNTE GEHEN ---- ABER FALSHER ANSATZ !!!!
//    public void registerPlayer(String name) {
//
//
//        if (this.currentPlayer.equals("")) {
//
//            Player player = new Player(name);
//
//            DatabaseReference ref = this.refPlayers.push();
//
//            if (this.playerKeys[0] == null) {
//
//                this.playerKeys[0] = ref.getKey();
//                this.playerNames[0] = name;
//            }
//            else {
//
//                this.playerKeys[1] = ref.getKey();
//                this.playerNames[1] = name;
//            }
//
//            ref.setValue(player);
//        }
//    }


    public void registerPlayer(String name) {

        if (this.currentPlayer.equals("")) {

            this.currentPlayer = name;

            Player player = new Player(name);
            this.refPlayers.push().setValue(player);
        }
    }

    @Override
    public void unregisterPlayer() {

        if (this.playerNames[0] != null && this.playerNames[0].equals(this.currentPlayer)) {

            this.currentPlayer = "";
            this.refPlayers.child(this.playerKeys[0]).removeValue();

        } else if (this.playerNames[1] != null && this.playerNames[1].equals(this.currentPlayer)) {

            this.currentPlayer = "";
            this.refPlayers.child(this.playerKeys[1]).removeValue();
        }
    }

    @Override
    public void initGame() {

        // need at first an empty hash map
        for (int row = 1; row <= Dimension; row++) {
            for (int col = 1; col <= Dimension; col++) {
                String key = this.cellToKey(row, col);
                this.board.put(key, GameStone.Empty.toString());
            }
        }

        // now initialize remote database
        for (int row = 1; row <= Dimension; row++) {
            for (int col = 1; col <= Dimension; col++) {
                this.setStoneRemote(row, col, GameStone.Empty);
            }
        }

        this.isFirstPlayer = true;
        this.gameState = GameState.Active;

        if (this.boardListener != null) {
            this.boardListener.clearBoard();
        }
    }

    @Override
    public GameStone getStoneAt(int row, int col) {

        String key = this.cellToKey(row, col);
        String value = this.board.get(key);
        return GameStone.valueOf(value);
    }

    @Override
    public boolean setStone(int row, int col) {

        String key = this.cellToKey(row, col);

        // ignore this request - current game over or not initialized
        if (this.gameState == GameState.Inactive)
            return false;

        // is there already a stone, ignore call
        if (!isFieldEmpty(key))
            return false;

        // set stone on board
        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        GameStone stone = (this.isFirstPlayer) ? GameStone.X : GameStone.O;
        this.setStoneRemote(row, col, stone);
        this.isFirstPlayer = !this.isFirstPlayer;

        // check for end of game
        // TODO ...

        return true;
    }

    // private helper methods
    private void evaluateBoardSnapshot(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Log.d(Globals.Tag, "    Key:   " + data.getKey());
            for (DataSnapshot subData : data.getChildren()) {

                if (subData.getKey().equals("col1")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col1: " + cell.toString());
                    this.onCellChanged(data.getKey(), "col1", cell.getState());
                } else if (subData.getKey().equals("col2")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col2: " + cell.toString());
                    this.onCellChanged(data.getKey(), "col2", cell.getState());
                } else if (subData.getKey().equals("col3")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col3: " + cell.toString());
                    this.onCellChanged(data.getKey(), "col3", cell.getState());
                }
            }
        }
    }

    private void onCellChanged(String row, String col, String stone) {

        String key = this.cellToKey(row, col);
        String value = this.board.get(key);

        GameStone oldStone = GameStone.valueOf(value);
        GameStone newStone = GameStone.valueOf(stone);

        if (oldStone != newStone) {

            // enter new stone into hash map of model
            this.board.put(key, stone);

            // fire notification
            if (this.boardListener != null) {

                this.boardListener.stoneChangedAt(
                        this.rowToInt(row),
                        this.colToInt(col),
                        GameStone.valueOf(stone));
            }
        }
    }

    private String cellToKey(int row, int col) {

        return Integer.toString((row - 1) * Dimension + col);
    }

    private String cellToKey(String srow, String scol) {

        int row = this.rowToInt(srow);
        int col = this.colToInt(scol);
        return this.cellToKey(row, col);
    }

    private boolean isFieldEmpty(String key) {

        String value = this.board.get(key);
        return value.equals(GameStone.Empty.toString());
    }

    private int rowToInt(String row) {

        return row.charAt(3) - '0';
    }

    private int colToInt(String row) {

        return row.charAt(3) - '0';
    }

    private void setStoneRemote(int r, int c, GameStone stone) {

        String row = "row" + r;
        String col = "col" + c;
        Cell cell = new Cell(stone.toString());
        this.refBoard.child(row).child(col).setValue(cell);
    }

//    private void evaluatePlayersSnapshot(DataSnapshot dataSnapshot) {
//
//        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//            Log.d(Globals.Tag, "    Key:   " + data.getKey());
//
//            if (data.getKey().equals("player_01")) {
//
//                Player player = data.getValue(Player.class);
//                this.player1 = player.getName();
//                Log.d(Globals.Tag, "        NAME_01:  " + player.getName());
//            } else if (data.getKey().equals("player_02")) {
//
//                Player player = data.getValue(Player.class);
//                this.player2 = player.getName();
//                Log.d(Globals.Tag, "        NAME_02:  " + player.getName());
//            }
//        }
//
//        // fire notification
//        if (this.playersListener != null) {
//
//            this.playersListener.playersChanged(player1, player2);
//        }
//    }

    private void evaluatePlayersSnapshot2(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Player player = data.getValue(Player.class);
            String name = player.getName();

            if (this.playerNames[0] == null) {

                this.playerKeys[0] = data.getKey();
                this.playerNames[0] = name;
            } else if (this.playerNames[1] == null) {

                this.playerKeys[1] = data.getKey();
                this.playerNames[1] = name;
            }

            Log.d(Globals.Tag, "      Name:  " + player.getName() + " [" + data.getKey() + "]");
        }

        // fire notification
        if (this.playersListener != null) {

            this.playersListener.playersChanged(
                    (this.playerNames[0] == null) ? "" : this.playerNames[0],
                    (this.playerNames[1] == null) ? "" : this.playerNames[1]);
        }
    }
}

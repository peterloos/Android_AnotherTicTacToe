package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 28.01.2018.
 */

class Cell
{
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

class Player
{
    private String name;

    public Player() {
        // default constructor required for Firebase
    }

    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


    @Override
    public String toString() {
        return "Name: ==> " + this.name;
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
    private boolean firstPlayer;
    private GameState gameState;
    private OnBoardChangedListener listener;

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
        this.refPlayers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    TicTacToeModelFirebase.this.evaluatePlayersSnapshot(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // TODO: ERror handling
                Log.w(Globals.Tag, "Failed to read value.", error.toException());
            }
        });

        this.initGame();
    }

    // public interface
    @Override
    public void initGame() {

        // need at first an empty hashmap
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

        this.firstPlayer = true;
        this.gameState = GameState.Active;

        if (this.listener != null) {
            this.listener.clearBoard();
        }
    }

    // implementation of interface 'ITicTacToe'
    @Override
    public void setOnBoardChangedListener(OnBoardChangedListener listener) {

        this.listener = listener;
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
        GameStone stone = (this.firstPlayer) ? GameStone.X : GameStone.O;
        this.setStoneRemote(row, col, stone);
        this.firstPlayer = !this.firstPlayer;

        return true;
    }

    public void evaluateBoardSnapshot(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Log.d(Globals.Tag, "    Key:   " + data.getKey());
            for (DataSnapshot subData : data.getChildren()) {

                if (subData.getKey().equals("col1")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col1: " + cell.toString());
                    this.onCellChanged (data.getKey(), "col1", cell.getState());
                }
                else if (subData.getKey().equals("col2")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col2: " + cell.toString());
                    this.onCellChanged (data.getKey(), "col2", cell.getState());
                }
                else if (subData.getKey().equals("col3")) {

                    Cell cell = subData.getValue(Cell.class);
                    // Log.d(Globals.Tag, "        Value at col3: " + cell.toString());
                    this.onCellChanged (data.getKey(), "col3", cell.getState());
                }
            }
        }
    }

    private void onCellChanged (String row, String col, String stone) {

        String key = this.cellToKey(row, col);
        String value = this.board.get(key);

        GameStone oldStone = GameStone.valueOf(value);
        GameStone newStone = GameStone.valueOf(stone);

        if (oldStone != newStone) {

            // enter new stone into hash map of model
            this.board.put(key, stone);

            // fire notification
            if (this.listener != null) {

                this.listener.stoneChangedAt(
                        this.rowToInt(row),
                        this.colToInt(col),
                        GameStone.valueOf(stone));
            }
        }
    }

    // private helper methods
    private String cellToKey (int row, int col) {

        return Integer.toString ((row - 1) * Dimension + col);
    }

    private String cellToKey (String srow, String scol) {

//        int row = srow.charAt(3) - '0';
//        int col = scol.charAt(3) - '0';

        int row = this.rowToInt (srow);
        int col = this.colToInt (scol);
        return this.cellToKey (row, col);
    }

    private boolean isFieldEmpty(String key) {

        String value = this.board.get(key);
        return value.equals(GameStone.Empty.toString());
    }

    private int rowToInt (String row) {

        return row.charAt(3) - '0';
    }

    private int colToInt (String row) {

        return row.charAt(3) - '0';
    }

    public void setStoneRemote(int r, int c, GameStone stone) {

        String row = "row" + r;
        String col = "col" + c;
        Cell cell = new Cell (stone.toString());
        this.refBoard.child(row).child(col).setValue(cell);
    }

//    public void evaluatePlayersSnapshot(DataSnapshot dataSnapshot) {
//
//        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//            Log.d(Globals.Tag, "    Key:   " + data.getKey());
//            for (DataSnapshot subData : data.getChildren()) {
//
//                if (subData.getKey().equals("player_01")) {
//
//                    String name = subData.getValue(String.class);
//                    Log.d(Globals.Tag, "        NAME_01:  " + name);
//                    // this.onCellChanged (data.getKey(), "col1", cell.getState());
//                }
//                else if (subData.getKey().equals("player_02")) {
//
//                    String name = subData.getValue(String.class);
//                    Log.d(Globals.Tag, "        NAME_02:  " + name);
//                    // this.onCellChanged (data.getKey(), "col2", cell.getState());
//                }
//            }
//        }
//    }

    public void evaluatePlayersSnapshot(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Log.d(Globals.Tag, "    Key:   " + data.getKey());

            if (data.getKey().equals("player_01")) {

                Player player = data.getValue(Player.class);
                Log.d(Globals.Tag, "        NAME_01:  " + player.getName());
            }
            else if (data.getKey().equals("player_02")) {

                Player player = data.getValue(Player.class);
                Log.d(Globals.Tag, "        NAME_02:  " + player.getName());
            }
        }
    }
}
